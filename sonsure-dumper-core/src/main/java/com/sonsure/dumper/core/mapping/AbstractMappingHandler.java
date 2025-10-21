/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.mapping;

import com.sonsure.dumper.common.utils.ClassUtils;
import com.sonsure.dumper.common.utils.NameUtils;
import com.sonsure.dumper.common.utils.StrUtils;
import com.sonsure.dumper.core.command.build.CacheEntityClassWrapper;
import com.sonsure.dumper.core.command.build.CmdParameter;
import com.sonsure.dumper.core.command.build.CommandBuildHelper;
import com.sonsure.dumper.core.command.build.EntityClassFieldWrapper;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liyd
 */
@Slf4j
public abstract class AbstractMappingHandler implements MappingHandler, TablePrefixSupportHandler {

    /**
     * value需要native内容前后包围符号
     */
    public static final String REFERENCE_CLASS_TOKEN = "`";

    /**
     * 主键属性后缀
     */
    protected static final String PRI_FIELD_SUFFIX = CommandBuildHelper.PRI_FIELD_SUFFIX;

    /**
     * class不存在时是否失败 (抛出异常)
     */
    @Setter
    @Getter
    protected boolean failOnMissingClass;

    /**
     * 表前缀定义, 如 com.sonsure 开头的class表名统一加ss_  com.sonsure.User -> ss_user
     */
    @Getter
    @Setter
    protected Map<String, String> tablePrefixMap;

    /**
     * The Class loader.
     */
    @Getter
    protected ClassLoader classLoader;

    /**
     * load的class
     */
    protected Map<String, Class<?>> loadedClass;

    /**
     * 扫描的包
     */
    protected String modelPackages;

    /**
     * 类名称映射
     */
    @Getter
    protected Map<String, Class<?>> classMapping;

    /**
     * 自定义类名称映射
     */
    @Getter
    @Setter
    protected Map<String, Class<?>> customClassMapping;

    public AbstractMappingHandler(String modelPackages, ClassLoader classLoader) {
        this.failOnMissingClass = true;
        loadedClass = new ConcurrentHashMap<>();
        classMapping = new ConcurrentHashMap<>();
        customClassMapping = new ConcurrentHashMap<>();
        tablePrefixMap = new ConcurrentHashMap<>();
        this.modelPackages = modelPackages;
        this.classLoader = classLoader == null ? getClass().getClassLoader() : classLoader;
        this.scanClassMapping();
    }

    public void addClassMapping(Class<?> clazz) {
        final String name = clazz.getSimpleName();
        final Class<?> existCls = classMapping.get(name);
        if (existCls == null) {
            classMapping.put(name, clazz);
        } else if (existCls != clazz) {
            throw new SonsureJdbcException("Class name冲突:" + existCls.getName() + ", " + clazz.getName());
        }
    }

    public void addClassMapping(Set<Class<?>> classes) {
        if (classes == null || classes.isEmpty()) {
            return;
        }
        for (Class<?> aClass : classes) {
            this.addClassMapping(aClass);
        }
    }

    public void addTablePrefix(String prefix, String... packages) {
        for (String aPackage : packages) {
            this.tablePrefixMap.put(aPackage, prefix);
        }
    }

    @Override
    public String getTable(String className, List<CmdParameter> parameters) {
        Class<?> tableClass = this.getTableClass(className);
        return tableClass == null ? className : this.getTable(tableClass, parameters);
    }

    @Override
    public String getColumn(String clazzName, String fieldName) {
        Class<?> tableClass = this.getTableClass(clazzName);
        return tableClass == null ? fieldName : this.getColumn(tableClass, fieldName);
    }

    @Override
    public String getTable(Class<?> entityClass, List<CmdParameter> parameters) {
        CacheEntityClassWrapper entityClassWrapper = new CacheEntityClassWrapper(entityClass);
        Object annotation = entityClassWrapper.getClassAnnotation();
        String tableName;
        if (annotation != null) {
            tableName = CommandBuildHelper.getTableAnnotationName(annotation);
        } else {
            if (tablePrefixMap == null) {
                //默认Java属性的骆驼命名法转换回数据库下划线“_”分隔的格式
                tableName = NameUtils.getUnderlineName(entityClass.getSimpleName());
            } else {
                String tablePreFix = this.getTablePrefix(entityClass.getName());
                tableName = tablePreFix + NameUtils.getUnderlineName(entityClass.getSimpleName());
            }
        }

        if (StrUtils.isBlank(tableName)) {
            throw new SonsureJdbcException("没有找到对应的表名:" + entityClass);
        }
        return tableName;
    }

    @Override
    public String getPkField(Class<?> entityClass) {
        CacheEntityClassWrapper entityClassWrapper = new CacheEntityClassWrapper(entityClass);
        return entityClassWrapper.getPrimaryKeyField().getFieldName();
    }

    @Override
    public String getColumn(Class<?> entityClass, String fieldName) {
        CacheEntityClassWrapper entityClassWrapper = new CacheEntityClassWrapper(entityClass);
        EntityClassFieldWrapper fieldWrapper = entityClassWrapper.getEntityField(fieldName);

        //count(*) as num  num是没有的
        if (fieldWrapper == null) {
            return fieldName;
        }

        Object columnAnnotation = fieldWrapper.getColumnAnnotation();
        if (columnAnnotation != null) {
            return fieldWrapper.getFieldAnnotationColumn();
        }
        return NameUtils.getUnderlineName(fieldName);
    }

    @Override
    public String getField(Class<?> clazz, String columnName) {
        CacheEntityClassWrapper entityClassWrapper = new CacheEntityClassWrapper(clazz);
        EntityClassFieldWrapper mappedFieldWrapper = entityClassWrapper.getMappedField(columnName);
        if (mappedFieldWrapper != null) {
            return mappedFieldWrapper.getFieldName();
        }
        return NameUtils.getCamelName(columnName);
    }

    @Override
    public String getTablePrefix(String classPackage) {
        for (Map.Entry<String, String> entry : tablePrefixMap.entrySet()) {
            if (classPackage.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return "";
    }

    protected Class<?> getTableClass(String className) {

        if (StrUtils.isBlank(className)) {
            throw new SonsureJdbcException("className不能为空");
        }
        if (className.startsWith(REFERENCE_CLASS_TOKEN) && className.endsWith(REFERENCE_CLASS_TOKEN)) {
            className = className.substring(REFERENCE_CLASS_TOKEN.length(), className.length() - REFERENCE_CLASS_TOKEN.length());
        }
        Class<?> clazz = null;
        if (className.contains(".")) {
            clazz = loadedClass.get(className);
            if (clazz == null) {
                clazz = this.loadClass(className);
                loadedClass.put(className, clazz);
            }
        }
        if (clazz == null && !customClassMapping.isEmpty()) {
            clazz = customClassMapping.get(className);
        }
        if (clazz == null && !classMapping.isEmpty()) {
            clazz = classMapping.get(className);
        }
        if (clazz == null && this.isFailOnMissingClass()) {
            throw new SonsureJdbcException("没有找到对应的class:" + className);
        }

        return clazz;
    }

    /**
     * 初始化类，容忍多次初始化无不良后果，并不需要严格的线程安全，
     */
    protected void scanClassMapping() {

        if (StrUtils.isBlank(this.modelPackages)) {
            return;
        }
        String[] pks = StrUtils.split(modelPackages, ",");
        for (String pk : pks) {
            List<Class<?>> classes = ClassUtils.scanClasses(pk);
            for (Class<?> clazz : classes) {
                String simpleName = clazz.getSimpleName();
                if (classMapping.containsKey(simpleName)) {
                    log.warn("短类名相同，使用时请自定义短类名或使用完整类名:class1:{},class2:{}", classMapping.get(simpleName), clazz);
                } else {
                    classMapping.put(simpleName, clazz);
                }
            }
        }
    }

    protected Class<?> loadClass(String className) {
        try {
            return getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new SonsureJdbcException("加载class失败:" + className);
        }
    }

}

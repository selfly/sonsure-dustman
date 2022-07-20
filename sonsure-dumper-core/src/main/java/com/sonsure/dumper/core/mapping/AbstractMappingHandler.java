/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.mapping;

import com.sonsure.commons.spring.scan.ClassPathBeanScanner;
import com.sonsure.commons.utils.NameUtils;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import com.sonsure.dumper.core.management.ModelClassCache;
import com.sonsure.dumper.core.management.ModelClassMeta;
import com.sonsure.dumper.core.management.ModelFieldMeta;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liyd
 */
public abstract class AbstractMappingHandler implements MappingHandler {

    protected static final Logger LOG = LoggerFactory.getLogger(MappingHandler.class);

    /**
     * value需要native内容前后包围符号
     */
    public static final String REFERENCE_CLASS_TOKEN = "`";

    /**
     * 主键属性后缀
     */
    protected static final String PRI_FIELD_SUFFIX = "Id";

    /**
     * class不存在时是否失败 (抛出异常)
     */
    protected boolean failOnMissingClass;

    /**
     * 表前缀定义, 如 com.sonsure 开头的class表名统一加ss_  com.sonsure.User -> ss_user
     */
    protected Map<String, String> tablePreFixMap;

    /**
     * The Class loader.
     */
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
    protected Map<String, Class<?>> classMapping;

    /**
     * 自定义类名称映射
     */
    protected Map<String, Class<?>> customClassMapping;

    public AbstractMappingHandler(String modelPackages) {
        this(modelPackages, null);
    }

    public AbstractMappingHandler(String modelPackages, ClassLoader classLoader) {
        this.failOnMissingClass = true;
        loadedClass = new ConcurrentHashMap<>();
        classMapping = new ConcurrentHashMap<>();
        customClassMapping = new ConcurrentHashMap<>();
        this.modelPackages = modelPackages;
        this.classLoader = classLoader == null ? getClass().getClassLoader() : classLoader;
        this.init();
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

    @Override
    public String getTable(String className, Map<String, Object> params) {
        Class<?> tableClass = this.getTableClass(className);
        return tableClass == null ? className : this.getTable(tableClass, params);
    }

    @Override
    public String getColumn(String clazzName, String fieldName) {
        Class<?> tableClass = this.getTableClass(clazzName);
        return tableClass == null ? fieldName : this.getColumn(tableClass, fieldName);
    }

    @Override
    public String getTable(Class<?> entityClass, Map<String, Object> params) {

        ModelClassMeta classMeta = ModelClassCache.getClassMeta(entityClass);
        Object annotation = classMeta.getAnnotation();
        String tableName = null;
        if (annotation != null) {
            tableName = ModelClassCache.getTableAnnotationName(annotation);
        } else {
            if (tablePreFixMap == null) {
                //默认Java属性的骆驼命名法转换回数据库下划线“_”分隔的格式
                tableName = NameUtils.getUnderlineName(entityClass.getSimpleName());
            } else {
                String tablePreFix = "";
                for (Map.Entry<String, String> entry : tablePreFixMap.entrySet()) {
                    if (StringUtils.startsWith(entityClass.getName(), entry.getKey())) {
                        tablePreFix = entry.getValue();
                        break;
                    }
                }
                tableName = tablePreFix + NameUtils.getUnderlineName(entityClass.getSimpleName());
            }
        }

        if (StringUtils.isBlank(tableName)) {
            throw new SonsureJdbcException("没有找到对应的表名:" + entityClass);
        }
        return tableName;
    }

    @Override
    public String getPkField(Class<?> entityClass) {

        ModelClassMeta classMeta = ModelClassCache.getClassMeta(entityClass);
        ModelFieldMeta pkFieldMeta = classMeta.getPkFieldMeta();
        if (pkFieldMeta != null) {
            return pkFieldMeta.getName();
        }
        Object annotation = classMeta.getAnnotation();
        if (annotation != null) {
            String table = ModelClassCache.getTableAnnotationName(annotation);
            String camelName = NameUtils.getCamelName(table);
            return camelName + PRI_FIELD_SUFFIX;
        }
        String firstLowerName = NameUtils.getFirstLowerName(entityClass.getSimpleName());
        //主键以类名加上“Id” 如user表主键属性即userId
        return firstLowerName + PRI_FIELD_SUFFIX;
    }

    @Override
    public String getColumn(Class<?> entityClass, String fieldName) {
        ModelFieldMeta classFieldMeta = ModelClassCache.getClassFieldMeta(entityClass, fieldName);

        //count(*) as num  num是没有的
        if (classFieldMeta == null) {
            return fieldName;
        }

        Object columnAnnotation = classFieldMeta.getColumnAnnotation();
        if (columnAnnotation != null) {
            return ModelClassCache.getColumnAnnotationName(columnAnnotation);
        }
        return NameUtils.getUnderlineName(fieldName);
    }

    @Override
    public String getField(Class<?> clazz, String columnName) {
        ModelFieldMeta mappedFieldMeta = ModelClassCache.getMappedFieldMeta(clazz, columnName);
        if (mappedFieldMeta != null) {
            return mappedFieldMeta.getName();
        }
        return NameUtils.getCamelName(columnName);
    }

    /**
     * 初始化类，容忍多次初始化无不良后果，并不需要严格的线程安全，
     */
    protected void init() {

        if (StringUtils.isBlank(this.modelPackages)) {
            return;
        }
        String[] pks = StringUtils.split(modelPackages, ",");
        for (String pk : pks) {
            List<String> classes = ClassPathBeanScanner.scanClasses(pk, getClassLoader());
            for (String clazz : classes) {

                int index = StringUtils.lastIndexOf(clazz, ".");
                String simpleName = StringUtils.substring(clazz, index + 1);

                if (classMapping.containsKey(simpleName)) {
                    LOG.warn("短类名相同，使用时请自定义短类名或使用完整类名:class1:{},class2:{}", classMapping.get(simpleName), clazz);
                } else {
                    Class<?> aClass = this.loadClass(clazz);
                    classMapping.put(simpleName, aClass);
                }
            }
        }
    }

    protected Class<?> getTableClass(String className) {

        if (StringUtils.isBlank(className)) {
            throw new SonsureJdbcException("className不能为空");
        }
        if (StringUtils.startsWith(className, REFERENCE_CLASS_TOKEN) && StringUtils.endsWith(className, REFERENCE_CLASS_TOKEN)) {
            className = StringUtils.substring(className, REFERENCE_CLASS_TOKEN.length(), className.length() - REFERENCE_CLASS_TOKEN.length());
        }
        Class<?> clazz = null;
        if (StringUtils.indexOf(className, ".") != -1) {
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
        if (clazz == null && failOnMissingClass) {
            throw new SonsureJdbcException("没有找到对应的class:" + className);
        }

        return clazz;
    }

    protected Class<?> loadClass(String className) {
        try {
            return getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new SonsureJdbcException("加载class失败:" + className);
        }
    }

    public boolean isFailOnMissingClass() {
        return failOnMissingClass;
    }

    public void setFailOnMissingClass(boolean failOnMissingClass) {
        this.failOnMissingClass = failOnMissingClass;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public String getModelPackages() {
        return modelPackages;
    }

    public Map<String, Class<?>> getClassMapping() {
        return classMapping;
    }

    public Map<String, Class<?>> getCustomClassMapping() {
        return customClassMapping;
    }

    public void setCustomClassMapping(Map<String, Class<?>> customClassMapping) {
        this.customClassMapping = customClassMapping;
    }

    public Map<String, String> getTablePreFixMap() {
        return tablePreFixMap;
    }

    public void setTablePreFixMap(Map<String, String> tablePreFixMap) {
        this.tablePreFixMap = tablePreFixMap;
    }
}

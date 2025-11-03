/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.mapping;

import com.sonsure.dumper.common.utility.GenericResourcePatternResolver;
import com.sonsure.dumper.common.utility.GenericResourcePatternResolverImpl;
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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liyd
 */
@Slf4j
@Getter
@Setter
public class MappingHandlerImpl implements MappingHandler {

    /**
     * 全类名需要前后包围符号
     */
    public static final String REFERENCE_CLASS_TOKEN = "`";
    public static final String DOT = ".";

    private static final GenericResourcePatternResolver CLASS_RESOLVER = new GenericResourcePatternResolverImpl();

    /**
     * class不存在时是否失败 (抛出异常)
     */
    protected boolean failOnMissingClass = false;

    /**
     * 扫描的包
     */
    protected Set<String> scanPackages = new HashSet<>(16);

    /**
     * 表前缀定义, 如 com.sonsure 开头的class表名统一加ss_  com.sonsure.User -> ss_user
     */
    protected Map<String, String> tablePrefixMapping = new ConcurrentHashMap<>(16);

    /**
     * 类名称映射
     */
    protected Map<String, Class<?>> classMapping;

    @Override
    public void registerClassMapping(String name, Class<?> clazz) {
        this.initScanClassMapping();
        final Class<?> existCls = classMapping.get(name);
        if (existCls == null) {
            classMapping.put(name, clazz);
        } else if (existCls != clazz) {
            throw new SonsureJdbcException("Class name冲突:" + existCls.getName() + ", " + clazz.getName());
        }
    }

    public void addScanPackages(String... scanPackages) {
        Collections.addAll(this.scanPackages, scanPackages);
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
        Object annotation = entityClassWrapper.getEntityAnnotation();
        String tableName;
        if (annotation != null) {
            tableName = CommandBuildHelper.getTableAnnotationName(annotation);
        } else {
            String tablePreFix = this.getTablePrefix(entityClass.getName());
            tableName = tablePreFix + NameUtils.getUnderlineName(entityClass.getSimpleName());
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
    public void registerTablePrefixMapping(String prefix, String... packages) {
        for (String pkg : packages) {
            this.tablePrefixMapping.put(pkg, prefix);
        }
    }

    protected String getTablePrefix(String className) {
        for (Map.Entry<String, String> entry : tablePrefixMapping.entrySet()) {
            if (className.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return "";
    }

    protected Class<?> getTableClass(String className) {

        if (StrUtils.isBlank(className)) {
            throw new SonsureJdbcException("className不能为空");
        }
        if (this.classMapping == null) {
            this.initScanClassMapping();
        }
        if (className.startsWith(REFERENCE_CLASS_TOKEN) && className.endsWith(REFERENCE_CLASS_TOKEN)) {
            className = className.substring(REFERENCE_CLASS_TOKEN.length(), className.length() - REFERENCE_CLASS_TOKEN.length());
        }
        String name = className;
        int index = className.lastIndexOf(DOT);
        if (index != -1) {
            name = className.substring(index + 1);
        }
        Class<?> clazz = classMapping.get(name);
        if (clazz == null && this.isFailOnMissingClass()) {
            throw new SonsureJdbcException("没有找到对应的class:" + className);
        }
        return clazz;
    }

    /**
     * 初始化类，容忍多次初始化不需要严格的线程安全，
     */
    protected void initScanClassMapping() {

        if (this.scanPackages == null) {
            return;
        }
        if (this.classMapping != null) {
            return;
        }
        this.classMapping = new ConcurrentHashMap<>(32);
        for (String pk : this.scanPackages) {
            List<Class<?>> classes = CLASS_RESOLVER.getResourcesClasses(pk);
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

}

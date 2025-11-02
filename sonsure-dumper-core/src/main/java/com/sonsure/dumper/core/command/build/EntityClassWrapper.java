/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.build;

import com.sonsure.dumper.common.utils.ClassUtils;
import com.sonsure.dumper.common.utils.NameUtils;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author selfly
 */
@Getter
@Setter
public class EntityClassWrapper {

    /**
     * 主键属性后缀
     */
    public static final String PRIMARY_KEY_FIELD_SUFFIX = "Id";

    /**
     * The Entity class.
     */
    private Class<?> entityClass;

    /**
     * 注解
     */
    private Object entityAnnotation;

    /**
     * 主键field
     */
    private EntityClassFieldWrapper primaryKeyField;

    /**
     * 本身field列表，一般顺向使用，如组装sql
     */
    private final Map<String, EntityClassFieldWrapper> classFieldMap;

    /**
     * 映射的field列表，包含了modelFieldMetas中的，及注解等field名称不对应的信息
     * 一般逆向使用，如查询结果集到Entity的处理
     */
    private final Map<String, EntityClassFieldWrapper> mappedFieldMap;

    public EntityClassWrapper(Class<?> cls) {
        this.entityClass = cls;
        classFieldMap = new LinkedHashMap<>(16);
        mappedFieldMap = new LinkedHashMap<>(16);

        Object entityAnnotation = CommandBuildHelper.getEntityAnnotation(this.entityClass);
        this.setEntityAnnotation(entityAnnotation);

        Field[] beanFields = ClassUtils.getSelfOrBaseFields(this.entityClass);
        for (Field field : beanFields) {
            if (Modifier.isStatic(field.getModifiers()) || CommandBuildHelper.getFieldTransientAnnotation(field) != null) {
                continue;
            }
            EntityClassFieldWrapper entityClassFieldWrapper = new EntityClassFieldWrapper();
            entityClassFieldWrapper.setFieldName(field.getName());
            Object fieldIdAnnotation = CommandBuildHelper.getFieldIdAnnotation(field);
            if (fieldIdAnnotation != null) {
                entityClassFieldWrapper.setIdAnnotation(fieldIdAnnotation);
                this.setPrimaryKeyField(entityClassFieldWrapper);
            }
            entityClassFieldWrapper.setColumnAnnotation(CommandBuildHelper.getFieldColumnAnnotation(field));

            this.addEntityField(entityClassFieldWrapper);
        }
        if (this.getPrimaryKeyField() == null) {
            String firstLowerName = NameUtils.getFirstLowerName(this.getEntityClass().getSimpleName());
            String pkFieldName = firstLowerName + PRIMARY_KEY_FIELD_SUFFIX;
            this.primaryKeyField = this.getClassFieldMap().get(pkFieldName);
        }
        if (this.getPrimaryKeyField() == null) {
            throw new SonsureJdbcException("Class不符合规范，未找到主键Field:" + this.entityClass);
        }
    }

    public void addEntityField(EntityClassFieldWrapper entityClassFieldWrapper) {
        this.classFieldMap.put(entityClassFieldWrapper.getFieldName(), entityClassFieldWrapper);
        //数据库返回可能大小写不一定，统一处理成小写
        this.mappedFieldMap.put(entityClassFieldWrapper.getFieldName().toLowerCase(), entityClassFieldWrapper);
        if (entityClassFieldWrapper.getColumnAnnotation() != null) {
            String columnAnnotationName = CommandBuildHelper.getFieldAnnotationColumn(entityClassFieldWrapper.getColumnAnnotation());
            mappedFieldMap.put(columnAnnotationName.toLowerCase(), entityClassFieldWrapper);
        }
    }

    public EntityClassFieldWrapper getMappedField(String columnName) {
        return mappedFieldMap.get(columnName.toLowerCase());
    }

    public EntityClassFieldWrapper getEntityField(String fileName) {
        return classFieldMap.get(fileName);
    }

    public Collection<EntityClassFieldWrapper> getEntityFields() {
        return classFieldMap.values();
    }

}

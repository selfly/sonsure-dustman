/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.management;

import com.sonsure.dumper.common.utils.ClassUtils;
import com.sonsure.dumper.common.utils.NameUtils;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

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
public class ModelClassDetails {

    private Class<?> modelClass;

    /**
     * 注解
     */
    private Object annotation;

    /**
     * 主键field
     */
    private ModelClassFieldDetails primaryKeyField;

    /**
     * 本身field列表，一般顺向使用，如组装sql
     */
    private final Map<String, ModelClassFieldDetails> modelFieldMap;

    /**
     * 映射的field列表，包含了modelFieldMetas中的，及注解等field名称不对应的信息
     * 一般逆向使用，如查询结果集到Model的处理
     */
    private final Map<String, ModelClassFieldDetails> mappedFieldMap;

    public ModelClassDetails(Class<?> cls) {
        this.modelClass = cls;
        modelFieldMap = new LinkedHashMap<>(16);
        mappedFieldMap = new LinkedHashMap<>(16);

        Object entityAnnotation = ModelClassDetailsHelper.getEntityAnnotation(this.modelClass);
        this.setAnnotation(entityAnnotation);

        Field[] beanFields = ClassUtils.getSelfOrBaseFields(this.modelClass);
        for (Field field : beanFields) {
            if (Modifier.isStatic(field.getModifiers()) || ModelClassDetailsHelper.getFieldTransientAnnotation(field) != null) {
                continue;
            }
            ModelClassFieldDetails modelClassFieldDetails = new ModelClassFieldDetails();
            modelClassFieldDetails.setFieldName(field.getName());
            Object fieldIdAnnotation = ModelClassDetailsHelper.getFieldIdAnnotation(field);
            if (fieldIdAnnotation != null) {
                modelClassFieldDetails.setIdAnnotation(fieldIdAnnotation);
                this.setPrimaryKeyField(modelClassFieldDetails);
            }
            modelClassFieldDetails.setColumnAnnotation(ModelClassDetailsHelper.getFieldColumnAnnotation(field));

            this.addModelFieldDetails(modelClassFieldDetails);
        }
        if (this.getPrimaryKeyField() == null) {
            String firstLowerName = NameUtils.getFirstLowerName(this.getModelClass().getSimpleName());
            String pkFieldName = firstLowerName + ModelClassDetailsHelper.PRI_FIELD_SUFFIX;
            this.primaryKeyField = this.getModelFieldMap().get(pkFieldName);
        }
        if (this.getPrimaryKeyField() == null) {
            throw new SonsureJdbcException("Class不符合规范，未找到主键Field:" + this.modelClass);
        }
    }

    public void addModelFieldDetails(ModelClassFieldDetails modelClassFieldDetails) {
        this.modelFieldMap.put(modelClassFieldDetails.getFieldName(), modelClassFieldDetails);
        //数据库返回可能大小写不一定，统一处理成小写
        this.mappedFieldMap.put(modelClassFieldDetails.getFieldName().toLowerCase(), modelClassFieldDetails);
        if (modelClassFieldDetails.getColumnAnnotation() != null) {
            String columnAnnotationName = ModelClassDetailsHelper.getColumnAnnotationName(modelClassFieldDetails.getColumnAnnotation());
            mappedFieldMap.put(columnAnnotationName.toLowerCase(), modelClassFieldDetails);
        }
    }

    public ModelClassFieldDetails getMappedFieldDetails(String columnName) {
        return mappedFieldMap.get(StringUtils.lowerCase(columnName));
    }

    public ModelClassFieldDetails getModelFieldDetails(String fileName) {
        return modelFieldMap.get(fileName);
    }

    public Collection<ModelClassFieldDetails> getModelFields() {
        return modelFieldMap.values();
    }

}

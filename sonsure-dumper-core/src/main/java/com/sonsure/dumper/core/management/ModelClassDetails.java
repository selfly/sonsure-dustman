/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.management;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author selfly
 */
@Getter
@Setter
public class ModelClassDetails {

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

    public ModelClassDetails() {
        modelFieldMap = new LinkedHashMap<>(16);
        mappedFieldMap = new LinkedHashMap<>(16);
    }

    public void addModelFieldDetails(ModelClassFieldDetails modelClassFieldDetails) {
        this.modelFieldMap.put(modelClassFieldDetails.getFieldName(), modelClassFieldDetails);
        if (modelClassFieldDetails.getIdAnnotation() != null) {
            this.primaryKeyField = modelClassFieldDetails;
        }
        //数据库返回可能大小写不一定，统一处理成小写
        this.mappedFieldMap.put(modelClassFieldDetails.getFieldName().toLowerCase(), modelClassFieldDetails);
        if (modelClassFieldDetails.getColumnAnnotation() != null) {
            String columnAnnotationName = ModelClassDetailsCache.getColumnAnnotationName(modelClassFieldDetails.getColumnAnnotation());
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

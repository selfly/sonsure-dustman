/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.management;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author selfly
 */
public class ModelClassMeta {

    /**
     * 注解
     */
    private Object annotation;

    /**
     * 主键field
     */
    private ModelFieldMeta pkFieldMeta;

    /**
     * 本身field列表，一般顺向使用，如组装sql
     */
    private final Map<String, ModelFieldMeta> modelFieldMetas;

    /**
     * 映射的field列表，包含了modelFieldMetas中的，及注解等field名称不对应的信息
     * 一般逆向使用，如查询结果集到Model的处理
     */
    private final Map<String, ModelFieldMeta> mappedFieldMetas;

    public ModelClassMeta() {
        modelFieldMetas = new LinkedHashMap<>(16);
        mappedFieldMetas = new LinkedHashMap<>(16);
    }

    public void addModelFieldMeta(ModelFieldMeta modelFieldMeta) {
        this.modelFieldMetas.put(modelFieldMeta.getName(), modelFieldMeta);
        if (modelFieldMeta.getIdAnnotation() != null) {
            this.pkFieldMeta = modelFieldMeta;
        }
        //数据库返回可能大小写不一定，统一处理成小写
        this.mappedFieldMetas.put(modelFieldMeta.getName().toLowerCase(), modelFieldMeta);
        if (modelFieldMeta.getColumnAnnotation() != null) {
            String columnAnnotationName = ModelClassCache.getColumnAnnotationName(modelFieldMeta.getColumnAnnotation());
            mappedFieldMetas.put(columnAnnotationName.toLowerCase(), modelFieldMeta);
        }
    }

    public ModelFieldMeta getMappedFieldMeta(String columnName) {
        return mappedFieldMetas.get(StringUtils.lowerCase(columnName));
    }

    public ModelFieldMeta getModelFieldMeta(String fileName) {
        return modelFieldMetas.get(fileName);
    }

    public Collection<ModelFieldMeta> getModelFieldMetas() {
        return modelFieldMetas.values();
    }

    public ModelFieldMeta getPkFieldMeta() {
        return pkFieldMeta;
    }

    public void setPkFieldMeta(ModelFieldMeta pkFieldMeta) {
        this.pkFieldMeta = pkFieldMeta;
    }

    public Object getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Object annotation) {
        this.annotation = annotation;
    }


}

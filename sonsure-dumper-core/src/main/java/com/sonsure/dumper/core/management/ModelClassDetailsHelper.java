/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.management;

import com.sonsure.dumper.core.annotation.Column;
import com.sonsure.dumper.core.annotation.Entity;
import com.sonsure.dumper.core.annotation.Id;
import com.sonsure.dumper.core.annotation.Transient;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author selfly
 */
public class ModelClassDetailsHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ModelClassDetailsHelper.class);

    public static final String DOT = ".";
    /**
     * 主键属性后缀
     */
    public static final String PRI_FIELD_SUFFIX = "Id";

    private static final Map<Class<?>, ModelClassDetails> CACHE = new WeakHashMap<>();

    private static boolean enableJavaxPersistence = false;

    private ModelClassDetailsHelper() {
    }

    static {
        try {
            Class<?> clazz = ModelClassDetailsHelper.class.getClassLoader().loadClass("javax.persistence.Entity");
            enableJavaxPersistence = clazz != null;
            if (LOG.isDebugEnabled()) {
                LOG.debug("Enable javax.persistence annotation");
            }
        } catch (ClassNotFoundException e) {
            //ignore
            enableJavaxPersistence = false;
            if (LOG.isDebugEnabled()) {
                LOG.debug("Disable javax.persistence annotation");
            }
        }
    }

    public static ModelClassDetails getClassDetails(Class<?> clazz) {
        ModelClassDetails modelClassDetails = CACHE.get(clazz);
        if (modelClassDetails == null) {
            modelClassDetails = initCache(clazz);
        }
        return modelClassDetails;
    }

    public static String getTableAnnotationName(Object annotation) {
        if (annotation instanceof Entity) {
            return ((Entity) annotation).value();
        } else if (enableJavaxPersistence && annotation instanceof javax.persistence.Entity) {
            return ((javax.persistence.Entity) annotation).name();
        } else {
            throw new SonsureJdbcException("不支持的注解:" + annotation);
        }
    }

    public static String getColumnAnnotationName(Object annotation) {
        if (annotation instanceof Column) {
            return ((Column) annotation).value();
        } else if (enableJavaxPersistence && annotation instanceof javax.persistence.Column) {
            return ((javax.persistence.Column) annotation).name();
        } else {
            throw new SonsureJdbcException("不支持的注解:" + annotation);
        }
    }

    /**
     * 获取class的属性
     *
     * @return class fields
     */
    public static Collection<ModelClassFieldDetails> getClassFieldMetas(Class<?> clazz) {
        ModelClassDetails modelClassDetails = getClassDetails(clazz);
        return modelClassDetails.getModelFields();
    }

    public static ModelClassFieldDetails getClassFieldMeta(Class<?> clazz, String fieldName) {
        ModelClassDetails classMeta = getClassDetails(clazz);
        return classMeta.getModelFieldDetails(fieldName);
    }

    public static ModelClassFieldDetails getMappedFieldMeta(Class<?> clazz, String columnName) {
        ModelClassDetails classMeta = getClassDetails(clazz);
        return classMeta.getMappedFieldDetails(columnName);
    }

    /**
     * 初始化实体类缓存
     *
     * @param clazz the clazz
     * @return the model class meta
     */
    public static ModelClassDetails initCache(Class<?> clazz) {
        ModelClassDetails modelClassDetails = new ModelClassDetails(clazz);
        CACHE.put(clazz, modelClassDetails);
        return modelClassDetails;
    }

    public static Object getEntityAnnotation(Class<?> clazz) {
        Object annotation = clazz.getAnnotation(Entity.class);
        if (annotation == null && enableJavaxPersistence) {
            annotation = clazz.getAnnotation(javax.persistence.Entity.class);
        }
        return annotation;
    }

    public static Object getFieldTransientAnnotation(Field field) {
        Object annotation = field.getAnnotation(Transient.class);
        if (annotation == null && enableJavaxPersistence) {
            annotation = field.getAnnotation(javax.persistence.Transient.class);
        }
        return annotation;
    }

    public static Object getFieldColumnAnnotation(Field field) {
        Object annotation = field.getAnnotation(Column.class);
        if (annotation == null && enableJavaxPersistence) {
            annotation = field.getAnnotation(javax.persistence.Column.class);
        }
        return annotation;
    }

    public static Object getFieldIdAnnotation(Field field) {
        Object annotation = field.getAnnotation(Id.class);
        if (annotation == null && enableJavaxPersistence) {
            annotation = field.getAnnotation(javax.persistence.Id.class);
        }
        return annotation;
    }

    public static String getTableAliasFileName(String tableAlias, String field) {
        if (StringUtils.isBlank(tableAlias)) {
            return field;
        }
        return tableAlias + DOT + field;
    }

}

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
import com.sonsure.dumper.core.annotation.Column;
import com.sonsure.dumper.core.annotation.Entity;
import com.sonsure.dumper.core.annotation.Id;
import com.sonsure.dumper.core.annotation.Transient;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author selfly
 */
public class ModelClassCache {

    private static final Logger LOG = LoggerFactory.getLogger(ModelClassCache.class);

    private static final Map<Class<?>, ModelClassMeta> CACHE = new WeakHashMap<>();

    private static boolean enableJavaxPersistence = false;

    private ModelClassCache() {
    }

    static {
        try {
            Class<?> clazz = ModelClassCache.class.getClassLoader().loadClass("javax.persistence.Entity");
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

    public static ModelClassMeta getClassMeta(Class<?> clazz) {
        ModelClassMeta modelClassMeta = CACHE.get(clazz);
        if (modelClassMeta == null) {
            modelClassMeta = initCache(clazz);
        }
        return modelClassMeta;
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
    public static Collection<ModelFieldMeta> getClassFieldMetas(Class<?> clazz) {
        assertClassNotNull(clazz);
        ModelClassMeta modelClassMeta = getClassMeta(clazz);
        return modelClassMeta.getModelFieldMetas();
    }

    public static ModelFieldMeta getClassFieldMeta(Class<?> clazz, String fieldName) {
        assertClassNotNull(clazz);
        ModelClassMeta classMeta = getClassMeta(clazz);
        return classMeta.getModelFieldMeta(fieldName);
    }

    public static ModelFieldMeta getMappedFieldMeta(Class<?> clazz, String columnName) {
        assertClassNotNull(clazz);
        ModelClassMeta classMeta = getClassMeta(clazz);
        return classMeta.getMappedFieldMeta(columnName);
    }

    private static void assertClassNotNull(Class<?> clazz) {
        if (clazz == null) {
            throw new SonsureJdbcException("class不能为null");
        }
    }

    /**
     * 初始化实体类缓存
     *
     * @param clazz the clazz
     * @return the model class meta
     */
    private static ModelClassMeta initCache(Class<?> clazz) {

        ModelClassMeta modelClassMeta = new ModelClassMeta();

        Object table = getEntityAnnotation(clazz);
        modelClassMeta.setAnnotation(table);

        Field[] beanFields = ClassUtils.getSelfOrBaseFields(clazz);
        for (Field field : beanFields) {

            if (Modifier.isStatic(field.getModifiers()) || getFieldTransientAnnotation(field) != null) {
                continue;
            }

            ModelFieldMeta modelFieldMeta = new ModelFieldMeta();
            modelFieldMeta.setName(field.getName());
            modelFieldMeta.setIdAnnotation(getFieldIdAnnotation(field));
            modelFieldMeta.setColumnAnnotation(getFieldColumnAnnotation(field));

            modelClassMeta.addModelFieldMeta(modelFieldMeta);
        }
        CACHE.put(clazz, modelClassMeta);

        return modelClassMeta;
    }

    private static Object getEntityAnnotation(Class<?> clazz) {
        Object annotation = clazz.getAnnotation(Entity.class);
        if (annotation == null && enableJavaxPersistence) {
            annotation = clazz.getAnnotation(javax.persistence.Entity.class);
        }
        return annotation;
    }

    private static Object getFieldTransientAnnotation(Field field) {
        Object annotation = field.getAnnotation(Transient.class);
        if (annotation == null && enableJavaxPersistence) {
            annotation = field.getAnnotation(javax.persistence.Transient.class);
        }
        return annotation;
    }

    private static Object getFieldColumnAnnotation(Field field) {
        Object annotation = field.getAnnotation(Column.class);
        if (annotation == null && enableJavaxPersistence) {
            annotation = field.getAnnotation(javax.persistence.Column.class);
        }
        return annotation;
    }

    private static Object getFieldIdAnnotation(Field field) {
        Object annotation = field.getAnnotation(Id.class);
        if (annotation == null && enableJavaxPersistence) {
            annotation = field.getAnnotation(javax.persistence.Id.class);
        }
        return annotation;
    }

}

/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command;

import com.sonsure.dumper.common.utils.ClassUtils;
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author selfly
 */
public class CommandBuildHelper {

    private static final Logger LOG = LoggerFactory.getLogger(CommandBuildHelper.class);

    /**
     * value需要native内容前后包围符号
     */
    public static final String NATIVE_OPEN_TOKEN = "{{";
    public static final String NATIVE_CLOSE_TOKEN = "}}";

    public static final String DOT = ".";
    /**
     * 主键属性后缀
     */
    public static final String PRI_FIELD_SUFFIX = "Id";

    private static final Map<Class<?>, ModelClassDetails> CACHE = new WeakHashMap<>();

    private static boolean enableJavaxPersistence = false;

    private CommandBuildHelper() {
    }

    static {
        try {
            Class<?> clazz = CommandBuildHelper.class.getClassLoader().loadClass("javax.persistence.Entity");
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

    public static String getTableAliasFieldName(String tableAlias, String field) {
        if (StringUtils.isBlank(tableAlias)) {
            return field;
        }
        return tableAlias + DOT + field;
    }

    public static String wrapperToNative(String name) {
        return NATIVE_OPEN_TOKEN + name + NATIVE_CLOSE_TOKEN;
    }

    public static boolean isNativeContent(String content) {
        return StringUtils.startsWith(content, NATIVE_OPEN_TOKEN) && StringUtils.endsWith(content, NATIVE_CLOSE_TOKEN);
    }

    public static String getNativeContentActualValue(String content) {
        return StringUtils.substring(content, NATIVE_OPEN_TOKEN.length(), content.length() - NATIVE_CLOSE_TOKEN.length());
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> obj2PropMap(Object obj, boolean ignoreNull) {
        //noinspection unchecked
        Map<String, Object> propMap = obj instanceof Map ? (Map<String, Object>) obj : ClassUtils.getSelfBeanPropMap(obj, Transient.class);
        Map<String, Object> resultMap = new LinkedHashMap<>(propMap.size());
        for (Map.Entry<String, Object> entry : propMap.entrySet()) {
            Object value = entry.getValue();
            if (value != null || !ignoreNull) {
                resultMap.put(entry.getKey(), value);
            }
        }
        return resultMap;
    }

}

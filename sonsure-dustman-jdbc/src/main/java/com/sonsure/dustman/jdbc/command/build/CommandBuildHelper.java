/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.jdbc.command.build;

import com.sonsure.dustman.common.utils.ClassUtils;
import com.sonsure.dustman.common.utils.StrUtils;
import com.sonsure.dustman.jdbc.annotation.Column;
import com.sonsure.dustman.jdbc.annotation.Entity;
import com.sonsure.dustman.jdbc.annotation.Id;
import com.sonsure.dustman.jdbc.annotation.Transient;
import com.sonsure.dustman.jdbc.exception.SonsureJdbcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

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

    private static boolean enableJavaxPersistence = false;

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

    public static String getTableAnnotationName(Object annotation) {
        if (annotation instanceof Entity) {
            return ((Entity) annotation).value();
        } else if (enableJavaxPersistence && annotation instanceof javax.persistence.Entity) {
            return ((javax.persistence.Entity) annotation).name();
        } else {
            throw new SonsureJdbcException("不支持的注解:" + annotation);
        }
    }

    public static String getFieldAnnotationColumn(Object annotation) {
        if (annotation instanceof Column) {
            return ((Column) annotation).value();
        } else if (enableJavaxPersistence && annotation instanceof javax.persistence.Column) {
            return ((javax.persistence.Column) annotation).name();
        } else {
            throw new SonsureJdbcException("不支持的注解:" + annotation);
        }
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
        if (StrUtils.isBlank(tableAlias)) {
            return field;
        }
        return tableAlias + DOT + field;
    }

    public static boolean isNativeContent(String content) {
        return content.startsWith(NATIVE_OPEN_TOKEN) && content.endsWith(NATIVE_CLOSE_TOKEN);
    }

    public static String getNativeContentActualValue(String content) {
        return content.substring(NATIVE_OPEN_TOKEN.length(), content.length() - NATIVE_CLOSE_TOKEN.length());
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> obj2PropMap(Object obj, boolean updateNull) {
        Map<String, Object> propMap = obj instanceof Map ? (Map<String, Object>) obj : ClassUtils.getSelfBeanPropMap(obj, Transient.class);
        Map<String, Object> resultMap = new LinkedHashMap<>(propMap.size());
        for (Map.Entry<String, Object> entry : propMap.entrySet()) {
            Object value = entry.getValue();
            if (value != null || updateNull) {
                resultMap.put(entry.getKey(), value);
            }
        }
        return resultMap;
    }

}

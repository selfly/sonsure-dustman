/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.lambda;

import com.sonsure.commons.utils.NameUtils;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import org.apache.commons.lang3.StringUtils;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author liyd
 */
public class LambdaMethod {

    private static final Field CAPTURING_CLASS_FIELD;

    static {
        try {
            CAPTURING_CLASS_FIELD = SerializedLambda.class.getDeclaredField("capturingClass");
            CAPTURING_CLASS_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new SonsureJdbcException("获取SerializedLambda capturingClass属性失败", e);
        }
    }


    public static <T, R> String getField(Function<T, R> lambda) {
        Method method = createMethod(lambda);
        return getMethodField(method);
    }

    @SafeVarargs
    public static <T, R> String[] getFields(Function<T, R>... functions) {
        String[] fields = new String[functions.length];
        for (int i = 0; i < functions.length; i++) {
            fields[i] = getField(functions[i]);
        }
        return fields;
    }

    public static String getMethodField(Method getter) {
        String name = getter.getName();
        if (!StringUtils.startsWith(name, "get")) {
            throw new SonsureJdbcException("只能是JavaBean的get方法");
        }
        return NameUtils.getFirstLowerName(StringUtils.substring(name, 3));
    }

    public static <T, R> Method createMethod(Function<T, R> lambda) {
        SerializedLambda serializedLambda = getSerializedLambda(lambda);
        return getMethod(serializedLambda);
    }

    private static SerializedLambda getSerializedLambda(Object lambda) {
        try {
            Method m = lambda.getClass().getDeclaredMethod("writeReplace");
            m.setAccessible(true);
            return (SerializedLambda) m.invoke(lambda);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Method getMethod(SerializedLambda serializedLambda) {
        String className = StringUtils.replace(serializedLambda.getImplClass(), "/", ".");
        try {
            final Class<?> clazz = (Class<?>) CAPTURING_CLASS_FIELD.get(serializedLambda);
            ClassLoader classLoader = clazz != null && clazz.getClassLoader() != null ? clazz.getClassLoader() : Thread.currentThread().getContextClassLoader();
            return classLoader.loadClass(className).getDeclaredMethod(serializedLambda.getImplMethodName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

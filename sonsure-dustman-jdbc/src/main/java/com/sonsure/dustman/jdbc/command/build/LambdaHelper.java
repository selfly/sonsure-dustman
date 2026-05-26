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

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liyd
 */
public class LambdaHelper {

    private static final Map<Class<?>, LambdaGetter> CACHE = new ConcurrentHashMap<>();

    public static <T> LambdaGetter getLambdaGetter(GetterFunction<T> lambda) {
        Class<?> lambdaClass = lambda.getClass();
        return CACHE.computeIfAbsent(lambdaClass, k -> {
            SerializedLambda invoke = getSerializedLambda(lambda);
            return new LambdaGetter(invoke);
        });
    }

    public static <T> String getFieldName(GetterFunction<T> lambda) {
        return getLambdaGetter(lambda).getFieldName();
    }

    private static SerializedLambda getSerializedLambda(Object lambda) {
        Method writeReplace = ClassUtils.getDeclaredMethod(lambda.getClass(), "writeReplace");
        return (SerializedLambda) ClassUtils.invokeMethod(writeReplace, lambda);
    }
}

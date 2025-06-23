/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.lambda;

import com.sonsure.dumper.common.spring.ReflectionUtils;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

/**
 * @author liyd
 */
public class LambdaHelper {

    public static <T, R> LambdaClass getLambdaClass(Function<T, R> lambda) {
        SerializedLambda invoke = getSerializedLambda(lambda);
        return new LambdaClass(invoke);
    }

    public static <T, R> String getFieldName(Function<T, R> lambda) {
        LambdaClass lambdaClass = getLambdaClass(lambda);
        return lambdaClass.getFieldName();
    }

    @SafeVarargs
    public static <T, R> String[] getFieldNames(Function<T, R>... functions) {
        String[] fields = new String[functions.length];
        for (int i = 0; i < functions.length; i++) {
            fields[i] = getFieldName(functions[i]);
        }
        return fields;
    }

    private static SerializedLambda getSerializedLambda(Object lambda) {
        Method writeReplace = ReflectionUtils.findMethod(lambda.getClass(), "writeReplace");
        ReflectionUtils.makeAccessible(writeReplace);
        return (SerializedLambda) ReflectionUtils.invokeMethod(writeReplace, lambda);
    }
}

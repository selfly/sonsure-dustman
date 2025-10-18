/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.build;

import com.sonsure.dumper.common.spring.ReflectionUtils;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

/**
 * @author liyd
 */
public class LambdaHelper {

    public static <T> LambdaGetter getLambdaGetter(GetterFunction<T> lambda) {
        SerializedLambda invoke = getSerializedLambda(lambda);
        return new LambdaGetter(invoke);
    }

    public static <T> String getFieldName(GetterFunction<T> lambda) {
        LambdaGetter lambdaGetter = getLambdaGetter(lambda);
        return lambdaGetter.getFieldName();
    }

    @SafeVarargs
    public static <T> String[] getFieldNames(GetterFunction<T>... functions) {
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

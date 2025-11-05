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

    private static SerializedLambda getSerializedLambda(Object lambda) {
        Method writeReplace = ClassUtils.getDeclaredMethod(lambda.getClass(), "writeReplace");
        return (SerializedLambda) ClassUtils.invokeMethod(writeReplace, lambda);
    }
}

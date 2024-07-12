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
import com.sonsure.dumper.common.utils.NameUtils;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import org.apache.commons.lang3.StringUtils;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

/**
 * @author liyd
 */
public class LambdaMethod {

    public static <T, R> String getField(Function<T, R> lambda) {
        SerializedLambda invoke = getSerializedLambda(lambda);
        return methodToField(invoke.getImplMethodName());
    }

    @SafeVarargs
    public static <T, R> String[] getFields(Function<T, R>... functions) {
        String[] fields = new String[functions.length];
        for (int i = 0; i < functions.length; i++) {
            fields[i] = getField(functions[i]);
        }
        return fields;
    }

    private static SerializedLambda getSerializedLambda(Object lambda) {
        Method writeReplace = ReflectionUtils.findMethod(lambda.getClass(), "writeReplace");
        ReflectionUtils.makeAccessible(writeReplace);
        return (SerializedLambda) ReflectionUtils.invokeMethod(writeReplace, lambda);
    }

    public static String methodToField(String name) {
        if (name.startsWith("is")) {
            name = StringUtils.substring(name, 2);
        } else if (name.startsWith("get")) {
            name = StringUtils.substring(name, 3);
        } else {
            throw new SonsureJdbcException("只能是JavaBean的get方法");
        }
        if (StringUtils.isNotBlank(name)) {
            name = NameUtils.getFirstLowerName(name);
        }
        return name;
    }
}

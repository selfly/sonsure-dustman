/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.common.bean;

import com.sonsure.dumper.common.utils.ClassUtils;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;

/**
 * @author liyd
 * @date 16/4/27
 */
public class NumberConverter implements TypeConverter {

    private static final List<Class<?>> SUPPORT_CLASSES = Arrays.asList(Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class);

    @Override
    public boolean supportSourceType(Class<?> sourceType) {
        return Number.class.isAssignableFrom(sourceType);
    }

    @Override
    public boolean supportTargetType(Class<?> targetType) {
        return SUPPORT_CLASSES.contains(ClassUtils.getGenericClass(targetType));
    }

    @Override
    public Object convert(PropertyDescriptor targetPd, Object value) {
        //只做最常见的几种转换
        final Number number = (Number) value;
        Class<?> targetClass = ClassUtils.getGenericClass(targetPd.getPropertyType());
        if (Byte.class.equals(targetClass)) {
            return number.byteValue();
        } else if (Short.class.equals(targetClass)) {
            return number.shortValue();
        } else if (Integer.class.equals(targetClass)) {
            return number.intValue();
        } else if (Long.class.equals(targetClass)) {
            return number.longValue();
        } else if (Float.class.equals(targetClass)) {
            return number.floatValue();
        } else if (Double.class.equals(targetClass)) {
            return number.doubleValue();
        } else {
            return value;
        }
    }

}

/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.common.bean;


import com.sonsure.dumper.common.enums.*;

import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * IEnum与String互转
 * <p>
 *
 * @author liyd
 * @date 8/10/14
 */
public class EnumConverter {

    public static class String2DynamicEnumConverter implements TypeConverter {

        @Override
        public boolean supportSourceType(Class<?> sourceType) {
            return String.class.equals(sourceType);
        }

        @Override
        public boolean supportTargetType(Class<?> targetType) {
            return BaseEnum.class.isAssignableFrom(targetType) || DynamicEnum.class.isAssignableFrom(targetType);
        }

        @Override
        public Object convert(PropertyDescriptor targetPd, Object value) {
            if (DynamicEnum.class.isAssignableFrom(targetPd.getPropertyType())) {
                Type[] parameterTypes = targetPd.getWriteMethod().getGenericParameterTypes();
                Type[] actualTypeArguments = ((ParameterizedType) parameterTypes[0]).getActualTypeArguments();
                //noinspection unchecked
                return DynamicEnumHelper.getEnumItemByCode((Class<? extends BaseDynamicEnum>) actualTypeArguments[0], (String) value);
            } else {
                //noinspection unchecked
                return EnumHelper.getEnum((Class<? extends BaseEnum>) targetPd.getPropertyType(), (String) value);
            }
        }
    }

    public static class BaseEnum2StringConverter implements TypeConverter {

        @Override
        public boolean supportSourceType(Class<?> sourceType) {
            return BaseEnum.class.isAssignableFrom(sourceType);
        }

        @Override
        public boolean supportTargetType(Class<?> targetType) {
            return String.class.equals(targetType);
        }

        @Override
        public Object convert(PropertyDescriptor targetPd, Object value) {
            return ((BaseEnum) value).getCode();
        }
    }

}

/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.common.bean;

import java.beans.PropertyDescriptor;

/**
 * 数据类型转换接口
 * <p/>
 * User: liyd
 * Date: 13-5-9 下午12:04
 * version $Id: TypeConverter.java, v 0.1 Exp $
 *
 * @author selfly
 */
public interface TypeConverter {

    /**
     * Handled type class.
     *
     * @param sourceType the source type
     * @return the class
     */
    boolean supportSourceType(Class<?> sourceType);

    /**
     * Target type class.
     *
     * @param targetType the target type
     * @return the class
     */
    boolean supportTargetType(Class<?> targetType);

    /**
     * 转换
     *
     * @param targetPd the target pd
     * @param value    The input value to be converted
     * @return The converted value
     */
    Object convert(PropertyDescriptor targetPd, Object value);

}

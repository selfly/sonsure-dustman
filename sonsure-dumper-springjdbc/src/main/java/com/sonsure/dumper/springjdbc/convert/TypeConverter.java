/*
 * Copyright (c) 2022. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   https://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.springjdbc.convert;

/**
 * @author liyd
 */
public interface TypeConverter {

    /**
     * Support boolean.
     *
     * @param requiredType the required type
     * @param value        the value
     * @return the boolean
     */
    boolean support(Class<?> requiredType, Object value);

    /**
     * Convert object.
     *
     * @param requiredType the required type
     * @param value        the value
     * @return the object
     */
    Object convert(Class<?> requiredType, Object value);

}

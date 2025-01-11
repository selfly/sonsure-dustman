/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.common.validation;

/**
 * @author liyd
 * @since 17/1/23
 */
public interface Validator {

    String PREFIX = "ss.";

    /***
     * 值校验
     *
     * @param value the value
     * @param message the message
     * @param msgArgs the msg args
     * @return the validator result
     */
    ValidatorResult validate(Object value, String message, Object[] msgArgs);
}

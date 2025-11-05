/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.common.exception;

/**
 * @author liyd
 */
public class SonsureCommonsException extends SonsureException {

    private static final long serialVersionUID = 5991174454832836064L;

    public SonsureCommonsException(String message, Throwable e) {
        super(message, e);
    }

    public SonsureCommonsException(String message) {
        super(message);
    }

    public SonsureCommonsException(String code, String message) {
        super(code, message);
    }

}

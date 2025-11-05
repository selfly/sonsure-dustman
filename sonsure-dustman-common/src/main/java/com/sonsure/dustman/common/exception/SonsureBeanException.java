/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.common.exception;

public class SonsureBeanException extends SonsureException {

    public SonsureBeanException(String message, Throwable e) {
        super(message, e);
    }

    public SonsureBeanException(String message) {
        super(message);
    }
}

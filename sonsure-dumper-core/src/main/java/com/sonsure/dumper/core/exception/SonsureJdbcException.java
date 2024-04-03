/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.exception;

import com.sonsure.dumper.common.exception.SonsureException;

/**
 * Created by liyd on 17/4/11.
 */
public class SonsureJdbcException extends SonsureException {

    private static final long serialVersionUID = -5833705110587887140L;

    public SonsureJdbcException(String message, Throwable e) {
        super(message, e);
    }

    /**
     * Constructor
     *
     * @param message the message
     */
    public SonsureJdbcException(String message) {
        super(message);
    }

}

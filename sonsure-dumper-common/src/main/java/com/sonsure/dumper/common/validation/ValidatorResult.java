/*
 * Copyright (c) 2021. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.common.validation;

import lombok.Getter;
import lombok.Setter;

import java.text.MessageFormat;

/**
 * The type Validator result.
 *
 * @author liyd
 */
@Setter
@Getter
public class ValidatorResult {

    private boolean success;

    private String code;

    private String message;

    public ValidatorResult(boolean success) {
        this.success = success;
    }

    public void resolveError(String code, String message, Object... args) {
        if (this.success) {
            return;
        }
        this.code = code;
        if (args != null) {
            message = MessageFormat.format(message, args);
        }
        this.message = message;
    }
}

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
 * @date 17/3/19
 */
public class BooleanValidator implements Validator {

    private static final String BOOLEAN = PREFIX + "not.eq.boolean";

    private final boolean expectVal;

    public BooleanValidator(boolean expectVal) {
        this.expectVal = expectVal;
    }

    @Override
    public ValidatorResult validate(Object value, String message) {
        ValidatorResult validatorResult = new ValidatorResult(false);
        if ((Boolean) value == expectVal) {
            validatorResult.setSuccess(true);
        } else {
            validatorResult.setCode(BOOLEAN);
            validatorResult.setMessage(message);
        }
        return validatorResult;
    }
}

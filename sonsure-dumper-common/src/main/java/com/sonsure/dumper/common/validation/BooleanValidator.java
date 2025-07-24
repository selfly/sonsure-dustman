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
 * @since 17/3/19
 */
public class BooleanValidator implements Validator {

    private static final String ERROR_CODE = PREFIX + "not.eq.boolean";

    private final boolean expectVal;

    public BooleanValidator(boolean expectVal) {
        this.expectVal = expectVal;
    }

    @Override
    public ValidatorResult validate(Object value, String message, Object[] msgArgs) {
        ValidatorResult validatorResult = new ValidatorResult(false);
        if ((Boolean) value == expectVal) {
            validatorResult.setSuccess(true);
        } else {
            validatorResult.resolveError(ERROR_CODE, message, msgArgs);
        }
        return validatorResult;
    }

}

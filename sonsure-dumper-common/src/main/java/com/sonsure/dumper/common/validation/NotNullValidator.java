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
public class NotNullValidator implements Validator {

    private static final String ERROR_CODE = PREFIX + "not.null";

    @Override
    public ValidatorResult validate(Object value, String message, Object[] msgArgs) {
        ValidatorResult validatorResult = new ValidatorResult(value != null);
        validatorResult.resolveError(ERROR_CODE, message, msgArgs);
        return validatorResult;
    }

}

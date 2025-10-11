/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.common.validation;


import com.sonsure.dumper.common.utils.StrUtils;

/**
 * @author liyd
 * @since 17/2/13
 */
public class StringValidator implements Validator {

    public static final String ERROR_CODE = PREFIX + "not.blank";

    @Override
    public ValidatorResult validate(Object value, String message, Object[] msgArgs) {
        ValidatorResult validatorResult = new ValidatorResult(StrUtils.isNotBlank((String) value));
        validatorResult.resolveError(ERROR_CODE, message, msgArgs);
        return validatorResult;
    }
}

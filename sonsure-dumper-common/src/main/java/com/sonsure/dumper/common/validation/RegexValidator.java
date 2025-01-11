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
 * @since 17/1/24
 */
public class RegexValidator implements Validator {

    private static final String ERROR_CODE = PREFIX + "regex.error";

    @Override
    public ValidatorResult validate(Object value, String message, Object[] msgArgs) {
        String[] values = (String[]) value;
        ValidatorResult result = new ValidatorResult(values[0].matches(values[1]));
        result.resolveError(ERROR_CODE, message, msgArgs);
        return result;
    }

}

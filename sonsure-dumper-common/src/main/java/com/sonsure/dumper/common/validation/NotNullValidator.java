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
 * @date 17/1/23
 */
public class NotNullValidator implements Validator {

    private static final String[] NOT_NULL = {PREFIX + "not.null", "不能为空"};

    @Override
    public ValidatorResult validate(Object obj, String validateName) {
        ValidatorResult validatorResult = new ValidatorResult(obj != null);
        validatorResult.setCode(NOT_NULL[0]);
        validatorResult.setMessage(validateName + NOT_NULL[1]);
        return validatorResult;
    }
}

/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.common.validation;

import com.sonsure.dumper.common.exception.ValidationException;
import org.apache.commons.lang3.StringUtils;

/**
 * @author liyd
 * @date 17/3/19
 */
public class BooleanValidator implements Validator {

    public static final String[] FALSE = {PREFIX + "not.eq.false", "必须为false"};

    public static final String[] TRUE = {PREFIX + "not.eq.true", "必须为true"};

    private final String type;

    public BooleanValidator(String type) {
        this.type = type;
    }

    @Override
    public ValidatorResult validate(Object value, String validateName) {
        ValidatorResult validatorResult = new ValidatorResult(false);
        if (value == null) {
            return validatorResult;
        }
        if (StringUtils.equals(type, FALSE[0])) {
            validatorResult.setSuccess(!((Boolean) value));
            validatorResult.setCode(FALSE[0]);
            validatorResult.setMessage(validateName + FALSE[1]);
            return validatorResult;
        } else if (StringUtils.equals(type, TRUE[0])) {
            validatorResult.setSuccess((Boolean) value);
            validatorResult.setCode(TRUE[0]);
            validatorResult.setMessage(validateName + TRUE[1]);
            return validatorResult;
        } else {
            throw new ValidationException("不支持的校验");
        }
    }
}

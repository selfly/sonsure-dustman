/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.common.validation;

import java.text.MessageFormat;

/**
 * @author liyd
 * @date 17/1/24
 */
public class RegexValidator implements Validator {

    private static final String[] REGEX = {PREFIX + "regex.error", "{0}格式不正确"};

    @Override
    public ValidatorResult validate(Object value, String validateName) {
        String[] values = (String[]) value;
        ValidatorResult result = new ValidatorResult(values[0].matches(values[1]));
        result.setCode(REGEX[0]);
        result.setMessage(MessageFormat.format(REGEX[1], validateName));
        return result;
    }
}

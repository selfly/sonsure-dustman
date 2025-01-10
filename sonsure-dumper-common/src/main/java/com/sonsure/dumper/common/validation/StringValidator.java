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

import java.text.MessageFormat;

/**
 * @author liyd
 * @date 17/2/13
 */
public class StringValidator implements Validator {

    public static final String NOT_BLANK = PREFIX + "not.blank";

    public static final String NOT_EMPTY = PREFIX + "not.empty";

    public static final String MUST_EQ = PREFIX + "must.eq";

    public static final String MUST_EQ_IGNORE_CASE = PREFIX + "must.eq.ignore.case";

    public static final String NOT_EQ = PREFIX + "not.eq";

    public static final String NOT_EQ_IGNORE_CASE = PREFIX + "not.eq.ignore.case";

    public static final String MIN_LENGTH = PREFIX + "min.length";

    public static final String MAX_LENGTH = PREFIX + "max.length";

    public static final String EQ_LENGTH = PREFIX + "eq.length";


    private final String code;

    public StringValidator(String code) {
        this.code = code;
    }

    @Override
    public ValidatorResult validate(Object value, String message) {

        ValidatorResult validatorResult = new ValidatorResult(true);
        String resultMsg = message;
        if (StringUtils.equals(code, NOT_BLANK)) {
            validatorResult.setSuccess(StringUtils.isNotBlank((String) value));
        } else if (StringUtils.equals(code, NOT_EMPTY)) {
            validatorResult.setSuccess(StringUtils.isNotEmpty((String) value));
        } else if (StringUtils.equals(code, MUST_EQ)) {
            Object[] values = (Object[]) value;
            validatorResult.setSuccess(StringUtils.equals((String) values[0], (String) values[1]));
        } else if (StringUtils.equals(code, MUST_EQ_IGNORE_CASE)) {
            Object[] values = (Object[]) value;
            validatorResult.setSuccess(StringUtils.equalsIgnoreCase((String) values[0], (String) values[1]));
        } else if (StringUtils.equals(code, MIN_LENGTH)) {
            Object[] values = (Object[]) value;
            validatorResult.setSuccess(StringUtils.length((String) values[0]) >= (Integer) values[1]);
            resultMsg = MessageFormat.format(message, values[1]);
        } else if (StringUtils.equals(code, MAX_LENGTH)) {
            Object[] values = (Object[]) value;
            validatorResult.setSuccess(StringUtils.length((String) values[0]) <= (Integer) values[1]);
            resultMsg = MessageFormat.format(message, values[1]);
        } else if (StringUtils.equals(code, EQ_LENGTH)) {
            Object[] values = (Object[]) value;
            validatorResult.setSuccess(StringUtils.length((String) values[0]) == (Integer) values[1]);
            resultMsg = MessageFormat.format(message, values[1]);
        } else if (StringUtils.equals(code, NOT_EQ)) {
            Object[] values = (Object[]) value;
            validatorResult.setSuccess(!StringUtils.equals((String) values[0], (String) values[1]));
            resultMsg = MessageFormat.format(message, values[1]);
        } else if (StringUtils.equals(code, NOT_EQ_IGNORE_CASE)) {
            Object[] values = (Object[]) value;
            validatorResult.setSuccess(!StringUtils.equalsIgnoreCase((String) values[0], (String) values[1]));
        } else {
            throw new ValidationException("不支持的校验");
        }
        if (!validatorResult.isSuccess()) {
            validatorResult.setCode(code);
            validatorResult.setMessage(resultMsg);
        }
        return validatorResult;
    }
}

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
import java.util.Collection;

/**
 * @author liyd
 * @date 17/3/12
 */
public class CollectionValidator implements Validator {

    public static final String IS_EMPTY = PREFIX + "collection.el.empty";
    public static final String MIN_SIZE = PREFIX + "collection.min.size";
    public static final String MAX_SIZE = PREFIX + "collection.max.size";
    public static final String EQ_SIZE = PREFIX + "collection.eq.size";

    private final String code;

    public CollectionValidator(String code) {
        this.code = code;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public ValidatorResult validate(Object value, String message) {

        ValidatorResult validatorResult = new ValidatorResult(false);
        String resultMsg = message;
        if (StringUtils.equals(code, IS_EMPTY)) {
            validatorResult.setSuccess(value == null || ((Collection) value).isEmpty());
        } else if (StringUtils.equals(code, MIN_SIZE)) {
            Object[] values = (Object[]) value;
            Collection collection = (Collection) values[0];
            validatorResult.setSuccess(collection != null && collection.size() >= (Integer) values[1]);
            resultMsg = MessageFormat.format(message, values[1]);
        } else if (StringUtils.equals(code, MAX_SIZE)) {
            Object[] values = (Object[]) value;
            Collection collection = (Collection) values[0];
            validatorResult.setSuccess(collection == null || collection.size() <= (Integer) values[1]);
            resultMsg = MessageFormat.format(message, values[1]);
        } else if (StringUtils.equals(code, EQ_SIZE)) {
            Object[] values = (Object[]) value;
            Collection collection = (Collection) values[0];
            validatorResult.setSuccess(collection != null && collection.size() == ((Integer) values[1]));
            resultMsg = MessageFormat.format(message, values[1]);
        } else {
            throw new ValidationException("不支持的集合操作");
        }
        if (!validatorResult.isSuccess()) {
            validatorResult.setCode(code);
            validatorResult.setMessage(resultMsg);
        }
        return validatorResult;
    }
}

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

    public static final String[] MUST_EMPTY = {PREFIX + "collection.must.empty", "必须为空"};
    public static final String[] MIN_SIZE = {PREFIX + "collection.min.size", "允许最小大小为{0}"};
    public static final String[] MAX_SIZE = {PREFIX + "collection.max.size", "允许最大大小为{0}"};
    public static final String[] EQ_SIZE = {PREFIX + "collection.eq.size", "大小必须为{0}"};

    private final String type;

    public CollectionValidator(String type) {
        this.type = type;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public ValidatorResult validate(Object value, String validateName) {

        ValidatorResult validatorResult = new ValidatorResult(false);
        if (StringUtils.equals(type, MUST_EMPTY[0])) {
            validatorResult.setSuccess(value == null || ((Collection) value).isEmpty());
            validatorResult.setCode(MUST_EMPTY[0]);
            validatorResult.setMessage(MUST_EMPTY[1]);
            return validatorResult;
        } else if (StringUtils.equals(type, MIN_SIZE[0])) {
            Object[] values = (Object[]) value;
            Collection collection = (Collection) values[0];
            validatorResult.setSuccess(collection != null && collection.size() >= (Integer) values[1]);
            validatorResult.setCode(MIN_SIZE[0]);
            validatorResult.setMessage(MessageFormat.format(MIN_SIZE[1], values[1]));
            return validatorResult;
        } else if (StringUtils.equals(type, MAX_SIZE[0])) {
            Object[] values = (Object[]) value;
            Collection collection = (Collection) values[0];
            validatorResult.setSuccess(collection == null || collection.size() <= (Integer) values[1]);
            validatorResult.setCode(MAX_SIZE[0]);
            validatorResult.setMessage(MessageFormat.format(MAX_SIZE[1], values[1]));
            return validatorResult;
        } else if (StringUtils.equals(type, EQ_SIZE[0])) {
            Object[] values = (Object[]) value;
            Collection collection = (Collection) values[0];
            validatorResult.setSuccess(collection != null && collection.size() == ((Integer) values[1]));
            validatorResult.setCode(EQ_SIZE[0]);
            validatorResult.setMessage(MessageFormat.format(EQ_SIZE[1], values[1]));
            return validatorResult;
        } else {

            throw new ValidationException("不支持的集合操作");
        }
    }
}

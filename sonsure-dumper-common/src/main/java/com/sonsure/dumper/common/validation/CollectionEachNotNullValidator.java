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
import java.util.Collection;

/**
 * @author liyd
 * @date 17/1/24
 */
public class CollectionEachNotNullValidator implements Validator {

    public static final String[] COLLECTION_EACH_NOT_NULL = {PREFIX + "collection.each.not.null", "{0}的每个元素都不能为空"};

    @Override
    public ValidatorResult validate(Object value, String validateName) {
        ValidatorResult validatorResult = new ValidatorResult(false);
        if (value == null) {
            return validatorResult;
        }
        Collection<?> collection = (Collection<?>) value;
        for (Object obj : collection) {
            if (obj == null) {
                validatorResult.setSuccess(false);
                validatorResult.setCode(COLLECTION_EACH_NOT_NULL[0]);
                validatorResult.setMessage(MessageFormat.format(COLLECTION_EACH_NOT_NULL[1], validateName));
                return validatorResult;
            }
        }
        validatorResult.setSuccess(true);
        return validatorResult;
    }
}

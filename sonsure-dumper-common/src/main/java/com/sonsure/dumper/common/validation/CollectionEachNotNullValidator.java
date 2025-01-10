/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.common.validation;

import java.util.Collection;

/**
 * @author liyd
 * @date 17/1/24
 */
public class CollectionEachNotNullValidator implements Validator {

    private static final String COLLECTION_EACH_NOT_NULL = PREFIX + "collection.each.not.null";

    @Override
    public ValidatorResult validate(Object value, String message) {
        ValidatorResult validatorResult = new ValidatorResult(false);
        if (value == null) {
            validatorResult.setMessage(message);
            return validatorResult;
        }
        Collection<?> collection = (Collection<?>) value;
        for (Object obj : collection) {
            if (obj == null) {
                validatorResult.setSuccess(false);
                validatorResult.setCode(COLLECTION_EACH_NOT_NULL);
                validatorResult.setMessage(message);
                return validatorResult;
            }
        }
        validatorResult.setSuccess(true);
        return validatorResult;
    }
}

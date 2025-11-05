/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.common.validation;

import java.util.Collection;

/**
 * @author liyd
 * @since  17/1/24
 */
public class CollectionEachNotNullValidator implements Validator {

    private static final String ERROR_CODE = PREFIX + "each.el.not.null";

    @Override
    public ValidatorResult validate(Object value, String message, Object[] msgArgs) {
        ValidatorResult validatorResult = new ValidatorResult(false);
        if (value == null) {
            validatorResult.resolveError(ERROR_CODE, message, msgArgs);
            return validatorResult;
        }
        Collection<?> collection = (Collection<?>) value;
        for (Object obj : collection) {
            if (obj == null) {
                validatorResult.resolveError(ERROR_CODE, message, msgArgs);
                return validatorResult;
            }
        }
        validatorResult.setSuccess(true);
        return validatorResult;
    }

}

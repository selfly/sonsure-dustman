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
import java.util.Map;

/**
 * @author liyd
 * @since  17/1/23
 */
public class NotEmptyValidator implements Validator {

    private static final String ERROR_CODE = PREFIX + "not.empty";

    @Override
    public ValidatorResult validate(Object value, String message, Object[] msgArgs) {
        ValidatorResult validatorResult = new ValidatorResult(false);
        if (value == null) {
            validatorResult.setSuccess(false);
        } else if (value instanceof Collection) {
            Collection<?> cts = (Collection<?>) value;
            validatorResult.setSuccess(!cts.isEmpty());
        } else if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            validatorResult.setSuccess(!map.isEmpty());
        } else if (value.getClass().isArray()) {
            validatorResult.setSuccess(((Object[]) value).length > 0);
        } else {
            throw new UnsupportedOperationException("不支持的参数类型");
        }
        validatorResult.resolveError(ERROR_CODE, message, msgArgs);
        return validatorResult;
    }

}

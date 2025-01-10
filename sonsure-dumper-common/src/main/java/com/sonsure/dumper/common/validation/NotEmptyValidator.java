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
import java.util.Map;

/**
 * @author liyd
 * @date 17/1/23
 */
public class NotEmptyValidator implements Validator {

    private static final String NOT_EMPTY = PREFIX + "not.empty";

    @Override
    public ValidatorResult validate(Object obj, String message) {
        ValidatorResult validatorResult = new ValidatorResult(false);
        if (obj == null) {
            validatorResult.setSuccess(false);
        } else if (obj instanceof Collection) {
            Collection<?> cts = (Collection<?>) obj;
            validatorResult.setSuccess(!cts.isEmpty());
        } else if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            validatorResult.setSuccess(!map.isEmpty());
        } else if (obj.getClass().isArray()) {
            validatorResult.setSuccess(((Object[]) obj).length > 0);
        } else if (obj instanceof String) {
            validatorResult.setSuccess(!((String) obj).isEmpty());
        } else {
            throw new UnsupportedOperationException("不支持的参数类型");
        }
        if (!validatorResult.isSuccess()) {
            validatorResult.setCode(NOT_EMPTY);
            validatorResult.setMessage(message);
        }
        return validatorResult;
    }
}

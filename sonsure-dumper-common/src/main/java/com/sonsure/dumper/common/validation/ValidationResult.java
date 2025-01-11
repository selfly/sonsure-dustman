/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.common.validation;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 验证结果
 *
 * @author liyd
 */
public class ValidationResult {

    /**
     * 是否成功
     */
    @Setter
    private boolean isSuccess;

    /**
     * 验证错误
     */
    @Getter
    private List<ValidationError> errors;

    public ValidationResult(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    /**
     * 添加错误
     *
     * @param error 错误
     */
    public void addError(ValidationError error) {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(error);
    }

}

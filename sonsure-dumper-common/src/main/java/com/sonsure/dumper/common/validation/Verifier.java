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

import java.util.*;

/**
 * @author liyd
 * @since 17/1/23
 */
public final class Verifier {

    /**
     * 校验器
     */
    private final List<ValidatorElement> validatorElements;

    public Verifier() {
        validatorElements = new ArrayList<>();
    }

    public static <T> List<String> jsrValidate(T t, Class<?>... groups) {
        return JsrValidator.validate(t, groups);
    }

    public static <T> List<String> jsrValidate(T t, boolean throwsExp, Class<?>... groups) {
        return JsrValidator.validate(t, throwsExp, groups);
    }

    public static Verifier init() {
        return new Verifier();
    }

    public Verifier notNull(Object value, String message) {
        ValidatorElement validatorElement = new ValidatorElement(value, message, new NotNullValidator());
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier notBlank(String value, String message) {
        ValidatorElement validatorElement = new ValidatorElement(value, message, new StringValidator());
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier notEmpty(Object value, String message) {
        ValidatorElement validatorElement = new ValidatorElement(value, message, new NotEmptyValidator());
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier eachElNotNull(Collection<?> collection, String message) {
        ValidatorElement validatorElement = new ValidatorElement(collection, message, new CollectionEachNotNullValidator());
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier thanTrue(boolean value, String message, Object... args) {
        ValidatorElement validatorElement = new ValidatorElement(value, message, args, new BooleanValidator(true));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier thanFalse(boolean value, String message, Object... args) {
        ValidatorElement validatorElement = new ValidatorElement(value, message, args, new BooleanValidator(false));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier regexMatch(String value, String regex, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new String[]{value, regex}, message,
                new RegexValidator());
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier with(boolean b) {
        if (!b) {
            removeLastValidationElement();
        }
        return this;
    }

    /**
     * 单独指定错误码
     *
     * @param errorCode the error code
     * @return verifier
     */
    public Verifier errorCode(String errorCode) {
        getLastValidationElement().setErrorCode(errorCode);
        return this;
    }

    public Verifier errorMessage(String message) {
        getLastValidationElement().setErrorMsg(message);
        return this;
    }

    public Verifier validate() {
        result(true);
        return this;
    }

    public ValidationResult result() {
        return result(false);
    }

    private ValidationResult result(boolean invalidFast) {

        ValidationResult result = new ValidationResult(true);

        for (ValidatorElement validatorElement : validatorElements) {

            ValidatorResult validatorResult = validatorElement.validate();
            if (!validatorResult.isSuccess()) {
                if (invalidFast) {
                    throw new ValidationException(validatorResult.getCode(), validatorResult.getMessage());
                } else {
                    ValidationError validationError = new ValidationError();
                    validationError.setErrorCode(validatorResult.getCode());
                    validationError.setErrorMsg(validatorResult.getMessage());
                    validationError.setInvalidValue(validatorElement.getValue());
                    result.setSuccess(false);
                    result.addError(validationError);
                }
            }
        }
        validatorElements.clear();
        return result;
    }

    private ValidatorElement getLastValidationElement() {
        if (validatorElements.isEmpty()) {
            throw new ValidationException("请先设置需要校验的元素");
        }
        return validatorElements.get(validatorElements.size() - 1);
    }

    private void removeLastValidationElement() {
        if (validatorElements.isEmpty()) {
            throw new ValidationException("请先设置需要校验的元素");
        }
        validatorElements.remove(validatorElements.size() - 1);
    }

}

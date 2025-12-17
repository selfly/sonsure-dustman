/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.common.validation;


import com.sonsure.dustman.common.enums.BaseEnum;
import com.sonsure.dustman.common.exception.ValidationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    public Verifier notNull(Object value, String code, String message) {
        ValidatorElement validatorElement = new ValidatorElement(value, code, message, new NotNullValidator());
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier notNull(Object value, String message) {
        return this.notNull(value, null, message);
    }

    public Verifier notNull(Object value, BaseEnum baseEnum) {
        return this.notNull(value, baseEnum.getCode(), baseEnum.getName());
    }

    public Verifier notBlank(String value, String code, String message) {
        ValidatorElement validatorElement = new ValidatorElement(value, code, message, new StringValidator());
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier notBlank(String value, String message) {
        return this.notBlank(value, null, message);
    }

    public Verifier notBlank(String value, BaseEnum baseEnum) {
        return this.notBlank(value, baseEnum.getCode(), baseEnum.getName());
    }

    public Verifier notEmpty(Object value, String code, String message) {
        ValidatorElement validatorElement = new ValidatorElement(value, code, message, new NotEmptyValidator());
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier notEmpty(Object value, String message) {
        return this.notEmpty(value, null, message);
    }

    public Verifier notEmpty(Object value, BaseEnum baseEnum) {
        return this.notEmpty(value, baseEnum.getCode(), baseEnum.getName());
    }

    public Verifier eachNotNull(Collection<?> collection, String code, String message) {
        ValidatorElement validatorElement = new ValidatorElement(collection, code, message, new CollectionEachNotNullValidator());
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier eachNotNull(Collection<?> collection, String message) {
        return this.eachNotNull(collection, null, message);
    }

    public Verifier eachNotNull(Collection<?> collection, BaseEnum baseEnum) {
        return this.eachNotNull(collection, baseEnum.getCode(), baseEnum.getName());
    }

    public Verifier expectTrue(boolean value, String code, String message, Object... args) {
        ValidatorElement validatorElement = new ValidatorElement(value, code, message, args, new BooleanValidator(true));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier expectTrue(boolean value, String message, Object... args) {
        return this.expectTrue(value, null, message, args);
    }

    public Verifier expectTrue(boolean value, BaseEnum baseEnum, Object... args) {
        return this.expectTrue(value, baseEnum.getCode(), baseEnum.getName(), args);
    }

    public Verifier expectFalse(boolean value, String code, String message, Object... args) {
        ValidatorElement validatorElement = new ValidatorElement(value, code, message, args, new BooleanValidator(false));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier expectFalse(boolean value, String message, Object... args) {
        return this.expectFalse(value, null, message, args);
    }

    public Verifier expectFalse(boolean value, BaseEnum baseEnum, Object... args) {
        return this.expectFalse(value, baseEnum.getCode(), baseEnum.getName(), args);
    }

    public Verifier regexMatch(String value, String regex, String code, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new String[]{value, regex}, code, message, new RegexValidator());
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier regexMatch(String value, String regex, String message) {
        return this.regexMatch(value, regex, null, message);
    }

    public Verifier regexMatch(String value, String regex, BaseEnum baseEnum) {
        return this.regexMatch(value, regex, baseEnum.getCode(), baseEnum.getName());
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
        getLastValidationElement().setParsedErrorMessage(message);
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

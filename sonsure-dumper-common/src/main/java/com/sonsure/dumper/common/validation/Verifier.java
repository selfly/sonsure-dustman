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
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author liyd
 * @date 17/1/23
 */
public final class Verifier {

    /**
     * 校验的元素信息
     */
    private final List<ValidatorElement> validatorElements;

    public Verifier() {
        validatorElements = new ArrayList<>();
    }

    public static void assertNotNull(Object obj, String message) {
        Verifier.init().isNotNull(obj, message).validate();
    }

    public static void assertNotBlank(String str, String message) {
        Verifier.init().isNotBlank(str, message).validate();
    }

    public static Verifier init() {
        return new Verifier();
    }

    public Verifier isNotNull(Object value, String message) {
        ValidatorElement validatorElement = new ValidatorElement(value, message, new NotNullValidator());
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier isNotEmpty(Object value, String message) {
        ValidatorElement validatorElement = new ValidatorElement(value, message, new NotEmptyValidator());
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier isNotBlank(String value, String message) {
        ValidatorElement validatorElement = new ValidatorElement(value, message,
                new StringValidator(StringValidator.NOT_BLANK));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier isMinLength(String value, int minLength, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, minLength}, message,
                new StringValidator(StringValidator.MIN_LENGTH));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier isMaxLength(String value, int maxLength, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, maxLength}, message,
                new StringValidator(StringValidator.MAX_LENGTH));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier isArrMinLength(Object value, int minLength, String message) {
        int length = ArrayUtils.getLength(value);
        this.isGtEq(length, minLength, message);
        return this;
    }

    public Verifier isArrMaxLength(Object value, int maxLength, String message) {
        int length = ArrayUtils.getLength(value);
        this.isLtEq(length, maxLength, message);
        return this;
    }

    public Verifier isEqLength(String value, int maxLength, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, maxLength}, message,
                new StringValidator(StringValidator.EQ_LENGTH));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier isEmpty(Collection<?> value, String message) {
        ValidatorElement validatorElement = new ValidatorElement(value, message,
                new CollectionValidator(CollectionValidator.IS_EMPTY));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier isFalse(Boolean bool, String message) {
        ValidatorElement validatorElement = new ValidatorElement(bool, message,
                new BooleanValidator(false));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier isTrue(Boolean bool, String message) {
        ValidatorElement validatorElement = new ValidatorElement(bool, message,
                new BooleanValidator(true));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier isMinSize(Collection<?> value, int minSize, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, minSize}, message,
                new CollectionValidator(CollectionValidator.MIN_SIZE));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier isMaxSize(Collection<?> value, int maxSize, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, maxSize}, message,
                new CollectionValidator(CollectionValidator.MAX_SIZE));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier isEqSize(Collection<?> value, int eqSize, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, eqSize}, message,
                new CollectionValidator(CollectionValidator.EQ_SIZE));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier isEq(String value, String expectVal, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, expectVal}, message,
                new StringValidator(StringValidator.MUST_EQ));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier isNotEq(String value, String expectVal, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, expectVal}, message,
                new StringValidator(StringValidator.NOT_EQ));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier isEqIgnoreCase(String value, String expectVal, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, expectVal}, message,
                new StringValidator(StringValidator.MUST_EQ_IGNORE_CASE));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier isNotEqIgnoreCase(String value, String expectVal, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, expectVal}, message,
                new StringValidator(StringValidator.NOT_EQ_IGNORE_CASE));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier isGtThan(long value, long expectVal, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, expectVal}, message,
                new NumberValidator(NumberValidator.GT));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier isGtEq(long value, long expectVal, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, expectVal}, message,
                new NumberValidator(NumberValidator.GT_EQ));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier isLtThan(long value, long expectVal, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, expectVal}, message,
                new NumberValidator(NumberValidator.LT));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier isLtEq(long value, long expectVal, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, expectVal}, message,
                new NumberValidator(NumberValidator.LT_EQ));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier isEq(long value, long expectVal, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, expectVal}, message,
                new NumberValidator(NumberValidator.EQ));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier isRegexMatch(String value, String regex, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new String[]{value, regex}, message,
                new RegexValidator());
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier isEachNotNull(Collection<?> collection, String message) {
        ValidatorElement validatorElement = new ValidatorElement(collection, message,
                new CollectionEachNotNullValidator());
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
                    validationError.setErrorCode(validatorResult.getCode())
                            .setErrorMsg(validatorResult.getMessage())
                            .setName(validatorElement.getValidateName())
                            .setInvalidValue(validatorElement.getValidateValue());

                    result.setIsSuccess(false);
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

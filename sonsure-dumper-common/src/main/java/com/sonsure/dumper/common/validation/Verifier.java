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

import java.util.*;

/**
 * @author liyd
 * @since 17/1/23
 */
public final class Verifier {

    /**
     * 校验的元素信息
     */
    private final List<ValidatorElement> validatorElements;

    public Verifier() {
        validatorElements = new ArrayList<>();
    }

    public static <T> List<String> validate(T t, Class<?>... groups) {
        return JsrValidator.validate(t, groups);
    }

    public static <T> List<String> validate(T t, boolean throwsExp, Class<?>... groups) {
        return JsrValidator.validate(t, throwsExp, groups);
    }

    public static void assertNotNull(Object obj, String message) {
        Verifier.init().notNull(obj, message).validate();
    }

    public static void assertNotBlank(String str, String message) {
        Verifier.init().notBlank(str, message).validate();
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
        ValidatorElement validatorElement = new ValidatorElement(value, message,
                new StringValidator(StringValidator.NOT_BLANK));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier notEmpty(Object value, String message) {
        ValidatorElement validatorElement = new ValidatorElement(value, message, new NotEmptyValidator());
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier eachElNotNull(Collection<?> collection, String message) {
        ValidatorElement validatorElement = new ValidatorElement(collection, message,
                new CollectionEachNotNullValidator());
        validatorElements.add(validatorElement);
        return this;
    }


    public Verifier minLength(String value, int minLength, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, minLength}, message,
                new StringValidator(StringValidator.MIN_LENGTH));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier maxLength(String value, int maxLength, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, maxLength}, message,
                new StringValidator(StringValidator.MAX_LENGTH));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier minArrLength(Object value, int minLength, String message) {
        int length = ArrayUtils.getLength(value);
        this.gtEq(length, minLength, message);
        return this;
    }

    public Verifier maxArrLength(Object value, int maxLength, String message) {
        int length = ArrayUtils.getLength(value);
        this.ltEq(length, maxLength, message);
        return this;
    }

    public Verifier eqLength(String value, int maxLength, String message) {
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

    public Verifier minSize(Collection<?> value, int minSize, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, minSize}, message,
                new CollectionValidator(CollectionValidator.MIN_SIZE));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier maxSize(Collection<?> value, int maxSize, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, maxSize}, message,
                new CollectionValidator(CollectionValidator.MAX_SIZE));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier eqSize(Collection<?> value, int eqSize, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, eqSize}, message,
                new CollectionValidator(CollectionValidator.EQ_SIZE));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier eq(String value, String expectVal, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, expectVal}, message,
                new StringValidator(StringValidator.MUST_EQ));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier notEq(String value, String expectVal, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, expectVal}, message,
                new StringValidator(StringValidator.NOT_EQ));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier eqIgnoreCase(String value, String expectVal, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, expectVal}, message,
                new StringValidator(StringValidator.MUST_EQ_IGNORE_CASE));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier notEqIgnoreCase(String value, String expectVal, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, expectVal}, message,
                new StringValidator(StringValidator.NOT_EQ_IGNORE_CASE));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier gtThan(long value, long expectVal, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, expectVal}, message,
                new NumberValidator(NumberValidator.GT));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier gtEq(long value, long expectVal, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, expectVal}, message,
                new NumberValidator(NumberValidator.GT_EQ));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier ltThan(long value, long expectVal, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, expectVal}, message,
                new NumberValidator(NumberValidator.LT));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier ltEq(long value, long expectVal, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, expectVal}, message,
                new NumberValidator(NumberValidator.LT_EQ));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier eq(long value, long expectVal, String message) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, expectVal}, message,
                new NumberValidator(NumberValidator.EQ));
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

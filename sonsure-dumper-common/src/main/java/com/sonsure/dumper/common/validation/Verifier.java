/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.common.validation;


import com.sonsure.dumper.common.enums.BaseEnum;
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
        validatorElements = new ArrayList<ValidatorElement>();
    }

    public static void assertNotNull(Object obj, String message) {
        Verifier.init().notNull(obj, "").errorMsg(message).validate();
    }

    public static void assertNotBlank(String str, String message) {
        Verifier.init().notBlank(str, "").errorMsg(message).validate();
    }

    public static Verifier init() {
        return new Verifier();
    }

    public Verifier notNull(Object value, String name) {
        ValidatorElement validatorElement = new ValidatorElement(value, name, new NotNullValidator());
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier notEmpty(Object value, String name) {
        ValidatorElement validatorElement = new ValidatorElement(value, name, new NotEmptyValidator());
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier notBlank(String value, String name) {
        ValidatorElement validatorElement = new ValidatorElement(value, name,
                new StringValidator(StringValidator.NOT_BLANK[0]));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier minLength(String value, int minLength, String name) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, minLength}, name,
                new StringValidator(StringValidator.MIN_LENGTH[0]));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier maxLength(String value, int maxLength, String name) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, maxLength}, name,
                new StringValidator(StringValidator.MAX_LENGTH[0]));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier minArrayLength(Object value, int minLength, String name) {
        int length = ArrayUtils.getLength(value);
        this.gtEq(length, minLength, name);
        return this;
    }

    public Verifier maxArrayLength(Object value, int maxLength, String name) {
        int length = ArrayUtils.getLength(value);
        this.ltEq(length, maxLength, name);
        return this;
    }

    public Verifier eqLength(String value, int maxLength, String name) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, maxLength}, name,
                new StringValidator(StringValidator.EQ_LENGTH[0]));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier mustEmpty(Collection<?> value, String name) {
        ValidatorElement validatorElement = new ValidatorElement(value, name,
                new CollectionValidator(CollectionValidator.MUST_EMPTY[0]));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier mustFalse(Boolean bool, String name) {
        ValidatorElement validatorElement = new ValidatorElement(bool, name,
                new BooleanValidator(BooleanValidator.FALSE[0]));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier mustTrue(Boolean bool, String name) {
        ValidatorElement validatorElement = new ValidatorElement(bool, name,
                new BooleanValidator(BooleanValidator.TRUE[0]));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier minSize(Collection<?> value, int minSize, String name) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, minSize}, name,
                new CollectionValidator(CollectionValidator.MIN_SIZE[0]));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier maxSize(Collection<?> value, int maxSize, String name) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, maxSize}, name,
                new CollectionValidator(CollectionValidator.MAX_SIZE[0]));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier eqSize(Collection<?> value, int eqSize, String name) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, eqSize}, name,
                new CollectionValidator(CollectionValidator.EQ_SIZE[0]));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier gtThan(int value, int expectVal, String name) {
        gtThan((long) value, (long) expectVal, name);
        return this;
    }

    public Verifier gtEq(int value, int expectVal, String name) {
        gtEq((long) value, (long) expectVal, name);
        return this;
    }

    public Verifier ltThan(int value, int expectVal, String name) {
        ltThan((long) value, (long) expectVal, name);
        return this;
    }

    public Verifier ltEq(int value, int expectVal, String name) {
        ltEq((long) value, (long) expectVal, name);
        return this;
    }

    public Verifier mustEq(int value, int expectVal, String name) {
        mustEq((long) value, (long) expectVal, name);
        return this;
    }

    public Verifier mustEq(String value, String expectVal, String name) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, expectVal}, name,
                new StringValidator(StringValidator.MUST_EQ[0]));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier notEq(String value, String expectVal, String name) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, expectVal}, name,
                new StringValidator(StringValidator.NOT_EQ[0]));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier mustEqIgnoreCase(String value, String expectVal, String name) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, expectVal}, name,
                new StringValidator(StringValidator.MUST_EQ_IGNORE_CASE[0]));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier gtThan(long value, long expectVal, String name) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, expectVal}, name,
                new NumberValidator(NumberValidator.GT[0]));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier gtEq(long value, long expectVal, String name) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, expectVal}, name,
                new NumberValidator(NumberValidator.GT_EQ[0]));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier ltThan(long value, long expectVal, String name) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, expectVal}, name,
                new NumberValidator(NumberValidator.LT[0]));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier ltEq(long value, long expectVal, String name) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, expectVal}, name,
                new NumberValidator(NumberValidator.LT_EQ[0]));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier mustEq(long value, long expectVal, String name) {
        ValidatorElement validatorElement = new ValidatorElement(new Object[]{value, expectVal}, name,
                new NumberValidator(NumberValidator.EQ[0]));
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier regex(String value, String regex, String name) {
        ValidatorElement validatorElement = new ValidatorElement(new String[]{value, regex}, name,
                new RegexValidator());
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier eachNotNull(Collection<?> collection, String name) {
        ValidatorElement validatorElement = new ValidatorElement(collection, name,
                new CollectionEachNotNullValidator());
        validatorElements.add(validatorElement);
        return this;
    }

    public Verifier on(Object value, String name, Validator validator) {
        ValidatorElement validatorElement = new ValidatorElement(value, name, validator);
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

    /**
     * 单独指定错误信息
     *
     * @param errorMsg the error msg
     * @return verifier
     */
    public Verifier errorMsg(String errorMsg) {
        getLastValidationElement().setErrorMsg(errorMsg);
        return this;
    }

    /**
     * 单独指定错误信息
     *
     * @param baseEnum the enum
     * @return verifier
     */
    public Verifier error(BaseEnum baseEnum) {
        ValidatorElement lastValidationElement = getLastValidationElement();
        lastValidationElement.setErrorCode(baseEnum.getCode());
        lastValidationElement.setErrorMsg(baseEnum.getDesc());
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

    private ValidatorElement removeLastValidationElement() {
        if (validatorElements.isEmpty()) {
            throw new ValidationException("请先设置需要校验的元素");
        }
        return validatorElements.remove(validatorElements.size() - 1);
    }

}

/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.common.validation;

import com.sonsure.dumper.common.utils.UUIDUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author liyd
 * @since  17/1/23
 */
public class ValidatorElement {

    private static final String PREFIX = "ss.";

    /**
     * 验证器
     */
    private Validator validator;

    /**
     * 待验证对象
     */
    private Object validateValue;

    /**
     * 校验对象的名称 一般中文备注名 非属性名
     */
    private String validateName;

    /**
     * 指定的错误码
     */
    private String errorCode;

    /**
     * 指定的错误信息
     */
    private String errorMsg;

    /**
     * create
     *
     * @param validateValue 待验证对象
     * @param validateName  the validate name
     * @param validator     验证器
     */
    public ValidatorElement(Object validateValue, String validateName, Validator validator) {
        this.validateValue = validateValue;
        this.validateName = validateName;
        this.validator = validator;
    }

    public ValidatorResult validate() {
        ValidatorResult result = this.getValidator().validate(this.validateValue, this.validateName);
        ValidatorResult theResult = new ValidatorResult(result.isSuccess());
        if (!result.isSuccess()) {
            //为空，自动生成一个唯一code
            if (StringUtils.isBlank(errorCode)) {
                this.errorCode = new StringBuilder(PREFIX)
                        .append(result.getCode())
                        .append(".")
                        .append(UUIDUtils.getUUID16(result.getMessage().getBytes()))
                        .toString();
            }
            if (StringUtils.isBlank(errorMsg)) {
                this.errorMsg = result.getMessage();
            }
            theResult.setCode(this.errorCode);
            theResult.setMessage(this.errorMsg);
        }
        return theResult;
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public Object getValidateValue() {
        return validateValue;
    }

    public String getValidateName() {
        return validateName;
    }

    public void setValidateName(String validateName) {
        this.validateName = validateName;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}

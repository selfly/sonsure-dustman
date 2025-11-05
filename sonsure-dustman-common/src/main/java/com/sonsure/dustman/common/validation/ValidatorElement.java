/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.common.validation;

import com.sonsure.dustman.common.utils.StrUtils;
import com.sonsure.dustman.common.utils.UUIDUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * @author liyd
 * @since 17/1/23
 */
@Getter
@Setter
public class ValidatorElement {

    private static final String PREFIX = "ss.";

    /**
     * 验证器
     */
    private Validator validator;

    /**
     * 待验证对象
     */
    private Object value;

    /**
     * 指定的校验信息
     */
    private String message;

    /**
     * 消息需要格式化时的参数
     */
    private Object[] messageArgs;

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
     * @param value     the value
     * @param message   the message
     * @param validator 验证器
     */
    public ValidatorElement(Object value, String message, Validator validator) {
        this(value, message, null, validator);
    }

    public ValidatorElement(Object value, String message, Object[] msgArgs, Validator validator) {
        this.value = value;
        this.message = message;
        this.messageArgs = msgArgs;
        this.validator = validator;
    }

    public ValidatorResult validate() {
        ValidatorResult result = this.getValidator().validate(this.getValue(), this.getMessage(), this.getMessageArgs());
        ValidatorResult theResult = new ValidatorResult(result.isSuccess());
        if (!result.isSuccess()) {
            //为空，自动生成一个唯一code
            if (StrUtils.isBlank(errorCode)) {
                this.errorCode = new StringBuilder(PREFIX)
                        .append(result.getCode())
                        .append(".")
                        .append(UUIDUtils.getUUID16(result.getMessage().getBytes()))
                        .toString();
            }
            if (StrUtils.isBlank(errorMsg)) {
                this.errorMsg = result.getMessage();
            }
            theResult.setCode(this.errorCode);
            theResult.setMessage(this.errorMsg);
        }
        return theResult;
    }

}

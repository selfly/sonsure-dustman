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

/**
 * 内部使用的验证结果包含的错误
 *
 * @author selfly
 */
@Getter
@Setter
public class ValidationError {

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     * 错误值
     */
    private Object invalidValue;

    @Override
    public String toString() {
        return "ValidationError{ errorCode=" + errorCode + ", errorMsg=" + errorMsg
               + ", invalidValue=" + invalidValue + " }";
    }

}

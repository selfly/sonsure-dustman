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
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;

/**
 * Number验证
 * <p>
 *
 * @author liyd
 * @date 17/1/24
 */
public class NumberValidator implements Validator {

    /**
     * 表示大于
     */
    public static final String GT = PREFIX + "number.must.gt";

    /**
     * 表示小于
     */
    public static final String LT = PREFIX + "number.must.lt";

    /**
     * 表示大于等于
     */
    public static final String GT_EQ = PREFIX + "number.must.gt.eq";

    /**
     * 表示小于等于
     */
    public static final String LT_EQ = PREFIX + "number.must.lt.eq";

    /**
     * 表示等于
     */
    public static final String EQ = PREFIX + "number.must.eq";

    private final String code;

    public NumberValidator(String code) {
        this.code = code;
    }

    @Override
    public ValidatorResult validate(Object value, String message) {
        Object[] values = (Object[]) value;
        BigDecimal val = new BigDecimal(String.valueOf(values[0]));
        BigDecimal expectVal = new BigDecimal(String.valueOf(values[1]));
        int i = val.compareTo(expectVal);
        ValidatorResult result = new ValidatorResult(false);
        String resultMsg;
        if (StringUtils.equals(code, GT)) {
            result.setSuccess(i > 0);
            resultMsg = MessageFormat.format(message, expectVal);
        } else if (StringUtils.equals(code, LT)) {
            result.setSuccess(i < 0);
            resultMsg = MessageFormat.format(message, expectVal);
        } else if (StringUtils.equals(code, GT_EQ)) {
            result.setSuccess(i >= 0);
            resultMsg = MessageFormat.format(message, expectVal);
        } else if (StringUtils.equals(code, LT_EQ)) {
            result.setSuccess(i <= 0);
            resultMsg = MessageFormat.format(message, expectVal);
        } else if (StringUtils.equals(code, EQ)) {
            result.setSuccess(i == 0);
            resultMsg = MessageFormat.format(message, expectVal);
        } else {
            throw new ValidationException("不支持的数字对比操作");
        }
        if (!result.isSuccess()) {
            result.setCode(code);
            result.setMessage(resultMsg);
        }
        return result;
    }
}

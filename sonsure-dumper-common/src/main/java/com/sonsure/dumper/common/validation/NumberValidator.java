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
    public static final String[] GT = {PREFIX + "number.must.gt", "{0}必须大于{1}"};

    /**
     * 表示小于
     */
    public static final String[] LT = {PREFIX + "number.must.lt", "{0}必须小于{1}"};

    /**
     * 表示大于等于
     */
    public static final String[] GT_EQ = {PREFIX + "number.must.gt.eq", "{0}必须大于等于{1}"};

    /**
     * 表示小于等于
     */
    public static final String[] LT_EQ = {PREFIX + "number.must.lt.eq", "{0}必须小于等于{1}"};

    /**
     * 表示等于
     */
    public static final String[] EQ = {PREFIX + "number.must.eq", "{0}必须等于{1}"};

    private final String type;

    public NumberValidator(String type) {
        this.type = type;
    }

    @Override
    public ValidatorResult validate(Object value, String validateName) {
        Object[] values = (Object[]) value;
        BigDecimal val = new BigDecimal(String.valueOf(values[0]));
        BigDecimal expectVal = new BigDecimal(String.valueOf(values[1]));
        int i = val.compareTo(expectVal);
        ValidatorResult result = new ValidatorResult(false);
        if (StringUtils.equals(type, GT[0])) {
            result.setSuccess(i > 0);
            result.setCode(GT[0]);
            result.setMessage(MessageFormat.format(GT[1], validateName, expectVal));
            return result;
        } else if (StringUtils.equals(type, LT[0])) {
            result.setSuccess(i < 0);
            result.setCode(LT[0]);
            result.setMessage(MessageFormat.format(LT[1], validateName, expectVal));
            return result;
        } else if (StringUtils.equals(type, GT_EQ[0])) {
            result.setSuccess(i >= 0);
            result.setCode(GT_EQ[0]);
            result.setMessage(MessageFormat.format(GT_EQ[1], validateName, expectVal));
            return result;
        } else if (StringUtils.equals(type, LT_EQ[0])) {
            result.setSuccess(i <= 0);
            result.setCode(LT_EQ[0]);
            result.setMessage(MessageFormat.format(LT_EQ[1], validateName, expectVal));
            return result;
        } else if (StringUtils.equals(type, EQ[0])) {
            result.setSuccess(i == 0);
            result.setCode(EQ[0]);
            result.setMessage(MessageFormat.format(EQ[1], validateName, expectVal));
            return result;
        } else {
            throw new ValidationException("不支持的数字对比操作");
        }
    }
}

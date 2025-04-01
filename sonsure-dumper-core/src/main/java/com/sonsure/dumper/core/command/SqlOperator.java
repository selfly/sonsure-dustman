/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command;

import com.sonsure.dumper.core.exception.SonsureJdbcException;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author selfly
 */

@Getter
public enum SqlOperator {

    /**
     * Eq comparison.
     */
    EQ("="),

    NEQ("!="),

    GT(">"),

    GTE(">="),

    LT("<"),

    LTE("<="),

    IS("is"),

    IS_NOT("is not"),

    LIKE("like"),

    IN("in"),

    NOT_IN("not in");

    private final String code;

    SqlOperator(String code) {
        this.code = code;
    }

    public static SqlOperator of(String code) {
        for (SqlOperator value : values()) {
            if (value.getCode().equalsIgnoreCase(StringUtils.trim(code))) {
                return value;
            }
        }
        throw new SonsureJdbcException("不支持的sql操作符:" + code);
    }

}

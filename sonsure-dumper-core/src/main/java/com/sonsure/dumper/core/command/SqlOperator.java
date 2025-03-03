/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command;

import lombok.Getter;

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

    IN("in"),

    IS("is"),

    NOT_IN("not in");

    private final String code;

    SqlOperator(String code) {
        this.code = code;
    }

}

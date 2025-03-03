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
public enum SqlKeyword {

    /**
     * Eq comparison.
     */
    IS("is"),

  ;

    private final String code;

    SqlKeyword(String code) {
        this.code = code;
    }

}

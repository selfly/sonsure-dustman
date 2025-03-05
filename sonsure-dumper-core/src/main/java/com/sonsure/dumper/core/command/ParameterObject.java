/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   https://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command;

import lombok.Getter;

/**
 * @author liyd
 */
@Getter
public class ParameterObject {

    /**
     * The Name.
     */
    String name;

    /**
     * The Value.
     */
    Object value;

    public ParameterObject(String name, Object value) {
        this.name = name;
        this.value = value;
    }

}

/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   https://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.build;

import lombok.Getter;

/**
 * @author liyd
 */
@Getter
public class BeanParameter {

    private final Object bean;

    public BeanParameter(Object bean) {
        this.bean = bean;
    }

}

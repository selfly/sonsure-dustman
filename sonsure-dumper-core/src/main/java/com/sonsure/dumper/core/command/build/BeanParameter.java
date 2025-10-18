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
import lombok.Setter;

/**
 * @author liyd
 */
@Setter
@Getter
public class BeanParameter {

    private Object bean;

    public BeanParameter() {
    }

    public BeanParameter(Object bean) {
        this.bean = bean;
    }

}

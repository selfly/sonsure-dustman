/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.management;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liyd
 */
@Setter
@Getter
public class CommandClass {

    /**
     * 对应实体类
     */
    private Class<?> cls;

    /**
     * 表别名
     */
    private String aliasName;

    public CommandClass(Class<?> cls) {
        this(cls, null);
    }

    public CommandClass(Class<?> cls, String aliasName) {
        this.cls = cls;
        this.aliasName = aliasName;
    }

}

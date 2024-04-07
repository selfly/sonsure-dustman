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
import lombok.Setter;

@Getter
@Setter
public class GenerateKey {

    private Class<?> clazz;

    private String column;

    private Object value;

    /**
     * 主键是否是参数名称
     * 例如oracle主键传的是序列名称，非值
     */
    private boolean pkIsParamName;

}

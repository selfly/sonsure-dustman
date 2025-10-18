/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.build;

import lombok.Getter;
import lombok.Setter;

/**
 * @author selfly
 */
@Getter
@Setter
public class GenerateKey {

    private String column;

    private Object value;

    /**
     * 主键是否是传参的参数
     * 例如oracle主键传的是序列名称，拼接在sql当中，非传参 为 false
     */
    private boolean primaryKeyParameter;

}

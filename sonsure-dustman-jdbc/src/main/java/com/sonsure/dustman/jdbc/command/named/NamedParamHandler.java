/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   https://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.jdbc.command.named;

/**
 * @author liyd
 */
public interface NamedParamHandler {

    /**
     * named方式时支持自定义生成参数值，例如随机数，
     * 返回的值只能是支持jdbc传参的简单类型，不能是list、数组等复杂对象
     *
     * @param paramName the param name
     * @return the value
     */
    Object getValue(String paramName);
}

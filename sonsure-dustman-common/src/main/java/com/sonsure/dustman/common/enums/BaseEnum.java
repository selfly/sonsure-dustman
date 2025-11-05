/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.common.enums;

/**
 * 枚举接口
 * <p>
 * User: liyd
 * Date: 14-1-13
 * Time: 下午4:13
 * @author selfly
 */
public interface BaseEnum {

    /**
     * 获取枚举编码
     *
     * @return code
     */
    String getCode();

    /**
     * 获取枚举名称
     *
     * @return desc
     */
    String getName();
}

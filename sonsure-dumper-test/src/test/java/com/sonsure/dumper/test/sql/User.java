/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.sql;

import com.sonsure.commons.model.Pageable;

public class User extends Pageable {


    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 登录名
     */
    private String loginName;

}

/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.model;


import com.sonsure.dumper.common.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 用户
 * <p>
 * UserInfo: liyd
 * Date: Wed Dec 24 16:46:48 CST 2014
 */
@Setter
@Getter
public class UserInfo extends BaseEntity {

    private static final long serialVersionUID = 8166785520231287816L;

    /**
     * 用户id
     */
    private Long userInfoId;

    /**
     * 登录名
     */
    private String loginName;

    /**
     * 密码
     */
    private String password;

    /**
     * 年龄
     */
    private Integer userAge;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModify;


}

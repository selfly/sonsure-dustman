/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户
 * <p>
 * UserInfo: liyd
 * Date: Wed Dec 24 16:46:48 CST 2014
 */
@Setter
@Getter
public class UserInfo extends BaseUser {

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
     * 用户类型
     */
    private String userType;

    /**
     * 状态
     */
    private String status;

    /**
     * 性別
     */
    private String gender;

    /**
     * 姓名
     */
    private String realName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机
     */
    private String mobile;

    /**
     * 头像图片
     */
    private String avatar;

    /**
     * 介绍
     */
    private String description;

}

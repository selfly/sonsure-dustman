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
import com.sonsure.dumper.core.annotation.Column;
import com.sonsure.dumper.core.annotation.Entity;
import com.sonsure.dumper.core.annotation.Id;
import com.sonsure.dumper.core.annotation.Transient;
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
@Entity("ktx_user_info")
public class AnnotationUserInfo extends BaseEntity {

    private static final long serialVersionUID = 8166785520231287816L;

    /**
     * 用户id
     */
    @Id
    @Column("USER_INFO_ID_")
    private Long rowId;

    /**
     * 登录名
     */
    @Column("LOGIN_NAME_")
    private String loginName;

    /**
     * 密码
     */
    @Column("PASSWORD_")
    private String password;

    /**
     * 年龄
     */
    @Column("USER_AGE_")
    private Integer userAge;

    /**
     * 创建时间
     */
    @Column("GMT_CREATE_")
    private Date gmtCreate;

    /**
     * 修改时间
     */
    @Column("GMT_MODIFY_")
    private Date gmtModify;

    @Transient
    private String testName;


}

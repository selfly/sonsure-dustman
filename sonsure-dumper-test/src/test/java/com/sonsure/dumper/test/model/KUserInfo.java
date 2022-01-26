/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.model;

import com.sonsure.commons.model.BaseEntity;
import com.sonsure.dumper.core.annotation.Column;
import com.sonsure.dumper.core.annotation.Entity;
import com.sonsure.dumper.core.annotation.Id;
import com.sonsure.dumper.core.annotation.Transient;

import java.util.Date;

/**
 * 用户
 * <p>
 * UserInfo: liyd
 * Date: Wed Dec 24 16:46:48 CST 2014
 */
@Entity("ktx_user_info")
public class KUserInfo extends BaseEntity {

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


    public Long getRowId() {
        return rowId;
    }

    public void setRowId(Long rowId) {
        this.rowId = rowId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getUserAge() {
        return userAge;
    }

    public void setUserAge(Integer userAge) {
        this.userAge = userAge;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }
}

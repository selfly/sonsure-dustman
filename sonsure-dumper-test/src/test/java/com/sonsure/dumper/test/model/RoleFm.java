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

import java.util.Date;

/**
 * 角色权限
 * <p>
 * Author: Created by code generator
 * Date: Mon Dec 04 11:30:56 CST 2017
 */
public class RoleFm extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 9109044429567101473L;

    /**
     * 主键id
     */
    private Long roleFmId;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 功能id
     */
    private Long funcMenuId;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 创建人
     */
    private String creator;

    public Long getRoleFmId() {
        return roleFmId;
    }

    public void setRoleFmId(Long roleFmId) {
        this.roleFmId = roleFmId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getFuncMenuId() {
        return funcMenuId;
    }

    public void setFuncMenuId(Long funcMenuId) {
        this.funcMenuId = funcMenuId;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}

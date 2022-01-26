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

public class FmAuth extends BaseEntity {

    private static final long serialVersionUID = -1360192395444996154L;

    /**
     * 主键
     */
    private Long fmAuthId;

    /**
     * 功能id
     */
    private Long funcMenuId;

    /**
     * 权限码id
     */
    private Long authCodeId;

    /**
     * 创建时间
     */
    private Date gmtCreate;


    public Long getFmAuthId() {
        return fmAuthId;
    }

    public void setFmAuthId(Long fmAuthId) {
        this.fmAuthId = fmAuthId;
    }

    public Long getFuncMenuId() {
        return funcMenuId;
    }

    public void setFuncMenuId(Long funcMenuId) {
        this.funcMenuId = funcMenuId;
    }

    public Long getAuthCodeId() {
        return authCodeId;
    }

    public void setAuthCodeId(Long authCodeId) {
        this.authCodeId = authCodeId;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }
}

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

/**
 * 权限码
 * <p>
 * Author: Created by code generator
 * Date: Wed Oct 11 19:05:41 CST 2017
 */
public class AuthCode extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5106990881876865412L;

    /**
     * 主键id
     */
    private Long authCodeId;

    /**
     * 权限编码
     */
    private String code;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 排序
     */
    private Integer orderNo;

    /**
     * 权限说明
     */
    private String description;

    /**
     * 权限分类
     */
    private String category;


    public static class Constant {

        /**
         * 权限分类数据字典key
         */
        public static final String AUTH_CATEGORY_DIC_KEY = "auth_code_category";

    }


    public Long getAuthCodeId() {
        return authCodeId;
    }

    public void setAuthCodeId(Long authCodeId) {
        this.authCodeId = authCodeId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

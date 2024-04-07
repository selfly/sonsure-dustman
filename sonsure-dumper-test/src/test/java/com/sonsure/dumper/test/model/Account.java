/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.model;


import com.sonsure.dumper.common.model.Pageable;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by liyd on 17/6/7.
 */
@Setter
@Getter
public class Account extends Pageable {

    private static final long serialVersionUID = -188967944235979673L;

    private Long accountId;

    private String loginName;

    private String accountName;

    private String password;

    private Integer userAge;

}

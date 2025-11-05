/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.test.model;


import com.sonsure.dustman.common.model.Pageable;
import com.sonsure.dustman.common.validation.ValidationGroup;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created by liyd on 17/6/7.
 */
@Setter
@Getter
public class ValidationModel extends Pageable {

    private static final long serialVersionUID = -188967944235979673L;

    @NotNull(message = "id不能为空",groups = ValidationGroup.Update.class)
    private Long modelId;

    @NotBlank(message = "name不能为空")
    private String ame;

    @NotBlank(message = "password不能为空")
    private String password;

    @NotNull(message = "age不能为空")
    private Integer age;

}

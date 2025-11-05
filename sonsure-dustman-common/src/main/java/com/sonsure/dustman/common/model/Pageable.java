/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.common.model;

import com.sonsure.dustman.common.utils.StrUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 分页等常用信息存储
 * <p/>
 *
 * @author liyd
 * @since 6/26/14
 */
@Setter
@Getter
public class Pageable implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 4060766214127186912L;

    /**
     * 每页显示条数
     */
    private int pageSize = 20;

    /**
     * 当前页码
     */
    private int pageNum = 1;

    @Override
    public String toString() {
        return StrUtils.reflectionToString(this);
    }
}

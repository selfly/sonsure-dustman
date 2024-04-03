/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.common.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 分页对象
 *
 * @param <T>
 * @author selfly
 */
@Setter
@Getter
public class Page<T> implements Serializable {

    private static final long serialVersionUID = -376015922238121976L;

    private List<T> list;

    private Pagination pagination;

    public Page() {

    }

    public Page(Pagination pagination) {
        this(null, pagination);
    }

    public Page(List<T> list, Pagination pagination) {
        this.list = list;
        this.pagination = pagination;
    }

}

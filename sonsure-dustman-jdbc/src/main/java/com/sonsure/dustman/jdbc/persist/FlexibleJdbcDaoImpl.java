/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */
package com.sonsure.dustman.jdbc.persist;

import com.sonsure.dustman.jdbc.config.JdbcContext;

/**
 * The type Flexible dao template.
 *
 * @author liyd
 */
public class FlexibleJdbcDaoImpl extends AbstractJdbcDaoImpl {

    public FlexibleJdbcDaoImpl(JdbcContext jdbcContext) {
        this.setJdbcContext(jdbcContext);
    }

    @Override
    public JdbcDao use(String name) {
        throw new UnsupportedOperationException("不支持的方法");
    }

}

/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */
package com.sonsure.dumper.core.persist;

import com.sonsure.dumper.core.config.JdbcExecutor;

/**
 * The type Flexible dao template.
 *
 * @author liyd
 */
public class FlexibleJdbcDaoImpl extends AbstractJdbcDaoImpl {

    public FlexibleJdbcDaoImpl(JdbcExecutor jdbcExecutor) {
        this.setDefaultJdbcExecutor(jdbcExecutor);
    }

    @Override
    public JdbcDao use(String name) {
        throw new UnsupportedOperationException("不支持的方法");
    }

}

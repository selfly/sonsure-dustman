/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.mybatis;


import com.sonsure.dumper.core.command.simple.AbstractSimpleCommandContextBuilder;
import com.sonsure.dumper.core.command.simple.AbstractSimpleCommandExecutor;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

/**
 * @author liyd
 * @date 17/4/25
 */
public class MybatisExecutorImpl extends AbstractSimpleCommandExecutor<MybatisExecutor> implements MybatisExecutor {

    public MybatisExecutorImpl(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig, new MybatisCommandContextBuilderImpl(new AbstractSimpleCommandContextBuilder.Context()));
        this.getSimpleCommandContextBuilder().namedParameter();
    }
}

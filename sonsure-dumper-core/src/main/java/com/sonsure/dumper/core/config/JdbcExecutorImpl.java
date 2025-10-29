/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.config;

import com.sonsure.dumper.core.command.CommandExecutor;

import javax.sql.DataSource;

/**
 * The type Jdbc engine.
 *
 * @author liyd
 * @since 17 /4/12
 */
public class JdbcExecutorImpl implements JdbcExecutor {

    private final JdbcExecutorConfig jdbcExecutorConfig;

    public JdbcExecutorImpl(JdbcExecutorConfig jdbcExecutorConfig) {
        this.jdbcExecutorConfig = jdbcExecutorConfig;
    }

    @Override
    public JdbcExecutorConfig getConfig() {
        return jdbcExecutorConfig;
    }

    @Override
    public DataSource getDataSource() {
        return jdbcExecutorConfig.getDataSource();
    }

    @Override
    public String getDatabaseProduct() {
        return this.getConfig().getPersistExecutor().getDialect();
    }

    @Override
    public <T extends CommandExecutor<?>, M> T createExecutor(Class<T> commandExecutorClass, Class<M> modelClass) {
        return this.jdbcExecutorConfig.getCommandExecutorFactory().createCommandExecutor(commandExecutorClass, this.jdbcExecutorConfig, modelClass);
    }

}

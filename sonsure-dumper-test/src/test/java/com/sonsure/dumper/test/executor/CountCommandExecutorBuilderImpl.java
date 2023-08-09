/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.executor;

import com.sonsure.dumper.core.command.CommandExecutor;
import com.sonsure.dumper.core.config.AbstractCommandExecutorBuilder;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

public class CountCommandExecutorBuilderImpl extends AbstractCommandExecutorBuilder {

    @Override
    public boolean support(Class<? extends CommandExecutor> commandExecutorClass, JdbcEngineConfig jdbcEngineConfig) {
        return commandExecutorClass == CountCommandExecutor.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends CommandExecutor> T build(Class<T> commandExecutorClass, JdbcEngineConfig jdbcEngineConfig) {
        CountCommandExecutorImpl commandExecutor = new CountCommandExecutorImpl(jdbcEngineConfig);
        return (T) commandExecutor;
    }
}

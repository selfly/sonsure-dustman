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
    public <T extends CommandExecutor, M> T build(Class<T> commandExecutorClass, Class<M> modelClass, JdbcEngineConfig jdbcEngineConfig) {
        CountCommandExecutorImpl commandExecutor = new CountCommandExecutorImpl(jdbcEngineConfig);
        //noinspection unchecked
        return (T) commandExecutor;
    }
}

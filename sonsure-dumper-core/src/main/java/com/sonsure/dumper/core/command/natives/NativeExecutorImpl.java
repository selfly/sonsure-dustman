/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.natives;


import com.sonsure.dumper.core.command.simple.AbstractSimpleCommandExecutor;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

/**
 * The type Native executor.
 *
 * @author liyd
 * @since 17 /4/25
 */
public class NativeExecutorImpl extends AbstractSimpleCommandExecutor<NativeExecutor> implements NativeExecutor {


    public NativeExecutorImpl(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
    }

    @Override
    public NativeExecutor parameters(Object... values) {
        this.getExecutableCmdBuilder().addParameters(values);
        return this;
    }

}

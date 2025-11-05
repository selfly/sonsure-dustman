/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.jdbc.command.natives;


import com.sonsure.dustman.jdbc.command.simple.AbstractSimpleCommandExecutor;
import com.sonsure.dustman.jdbc.config.JdbcContext;

/**
 * The type Native executor.
 *
 * @author liyd
 * @since 17 /4/25
 */
public class NativeExecutorImpl extends AbstractSimpleCommandExecutor<NativeExecutor> implements NativeExecutor {


    public NativeExecutorImpl(JdbcContext jdbcContext) {
        super(jdbcContext);
    }

    @Override
    public NativeExecutor parameters(Object... values) {
        this.getExecutableCmdBuilder().addParameters(values);
        return this;
    }

}

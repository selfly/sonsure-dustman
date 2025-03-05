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
import com.sonsure.dumper.core.exception.SonsureJdbcException;

/**
 * The type Native executor.
 *
 * @author liyd
 * @date 17 /4/25
 */
public class NativeExecutorImpl extends AbstractSimpleCommandExecutor<NativeExecutor> implements NativeExecutor {

    private static final String DEFAULT_NATIVE_PARAM_PREFIX = "nativeParam";

    public NativeExecutorImpl(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig, new NativeCommandDetailsBuilderImpl(jdbcEngineConfig));
    }

    @Override
    public NativeExecutor parameters(Object... values) {
        if (this.isNamedParameter()) {
            throw new SonsureJdbcException("Named Parameter方式不支持数组传参");
        }
        int count = 1;
        for (Object value : values) {
            this.getSimpleCommandDetailsBuilder().parameter(DEFAULT_NATIVE_PARAM_PREFIX + (count++), value);
        }
        return this;
    }

}

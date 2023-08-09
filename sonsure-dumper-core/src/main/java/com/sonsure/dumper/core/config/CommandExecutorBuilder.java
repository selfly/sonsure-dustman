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

/**
 * @author liyd
 */
public interface CommandExecutorBuilder {

    /**
     * 是否支持
     *
     * @param commandExecutorClass the command executor class
     * @param param                the param
     * @param jdbcEngineConfig     the jdbc engine config
     * @return boolean
     */
    boolean support(Class<? extends CommandExecutor> commandExecutorClass, JdbcEngineConfig jdbcEngineConfig);

    /**
     * 构建CommandExecutor
     *
     * @param <T>                  the type parameter
     * @param commandExecutorClass the command executor class
     * @param param                the param
     * @param jdbcEngineConfig     the jdbc engine config
     * @return command executor
     */
    <T extends CommandExecutor> T build(Class<T> commandExecutorClass, JdbcEngineConfig jdbcEngineConfig);
}

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
public interface CommandExecutorFactory {


    /**
     * 获取commandExecutor
     *
     * @param <T>                  the type parameter
     * @param <M>                  the type parameter
     * @param commandExecutorClass the command executor class
     * @param modelClass           the model class
     * @param jdbcEngineConfig     the jdbc engine config
     * @return command executor
     */
    <T extends CommandExecutor, M> T getCommandExecutor(Class<T> commandExecutorClass, Class<M> modelClass, JdbcEngineConfig jdbcEngineConfig);

}

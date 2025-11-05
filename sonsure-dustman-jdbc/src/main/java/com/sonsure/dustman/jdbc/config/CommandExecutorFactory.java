/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.jdbc.config;

import com.sonsure.dustman.jdbc.command.CommandExecutor;

/**
 * @author liyd
 */
public interface CommandExecutorFactory {


    /**
     * 获取commandExecutor
     *
     * @param <T>                  the type parameter
     * @param jdbcContext          the jdbc context
     * @param commandExecutorClass the command executor class
     * @param params               the params
     * @return command executor
     */
    <T extends CommandExecutor<?>> T createCommandExecutor(JdbcContext jdbcContext, Class<T> commandExecutorClass, Object... params);

}

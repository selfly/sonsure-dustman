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
 * The interface Jdbc engine.
 *
 * @author liyd
 * @since 17 /4/12
 */
public interface JdbcExecutor {

    /**
     * jdbc 配置
     *
     * @return jdbc engine config
     */
    JdbcExecutorConfig getConfig();

    /**
     * Gets data source.
     *
     * @return the data source
     */
    DataSource getDataSource();

    /**
     * Gets database product.
     *
     * @return the database product
     */
    String getDatabaseProduct();

    /**
     * 创建执行器
     *
     * @param <T>                  the type parameter
     * @param <M>                  the type parameter
     * @param commandExecutorClass 执行器class
     * @param modelClass           the model class
     * @return t t
     */
    <T extends CommandExecutor<?>, M> T createExecutor(Class<T> commandExecutorClass, Class<M> modelClass);

}

/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.persist;


import com.sonsure.dumper.core.command.build.ExecutableCmd;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

/**
 * 持久化执行
 * <p>
 *
 * @author liyd
 * @since 17/4/11
 */
public interface PersistExecutor {

    /**
     * Gets database product.
     *
     * @return the database product
     */
    default String getDatabaseProduct() {
        return this.executeInConnection(connection -> {
            final DatabaseMetaData metaData = connection.getMetaData();
            return metaData.getDatabaseProductName().toLowerCase() + "/" + metaData.getDatabaseProductVersion();
        });
    }

    /**
     * Execute t.
     *
     * @param <R>      the type parameter
     * @param function the function
     * @return the t
     */
    <R> R executeInConnection(ExecutionFunction<Connection, R> function);

    /**
     * 执行command
     *
     * @param executableCmd the executable cmd
     * @return object object
     */
    Object execute(ExecutableCmd executableCmd);

}

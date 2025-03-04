/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.persist;


import com.sonsure.dumper.core.command.CommandDetails;

/**
 * 持久化执行
 * <p>
 *
 * @author liyd
 * @date 17/4/11
 */
public interface PersistExecutor {

    /**
     * 获取数据库方言
     *
     * @return dialect
     */
    String getDialect();

    /**
     * 执行command
     *
     * @param commandDetails the command context
     * @return object
     */
    Object execute(CommandDetails commandDetails);

}

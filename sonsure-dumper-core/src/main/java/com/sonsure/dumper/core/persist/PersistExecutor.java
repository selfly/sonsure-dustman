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

/**
 * 持久化执行
 * <p>
 *
 * @author liyd
 * @since 17/4/11
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
     * @param executableCmd the executable cmd
     * @return object object
     */
    Object execute(ExecutableCmd executableCmd);

}

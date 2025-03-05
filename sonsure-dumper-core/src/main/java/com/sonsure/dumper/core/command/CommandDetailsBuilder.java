/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command;

import com.sonsure.dumper.common.model.Pageable;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

/**
 * CommandContext构建
 * <p>
 *
 * @author liyd
 * @date 17/4/11
 */
public interface CommandDetailsBuilder<T extends CommandDetailsBuilder<T>> {

    /**
     * Force native
     *
     * @return the t
     */
    T forceNative();

    /**
     * Paginate.
     *
     * @param pageNum  the page num
     * @param pageSize the page size
     * @return the t
     */
    T paginate(int pageNum, int pageSize);

    /**
     * Paginate.
     *
     * @param pageable the pageable
     * @return the t
     */
    T paginate(Pageable pageable);

    /**
     * Limit.
     *
     * @param offset the offset
     * @param size   the size
     * @return the t
     */
    T limit(int offset, int size);

    /**
     * Disable count query command details builder.
     *
     * @return the t
     */
    T disableCountQuery();

    /**
     * 构建执行内容
     *
     * @param jdbcEngineConfig the jdbc engine config
     * @param commandType      the command type
     * @return command context
     */
    CommandDetails build(JdbcEngineConfig jdbcEngineConfig, CommandType commandType);

}

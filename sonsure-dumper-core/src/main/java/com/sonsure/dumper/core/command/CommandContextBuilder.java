/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command;

import com.sonsure.dumper.core.config.JdbcEngineConfig;

/**
 * CommandContext构建
 * <p>
 *
 * @author liyd
 * @date 17/4/11
 */
public interface CommandContextBuilder {

    /**
     * 构建执行内容
     *
     * @param jdbcEngineConfig the jdbc engine config
     * @return command context
     */
    CommandContext build(JdbcEngineConfig jdbcEngineConfig);

}

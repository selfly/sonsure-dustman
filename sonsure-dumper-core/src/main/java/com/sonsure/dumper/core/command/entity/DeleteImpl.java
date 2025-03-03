/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;


import com.sonsure.dumper.core.command.CommandDetails;
import com.sonsure.dumper.core.command.CommandType;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

/**
 * The type Delete.
 *
 * @author liyd
 * @date 17 /4/14
 */
public class DeleteImpl extends AbstractConditionCommandExecutor<Delete> implements Delete {

    public DeleteImpl(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
    }

    @Override
    public Delete from(Class<?> cls) {
        this.getCommandDetailsBuilder().deleteFrom(cls);
        return this;
    }

    @Override
    public int execute() {
        CommandDetails commandDetails = this.getCommandDetailsBuilder().build(getJdbcEngineConfig());
        return (Integer) getJdbcEngineConfig().getPersistExecutor().execute(commandDetails, CommandType.DELETE);
    }

}

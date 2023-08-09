/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;


import com.sonsure.dumper.core.command.CommandContext;
import com.sonsure.dumper.core.command.CommandType;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

/**
 * The type Delete.
 *
 * @author liyd
 * @date 17 /4/14
 */
public class DeleteImpl extends AbstractConditionCommandExecutor<Delete> implements Delete {

    private final DeleteCommandContextBuilderImpl deleteCommandContextBuilder;

    public DeleteImpl(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
        this.deleteCommandContextBuilder = new DeleteCommandContextBuilderImpl(new DeleteCommandContextBuilderImpl.Context());
    }

    @Override
    protected AbstractCommandContextBuilder getCommandContextBuilder() {
        return this.deleteCommandContextBuilder;
    }

    @Override
    public Delete from(Class<?> cls) {
        deleteCommandContextBuilder.addModelClass(cls);
        return this;
    }

    @Override
    public int execute() {
        CommandContext commandContext = this.getCommandContextBuilder().build(getJdbcEngineConfig());
        return (Integer) getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.DELETE);
    }

}

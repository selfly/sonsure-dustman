/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;


import com.sonsure.dumper.core.command.ExecutionType;
import com.sonsure.dumper.core.command.build.ExecutableCmd;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

/**
 * The type Delete.
 *
 * @author liyd
 * @since 17 /4/14
 */
public class DeleteImpl extends AbstractConditionCommandExecutor<Delete> implements Delete {

    public DeleteImpl(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
    }

    @Override
    public Delete from(Class<?> cls) {
        this.registerClassToMappingHandler(cls);
        this.getExecutableCmdBuilder().deleteFrom(cls.getSimpleName());
        return this;
    }

    @Override
    public int execute() {
        this.getExecutableCmdBuilder().executionType(ExecutionType.DELETE);
        this.getExecutableCmdBuilder().resultType(Integer.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return (Integer) getJdbcEngineConfig().getPersistExecutor().execute(executableCmd);
    }

}

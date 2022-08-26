/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   https://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.batch;

import com.sonsure.dumper.core.command.AbstractCommonCommandExecutor;
import com.sonsure.dumper.core.command.CommandContext;
import com.sonsure.dumper.core.command.CommandExecutorContext;
import com.sonsure.dumper.core.command.CommandType;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

import java.util.Collection;

/**
 * The type Batch update executor.
 *
 * @author liyd
 */
@SuppressWarnings("rawtypes")
public class BatchUpdateExecutorImpl extends AbstractCommonCommandExecutor<BatchUpdateExecutor> implements BatchUpdateExecutor {

    public BatchUpdateExecutorImpl(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
    }

    @Override
    public <T> Object execute(String command, Collection<T> batchData, int batchSize, ParameterizedSetter<T> parameterizedSetter) {
        this.getCommandExecutorContext().setCommand(command);
        final CommandExecutorContext.BatchUpdateExecutorContext<T> batchUpdateExecutorContext = this.getCommandExecutorContext().batchUpdateExecutorContext();
        batchUpdateExecutorContext.setBatchSize(batchSize);
        batchUpdateExecutorContext.setBatchData(batchData);
        batchUpdateExecutorContext.setParameterizedSetter(parameterizedSetter);
        CommandContext commandContext = this.getCommandContextBuilder().build(this.getCommandExecutorContext(), getJdbcEngineConfig());
        return getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.BATCH_UPDATE);
    }

}

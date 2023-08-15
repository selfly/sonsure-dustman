/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   https://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.batch;

import com.sonsure.dumper.core.command.AbstractCommonCommandContextBuilder;
import com.sonsure.dumper.core.command.AbstractCommonCommandExecutor;
import com.sonsure.dumper.core.command.CommandContext;
import com.sonsure.dumper.core.command.CommandType;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

import java.util.Collection;

/**
 * The type Batch update executor.
 *
 * @author liyd
 */
public class BatchUpdateExecutorImpl extends AbstractCommonCommandExecutor<BatchUpdateExecutor> implements BatchUpdateExecutor {

    private final BatchUpdateCommandContextBuilderImpl batchUpdateCommandContextBuilder;

    public BatchUpdateExecutorImpl(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
        this.batchUpdateCommandContextBuilder = new BatchUpdateCommandContextBuilderImpl(new BatchUpdateCommandContextBuilderImpl.Context());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends AbstractCommonCommandContextBuilder> T getCommandContextBuilder() {
        return (T) batchUpdateCommandContextBuilder;
    }

    @Override
    public <T> Object execute(String command, Collection<T> batchData, int batchSize, ParameterizedSetter<T> parameterizedSetter) {
        this.batchUpdateCommandContextBuilder.command(command);
        batchUpdateCommandContextBuilder.batchSize(batchSize);
        batchUpdateCommandContextBuilder.batchData(batchData);
        batchUpdateCommandContextBuilder.parameterizedSetter(parameterizedSetter);
        CommandContext commandContext = this.batchUpdateCommandContextBuilder.build(getJdbcEngineConfig());
        return getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.BATCH_UPDATE);
    }

}

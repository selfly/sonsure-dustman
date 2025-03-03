/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   https://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.batch;

import com.sonsure.dumper.core.command.AbstractCommonCommandDetailsBuilder;
import com.sonsure.dumper.core.command.AbstractCommonCommandExecutor;
import com.sonsure.dumper.core.command.CommandDetails;
import com.sonsure.dumper.core.command.CommandType;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

import java.util.Collection;

/**
 * The type Batch update executor.
 *
 * @author liyd
 */
public class BatchUpdateExecutorImpl extends AbstractCommonCommandExecutor<BatchUpdateExecutor> implements BatchUpdateExecutor {

    private final BatchUpdateCommandDetailsBuilderImpl batchUpdateCommandContextBuilder;

    public BatchUpdateExecutorImpl(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
        this.batchUpdateCommandContextBuilder = new BatchUpdateCommandDetailsBuilderImpl(new BatchUpdateCommandDetailsBuilderImpl.Context());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends AbstractCommonCommandDetailsBuilder> T getCommandContextBuilder() {
        return (T) batchUpdateCommandContextBuilder;
    }

    @Override
    public <T> Object execute(String command, Collection<T> batchData, int batchSize, ParameterizedSetter<T> parameterizedSetter) {
        this.batchUpdateCommandContextBuilder.command(command);
        batchUpdateCommandContextBuilder.batchSize(batchSize);
        batchUpdateCommandContextBuilder.batchData(batchData);
        batchUpdateCommandContextBuilder.parameterizedSetter(parameterizedSetter);
        CommandDetails commandDetails = this.batchUpdateCommandContextBuilder.build(getJdbcEngineConfig());
        return getJdbcEngineConfig().getPersistExecutor().execute(commandDetails, CommandType.BATCH_UPDATE);
    }

}

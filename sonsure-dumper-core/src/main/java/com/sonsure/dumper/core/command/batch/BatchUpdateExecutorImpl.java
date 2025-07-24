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
import com.sonsure.dumper.core.command.CommandDetails;
import com.sonsure.dumper.core.command.CommandDetailsBuilder;
import com.sonsure.dumper.core.command.CommandType;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

import java.util.Collection;

/**
 * The type Batch update executor.
 *
 * @author liyd
 */
public class BatchUpdateExecutorImpl extends AbstractCommonCommandExecutor<BatchUpdateExecutor> implements BatchUpdateExecutor {

    private final BatchUpdateCommandDetailsBuilder batchUpdateCommandDetailsBuilder;

    public BatchUpdateExecutorImpl(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
        this.batchUpdateCommandDetailsBuilder = new BatchUpdateCommandDetailsBuilderImpl();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends CommandDetailsBuilder<T>> T getCommandDetailsBuilder() {
        //noinspection unchecked
        return (T) batchUpdateCommandDetailsBuilder;
    }

    @Override
    public <T> Object execute(String command, Collection<T> batchData, int batchSize, ParameterizedSetter<T> parameterizedSetter) {
        this.batchUpdateCommandDetailsBuilder.command(command);
        batchUpdateCommandDetailsBuilder.batchSize(batchSize);
        batchUpdateCommandDetailsBuilder.batchData(batchData);
        batchUpdateCommandDetailsBuilder.parameterizedSetter(parameterizedSetter);
        CommandDetails commandDetails = this.batchUpdateCommandDetailsBuilder.build(getJdbcEngineConfig(), CommandType.BATCH_UPDATE);
        commandDetails.setResultType(Object.class);
        return getJdbcEngineConfig().getPersistExecutor().execute(commandDetails);
    }

}

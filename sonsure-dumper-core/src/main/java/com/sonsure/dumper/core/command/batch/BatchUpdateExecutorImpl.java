/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   https://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.batch;

import com.sonsure.dumper.common.bean.BeanKit;
import com.sonsure.dumper.core.command.AbstractCommandExecutor;
import com.sonsure.dumper.core.command.ExecutionType;
import com.sonsure.dumper.core.command.build.ExecutableCmd;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

import java.util.Collection;

/**
 * The type Batch update executor.
 *
 * @author liyd
 */
public class BatchUpdateExecutorImpl extends AbstractCommandExecutor<BatchUpdateExecutor> implements BatchUpdateExecutor {


    public BatchUpdateExecutorImpl(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
    }

    @Override
    public <T> Object execute(String command, Collection<T> batchData, int batchSize, ParameterizedSetter<T> parameterizedSetter) {
        this.getExecutableCmdBuilder().command(command);
        this.getExecutableCmdBuilder().executionType(ExecutionType.BATCH_UPDATE);
        this.getExecutableCmdBuilder().resultType(Object.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        BatchExecutableCmd<T> batchExecutableCmd = BeanKit.copyProperties(new BatchExecutableCmd<T>(), executableCmd);
        batchExecutableCmd.setBatchSize(batchSize);
        batchExecutableCmd.setBatchData(batchData);
        batchExecutableCmd.setParameterizedSetter(parameterizedSetter);
        return getJdbcEngineConfig().getPersistExecutor().execute(batchExecutableCmd);
    }

}

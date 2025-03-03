/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.executor;

import com.sonsure.dumper.core.command.CommandContextBuilderContext;
import com.sonsure.dumper.core.command.CommandDetails;
import com.sonsure.dumper.core.command.CommandType;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.persist.PersistExecutor;

public class CountCommandExecutorImpl implements CountCommandExecutor {

    private JdbcEngineConfig jdbcEngineConfig;

    private CountCommandDetailsBuilder countCommandContextBuilder;

    public CountCommandExecutorImpl(JdbcEngineConfig jdbcEngineConfig) {
        this.jdbcEngineConfig = jdbcEngineConfig;
        this.countCommandContextBuilder = new CountCommandDetailsBuilder(new CommandContextBuilderContext());
    }

    @Override
    public CountCommandExecutor clazz(Class<?> clazz) {
        this.countCommandContextBuilder.addModelClass(clazz);
        return this;
    }

    @Override
    public long getCount() {
        CommandDetails commandDetails = this.countCommandContextBuilder.build(this.jdbcEngineConfig);
        PersistExecutor persistExecutor = this.jdbcEngineConfig.getPersistExecutor();
        commandDetails.setResultType(Long.class);
        Object result = persistExecutor.execute(commandDetails, CommandType.QUERY_ONE_COL);
        return (Long) result;
    }

}

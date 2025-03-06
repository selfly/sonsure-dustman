/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.config;

import com.sonsure.dumper.core.command.CommandExecutor;
import com.sonsure.dumper.core.command.batch.BatchUpdateExecutor;
import com.sonsure.dumper.core.command.batch.BatchUpdateExecutorImpl;
import com.sonsure.dumper.core.command.entity.*;
import com.sonsure.dumper.core.command.mybatis.MybatisExecutor;
import com.sonsure.dumper.core.command.mybatis.MybatisExecutorImpl;
import com.sonsure.dumper.core.command.natives.NativeExecutor;
import com.sonsure.dumper.core.command.natives.NativeExecutorImpl;

/**
 * The type Command executor builder.
 *
 * @author liyd
 */
public class CommandExecutorBuilderImpl extends AbstractCommandExecutorBuilder {

    @Override
    public <T extends CommandExecutor> T build(Class<T> commandExecutorClass, JdbcEngineConfig jdbcEngineConfig, Object... params) {
        if (Insert.class == commandExecutorClass) {
            return (T) new InsertImpl(jdbcEngineConfig);
        } else if (Select.class == commandExecutorClass) {
            return (T) new SelectImpl<>(jdbcEngineConfig, params);
        } else if (Update.class == commandExecutorClass) {
            return (T) new UpdateImpl(jdbcEngineConfig);
        } else if (Delete.class == commandExecutorClass) {
            return (T) new DeleteImpl(jdbcEngineConfig);
        } else if (NativeExecutor.class == commandExecutorClass) {
            return (T) new NativeExecutorImpl(jdbcEngineConfig);
        } else if (MybatisExecutor.class == commandExecutorClass) {
            return (T) new MybatisExecutorImpl(jdbcEngineConfig);
        } else if (BatchUpdateExecutor.class == commandExecutorClass) {
            return (T) new BatchUpdateExecutorImpl(jdbcEngineConfig);
        } else {
            return null;
        }
    }
}

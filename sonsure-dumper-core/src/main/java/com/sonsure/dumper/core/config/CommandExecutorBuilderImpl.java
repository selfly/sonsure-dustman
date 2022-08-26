/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.config;

import com.sonsure.dumper.core.command.AbstractCommonCommandExecutor;
import com.sonsure.dumper.core.command.CommandContextBuilder;
import com.sonsure.dumper.core.command.CommandExecutor;
import com.sonsure.dumper.core.command.batch.BatchUpdateCommandContextBuilder;
import com.sonsure.dumper.core.command.batch.BatchUpdateExecutor;
import com.sonsure.dumper.core.command.batch.BatchUpdateExecutorImpl;
import com.sonsure.dumper.core.command.entity.*;
import com.sonsure.dumper.core.command.mybatis.MybatisCommandContextBuilder;
import com.sonsure.dumper.core.command.mybatis.MybatisExecutor;
import com.sonsure.dumper.core.command.mybatis.MybatisExecutorImpl;
import com.sonsure.dumper.core.command.natives.NativeCommandContextBuilder;
import com.sonsure.dumper.core.command.natives.NativeExecutor;
import com.sonsure.dumper.core.command.natives.NativeExecutorImpl;
import com.sonsure.dumper.core.exception.SonsureJdbcException;

import java.util.Arrays;
import java.util.List;

/**
 * The type Command executor builder.
 *
 * @author liyd
 */
public class CommandExecutorBuilderImpl extends AbstractCommandExecutorBuilder {

    protected List<Class<? extends CommandExecutor>> commandExecutorClasses;

    public CommandExecutorBuilderImpl() {
        commandExecutorClasses = Arrays.asList(Insert.class, Select.class, Update.class, Delete.class, NativeExecutor.class, MybatisExecutor.class, BatchUpdateExecutor.class);
    }

    @Override
    public boolean support(Class<? extends CommandExecutor> commandExecutorClass, Object param, JdbcEngineConfig jdbcEngineConfig) {
        return commandExecutorClasses.contains(commandExecutorClass);
    }

    @Override
    public CommandExecutor build(Class<? extends CommandExecutor> commandExecutorClass, Object param, JdbcEngineConfig jdbcEngineConfig) {

        AbstractCommonCommandExecutor<?> commandExecutor = null;
        CommandContextBuilder commandContextBuilder = null;

        if (commandExecutorClass == Insert.class) {
            commandExecutor = new InsertImpl(jdbcEngineConfig);
            commandContextBuilder = new InsertCommandContextBuilderImpl();
        } else if (commandExecutorClass == Select.class) {
            commandExecutor = new SelectImpl(jdbcEngineConfig, ((String[]) param));
            commandContextBuilder = new SelectCommandContextBuilderImpl();
        } else if (commandExecutorClass == Update.class) {
            commandExecutor = new UpdateImpl(jdbcEngineConfig);
            commandContextBuilder = new UpdateCommandContextBuilderImpl();
        } else if (commandExecutorClass == Delete.class) {
            commandExecutor = new DeleteImpl(jdbcEngineConfig);
            commandContextBuilder = new DeleteCommandContextBuilderImpl();
        } else if (commandExecutorClass == NativeExecutor.class) {
            commandExecutor = new NativeExecutorImpl(jdbcEngineConfig);
            commandContextBuilder = new NativeCommandContextBuilder();
        } else if (commandExecutorClass == MybatisExecutor.class) {
            commandExecutor = new MybatisExecutorImpl(jdbcEngineConfig);
            commandContextBuilder = new MybatisCommandContextBuilder();
        } else if (commandExecutorClass == BatchUpdateExecutor.class) {
            commandExecutor = new BatchUpdateExecutorImpl(jdbcEngineConfig);
            commandContextBuilder = new BatchUpdateCommandContextBuilder();
        } else {
            throw new SonsureJdbcException("没有匹配的commandExecutor");
        }
        commandExecutor.setCommandContextBuilder(commandContextBuilder);
        return commandExecutor;
    }
}

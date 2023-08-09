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
import com.sonsure.dumper.core.exception.SonsureJdbcException;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Command executor builder.
 *
 * @author liyd
 */
public class CommandExecutorBuilderImpl extends AbstractCommandExecutorBuilder {

    protected Map<Class<? extends CommandExecutor>, Class<? extends CommandExecutor>> commandExecutorClassMap = new HashMap<>();

    public CommandExecutorBuilderImpl() {
        commandExecutorClassMap.put(Insert.class, InsertImpl.class);
        commandExecutorClassMap.put(Select.class, SelectImpl.class);
        commandExecutorClassMap.put(Update.class, UpdateImpl.class);
        commandExecutorClassMap.put(Delete.class, DeleteImpl.class);
        commandExecutorClassMap.put(NativeExecutor.class, NativeExecutorImpl.class);
        commandExecutorClassMap.put(MybatisExecutor.class, MybatisExecutorImpl.class);
        commandExecutorClassMap.put(BatchUpdateExecutor.class, BatchUpdateExecutorImpl.class);
    }

    @Override
    public boolean support(Class<? extends CommandExecutor> commandExecutorClass, JdbcEngineConfig jdbcEngineConfig) {
        return commandExecutorClassMap.containsKey(commandExecutorClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends CommandExecutor> T build(Class<T> commandExecutorClass, JdbcEngineConfig jdbcEngineConfig) {
        try {
            Class<? extends CommandExecutor> implClass = commandExecutorClassMap.get(commandExecutorClass);
            Constructor<? extends CommandExecutor> constructor = implClass.getDeclaredConstructor(JdbcEngineConfig.class);
            return (T) constructor.newInstance(jdbcEngineConfig);
        } catch (Exception e) {
            throw new SonsureJdbcException("创建CommandExecutor失败", e);
        }
    }
}

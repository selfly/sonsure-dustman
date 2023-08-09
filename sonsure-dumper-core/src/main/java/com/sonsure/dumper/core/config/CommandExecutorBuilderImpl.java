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
import com.sonsure.dumper.core.command.entity.Delete;
import com.sonsure.dumper.core.command.entity.Insert;
import com.sonsure.dumper.core.command.entity.Select;
import com.sonsure.dumper.core.command.entity.Update;
import com.sonsure.dumper.core.command.mybatis.MybatisExecutor;
import com.sonsure.dumper.core.command.natives.NativeExecutor;
import com.sonsure.dumper.core.exception.SonsureJdbcException;

import java.lang.reflect.Constructor;
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
    public boolean support(Class<? extends CommandExecutor> commandExecutorClass, JdbcEngineConfig jdbcEngineConfig) {
        return commandExecutorClasses.contains(commandExecutorClass);
    }

    @Override
    public <T extends CommandExecutor> T build(Class<T> commandExecutorClass, JdbcEngineConfig jdbcEngineConfig) {
        try {
            Constructor<T> constructor = commandExecutorClass.getDeclaredConstructor(JdbcEngineConfig.class);
            return constructor.newInstance(jdbcEngineConfig);
        } catch (Exception e) {
            throw new SonsureJdbcException("创建CommandExecutor失败", e);
        }
    }
}

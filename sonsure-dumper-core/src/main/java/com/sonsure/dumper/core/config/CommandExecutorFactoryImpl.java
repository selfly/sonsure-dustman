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
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liyd
 */
@Setter
public class CommandExecutorFactoryImpl implements CommandExecutorFactory {

    protected List<CommandExecutorBuilder> commandExecutorBuilders;

    public CommandExecutorFactoryImpl() {
        commandExecutorBuilders = new ArrayList<>();
        commandExecutorBuilders.add(new CommandExecutorBuilderImpl());
    }

    @Override
    public <T extends CommandExecutor, M> T getCommandExecutor(Class<T> commandExecutorClass, Class<M> modelClass, JdbcEngineConfig jdbcEngineConfig) {
        for (CommandExecutorBuilder commandExecutorBuilder : this.commandExecutorBuilders) {
            T executor = commandExecutorBuilder.build(commandExecutorClass, modelClass, jdbcEngineConfig);
            if (executor != null) {
                return executor;
            }
        }
        throw new SonsureJdbcException(String.format("没有找到对应的CommandExecutorBuilder,commandExecutorClass:%s", commandExecutorClass.getName()));
    }

    public void addCommandExecutorBuilder(CommandExecutorBuilder commandExecutorBuilder) {
        this.commandExecutorBuilders.add(commandExecutorBuilder);
    }

}

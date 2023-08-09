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

import java.util.ArrayList;
import java.util.List;

/**
 * @author liyd
 */
public class CommandExecutorFactoryImpl implements CommandExecutorFactory {

    protected List<CommandExecutorBuilder> defaultCommandExecutorBuilders;

    protected List<CommandExecutorBuilder> commandExecutorBuilders;

    public CommandExecutorFactoryImpl() {
        defaultCommandExecutorBuilders = new ArrayList<>();
        defaultCommandExecutorBuilders.add(new CommandExecutorBuilderImpl());
    }

    @Override
    public <T extends CommandExecutor> T getCommandExecutor(Class<T> commandExecutorClass, JdbcEngineConfig jdbcEngineConfig) {
        CommandExecutorBuilder commandExecutorBuilder = this.getCommandExecutorBuilder(commandExecutorClass, jdbcEngineConfig);
        return commandExecutorBuilder.build(commandExecutorClass, jdbcEngineConfig);
    }

    protected CommandExecutorBuilder getCommandExecutorBuilder(Class<? extends CommandExecutor> commandExecutorClass, JdbcEngineConfig jdbcEngineConfig) {
        if (this.commandExecutorBuilders != null) {
            for (CommandExecutorBuilder ceb : this.commandExecutorBuilders) {
                if (ceb.support(commandExecutorClass, jdbcEngineConfig)) {
                    return ceb;
                }
            }
        }
        for (CommandExecutorBuilder ceb : this.defaultCommandExecutorBuilders) {
            if (ceb.support(commandExecutorClass, jdbcEngineConfig)) {
                return ceb;
            }
        }
        throw new SonsureJdbcException(String.format("没有找到对应的CommandExecutorBuilder,commandExecutorClass:%s", commandExecutorClass.getName()));
    }

    public void setCommandExecutorBuilders(List<CommandExecutorBuilder> commandExecutorBuilders) {
        this.commandExecutorBuilders = commandExecutorBuilders;
    }
}

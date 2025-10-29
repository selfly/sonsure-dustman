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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author liyd
 */
@Setter
public class CommandExecutorFactoryImpl implements CommandExecutorFactory {

    protected Map<Class<?>, CommandExecutorCreator> commandExecutorCreators;

    public CommandExecutorFactoryImpl() {
        commandExecutorCreators = new LinkedHashMap<>(8);
        this.registerCommandExecutorCreator(new InternalCommandExecutorCreatorImpl());
    }

    @Override
    public <T extends CommandExecutor<?>> T createCommandExecutor(Class<T> commandExecutorClass, JdbcExecutorConfig jdbcExecutorConfig, Object... params) {
        CommandExecutorCreator commandExecutorCreator = commandExecutorCreators.get(commandExecutorClass);
        if (commandExecutorCreator == null) {
            throw new SonsureJdbcException(String.format("没有找到对应的CommandExecutorCreator,commandExecutorClass:%s", commandExecutorClass.getName()));
        }
        return commandExecutorCreator.create(commandExecutorClass, jdbcExecutorConfig, params);
    }

    public void registerCommandExecutorCreator(CommandExecutorCreator commandExecutorCreator) {
        for (Class<?> commandExecutorClass : commandExecutorCreator.getCommandExecutorClasses()) {
            commandExecutorCreators.put(commandExecutorClass, commandExecutorCreator);
        }
    }

}

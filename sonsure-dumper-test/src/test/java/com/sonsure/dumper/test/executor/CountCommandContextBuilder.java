/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.executor;

import com.sonsure.dumper.core.command.CommandContext;
import com.sonsure.dumper.core.command.CommandContextBuilderContext;
import com.sonsure.dumper.core.command.entity.AbstractCommandContextBuilder;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

public class CountCommandContextBuilder extends AbstractCommandContextBuilder {

    public CountCommandContextBuilder(CommandContextBuilderContext commandContextBuilderContext) {
        super(commandContextBuilderContext);
    }

    @Override
    public CommandContext doBuild(JdbcEngineConfig jdbcEngineConfig) {
        Class<?> clazz = this.getUniqueModelClass();
        CommandContext commandContext = new CommandContext();
        commandContext.setCommand("select count(*) from " + clazz.getSimpleName());
        return commandContext;
    }
}

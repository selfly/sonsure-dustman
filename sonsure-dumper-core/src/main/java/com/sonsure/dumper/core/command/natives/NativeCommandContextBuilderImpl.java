/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.natives;

import com.sonsure.dumper.core.command.CommandContext;
import com.sonsure.dumper.core.command.simple.AbstractSimpleCommandContextBuilder;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

/**
 * @author liyd
 */
public class NativeCommandContextBuilderImpl extends AbstractSimpleCommandContextBuilder {


    public NativeCommandContextBuilderImpl(Context simpleContext) {
        super(simpleContext);
    }

    @Override
    public CommandContext doBuild(JdbcEngineConfig jdbcEngineConfig) {
        CommandContext commandContext = new CommandContext();
        commandContext.setCommand(getSimpleContext().getCommand());
        if (getSimpleContext().getCommandParameters() != null) {
            commandContext.addCommandParameters(getSimpleContext().getCommandParameters());
        }
        return commandContext;
    }
}

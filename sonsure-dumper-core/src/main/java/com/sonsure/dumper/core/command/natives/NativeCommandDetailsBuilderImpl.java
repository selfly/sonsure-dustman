/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.natives;

import com.sonsure.dumper.core.command.CommandDetails;
import com.sonsure.dumper.core.command.simple.AbstractSimpleCommandDetailsBuilder;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

/**
 * @author liyd
 */
public class NativeCommandDetailsBuilderImpl extends AbstractSimpleCommandDetailsBuilder {


    public NativeCommandDetailsBuilderImpl(Context simpleContext) {
        super(simpleContext);
    }

    @Override
    public CommandDetails doBuild(JdbcEngineConfig jdbcEngineConfig) {
        CommandDetails commandDetails = new CommandDetails();
        commandDetails.setCommand(getSimpleContext().getCommand());
        if (getSimpleContext().getCommandParameters() != null) {
            commandDetails.addCommandParameters(getSimpleContext().getCommandParameters());
        }
        return commandDetails;
    }
}

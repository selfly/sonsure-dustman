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
import com.sonsure.dumper.core.command.CommandType;
import com.sonsure.dumper.core.command.GenerateKey;
import com.sonsure.dumper.core.command.simple.AbstractSimpleCommandDetailsBuilder;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

/**
 * @author liyd
 */
public class NativeCommandDetailsBuilderImpl extends AbstractSimpleCommandDetailsBuilder<NativeCommandDetailsBuilderImpl> {

    @Override
    protected CommandDetails doCustomize(JdbcEngineConfig jdbcEngineConfig, CommandType commandType) {
        CommandDetails commandDetails = new CommandDetails();
        commandDetails.setCommand(this.getCommand());
        commandDetails.setCommandParameters(this.getCommandParameters());
        if (CommandType.INSERT == commandType) {
            GenerateKey generateKey = new GenerateKey();
            generateKey.setPrimaryKeyParameter(false);
            commandDetails.setGenerateKey(generateKey);
        }
        return commandDetails;
    }
}

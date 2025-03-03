/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.executor;

import com.sonsure.dumper.core.command.AbstractCommonCommandDetailsBuilder;
import com.sonsure.dumper.core.command.CommandContextBuilderContext;
import com.sonsure.dumper.core.command.CommandDetails;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

public class CountCommandDetailsBuilder extends AbstractCommonCommandDetailsBuilder {

    public CountCommandDetailsBuilder(CommandContextBuilderContext commandContextBuilderContext) {
        super(commandContextBuilderContext);
    }

    @Override
    public CommandDetails doBuild(JdbcEngineConfig jdbcEngineConfig) {
        Class<?> clazz = this.getCommandContextBuilderContext().getUniqueModelClass();
        CommandDetails commandDetails = new CommandDetails();
        commandDetails.setCommand("select count(*) from " + clazz.getSimpleName());
        return commandDetails;
    }
}

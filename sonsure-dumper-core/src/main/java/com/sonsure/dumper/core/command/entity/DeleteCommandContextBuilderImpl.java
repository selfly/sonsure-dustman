/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;


import com.sonsure.dumper.core.command.AbstractCommonCommandContextBuilder;
import com.sonsure.dumper.core.command.CommandContext;
import com.sonsure.dumper.core.command.CommandContextBuilderContext;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

/**
 * The type Delete command context builder.
 *
 * @author liyd
 * @date 17 /4/14
 */
public class DeleteCommandContextBuilderImpl extends AbstractCommonCommandContextBuilder {

    private static final String COMMAND_OPEN = "delete from ";

    private final ConditionCommandBuilderImpl conditionCommandBuilder;

    public DeleteCommandContextBuilderImpl(Context deleteContext) {
        super(deleteContext);
        this.conditionCommandBuilder = new ConditionCommandBuilderImpl(new ConditionCommandBuilderImpl.Context());
    }

    @Override
    public void namedParameter() {
        super.namedParameter();
        this.conditionCommandBuilder.namedParameter();
    }

    @Override
    public CommandContext doBuild(JdbcEngineConfig jdbcEngineConfig) {
        StringBuilder command = new StringBuilder(COMMAND_OPEN);
        final Class<?> modelClass = this.getCommandContextBuilderContext().getUniqueModelClass();
        command.append(this.getModelAliasName(modelClass, null));

        CommandContext commandContext = createCommandContext();

        CommandContext whereCommandContext = this.conditionCommandBuilder.build(jdbcEngineConfig);
        if (whereCommandContext != null) {
            command.append(whereCommandContext.getCommand());
            commandContext.addCommandParameters(whereCommandContext.getCommandParameters());
        }
        commandContext.setCommand(command.toString());
        return commandContext;
    }

    public ConditionCommandBuilderImpl getConditionCommandBuilder() {
        return conditionCommandBuilder;
    }

    public static class Context extends CommandContextBuilderContext {
    }

}

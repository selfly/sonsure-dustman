/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;

import com.sonsure.dumper.core.command.CommandContext;
import com.sonsure.dumper.core.command.CommandContextBuilderContext;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.management.CommandField;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liyd
 * @date 17/4/14
 */
public class UpdateCommandContextBuilderImpl extends AbstractCommandContextBuilder {

    private static final String COMMAND_OPEN = "update ";

    private final Context updateContext;

    private final ConditionCommandBuilderImpl conditionCommandBuilder;

    public UpdateCommandContextBuilderImpl(Context updateContext) {
        super(updateContext);
        this.updateContext = updateContext;
        this.conditionCommandBuilder = new ConditionCommandBuilderImpl(new ConditionCommandBuilderImpl.Context());
    }

    public void addSetField(String field, Object value) {
        this.updateContext.addSetField(field, value);
    }

    public void setIgnoreNull(boolean ignoreNull) {
        this.updateContext.setIgnoreNull(ignoreNull);
    }

    @Override
    public CommandContext doBuild(JdbcEngineConfig jdbcEngineConfig) {

        CommandContext commandContext = this.createCommandContext();

        StringBuilder command = new StringBuilder(COMMAND_OPEN);
        final Class<?> modelClass = this.getUniqueModelClass();
        command.append(this.getModelAliasName(modelClass, null)).append(" set ");

        String pkField = this.getPkField(modelClass, jdbcEngineConfig.getMappingHandler());
        final List<CommandField> setFields = updateContext.getSetFields();
        final boolean ignoreNull = updateContext.isIgnoreNull();
        for (CommandField commandField : setFields) {
            //主键 不管怎么更新都不更新主键
            if (StringUtils.equals(pkField, commandField.getFieldName())) {
                continue;
            }
            //null值
            if (commandField.getValue() == null && ignoreNull) {
                continue;
            }
            final String filedCommandName = this.getFiledCommandName(commandField, jdbcEngineConfig);
            command.append(filedCommandName).append(" = ");
            if (commandField.getValue() == null) {
                command.append("null");
            } else if (commandField.isNative()) {
                command.append(commandField.getValue());
            } else {
                final String placeholder = this.createParameterPlaceholder(commandField.getFieldName(), updateContext.isNamedParameter());
                command.append(placeholder);
                commandContext.addCommandParameter(commandField.getFieldName(), commandField.getValue());
            }
            command.append(",");
        }
        command.deleteCharAt(command.length() - 1);

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

        private final List<CommandField> setFields;

        /**
         * 是否忽略null值
         */
        private boolean isIgnoreNull = true;

        public Context() {
            this.setFields = new ArrayList<>();
        }

        public void addSetField(String field, Object value) {
            CommandField commandField = this.createCommandClassField(field, true, CommandField.Type.MANUAL_FIELD);
            commandField.setValue(value);
            this.setFields.add(commandField);
        }

        public List<CommandField> getSetFields() {
            return setFields;
        }

        public boolean isIgnoreNull() {
            return isIgnoreNull;
        }

        public void setIgnoreNull(boolean ignoreNull) {
            isIgnoreNull = ignoreNull;
        }
    }
}

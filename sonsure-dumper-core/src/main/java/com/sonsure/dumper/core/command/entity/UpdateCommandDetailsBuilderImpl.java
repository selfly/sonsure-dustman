///*
// * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
// * You may obtain more information at
// *
// *   http://www.sonsure.com
// *
// * Designed By Selfly Lee (selfly@live.com)
// */
//
//package com.sonsure.dumper.core.command.entity;
//
//import com.sonsure.dumper.core.command.AbstractCommonCommandDetailsBuilder;
//import com.sonsure.dumper.core.command.CommandDetails;
//import com.sonsure.dumper.core.command.CommandContextBuilderContext;
//import com.sonsure.dumper.core.config.JdbcEngineConfig;
//import com.sonsure.dumper.core.management.CommandField;
//import lombok.Getter;
//import org.apache.commons.lang3.StringUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author liyd
// * @date 17/4/14
// */
//public class UpdateCommandDetailsBuilderImpl extends AbstractCommonCommandDetailsBuilder {
//
//    private static final String COMMAND_OPEN = "update ";
//
//    private final Context updateContext;
//
//    @Getter
//    private final ConditionCommandBuilderImpl conditionCommandBuilder;
//
//    public UpdateCommandDetailsBuilderImpl(Context updateContext) {
//        super(updateContext);
//        this.updateContext = updateContext;
//        this.conditionCommandBuilder = new ConditionCommandBuilderImpl(new ConditionCommandBuilderImpl.Context());
//    }
//
//    @Override
//    public void namedParameter() {
//        super.namedParameter();
//        this.conditionCommandBuilder.namedParameter();
//    }
//
//    public void addSetField(String field, Object value) {
//        CommandField commandField = this.createCommandClassField(field, true, CommandField.Type.MANUAL_FIELD);
//        commandField.setValue(value);
//        this.updateContext.addSetField(commandField);
//    }
//
//    public void setIgnoreNull(boolean ignoreNull) {
//        this.updateContext.setIgnoreNull(ignoreNull);
//    }
//
//    @Override
//    public CommandDetails doBuild(JdbcEngineConfig jdbcEngineConfig) {
//
//        CommandDetails commandDetails = this.createCommandContext();
//
//        StringBuilder command = new StringBuilder(COMMAND_OPEN);
//        final Class<?> modelClass = this.updateContext.getUniqueModelClass();
//        command.append(this.getModelAliasName(modelClass, null)).append(" set ");
//
//        String pkField = this.getPkField(modelClass, jdbcEngineConfig.getMappingHandler());
//        final List<CommandField> setFields = updateContext.getSetFields();
//        final boolean ignoreNull = updateContext.isIgnoreNull();
//        for (CommandField commandField : setFields) {
//            //主键 不管怎么更新都不更新主键
//            if (StringUtils.equals(pkField, commandField.getFieldName())) {
//                continue;
//            }
//            //null值
//            if (commandField.getValue() == null && ignoreNull) {
//                continue;
//            }
//            final String filedCommandName = this.getFiledCommandName(commandField, jdbcEngineConfig);
//            command.append(filedCommandName).append(" = ");
//            if (commandField.getValue() == null) {
//                command.append("null");
//            } else if (commandField.isNative()) {
//                command.append(commandField.getValue());
//            } else {
//                final String placeholder = this.createParameterPlaceholder(commandField.getFieldName(), updateContext.isNamedParameter());
//                command.append(placeholder);
//                commandDetails.addCommandParameter(commandField.getFieldName(), commandField.getValue());
//            }
//            command.append(",");
//        }
//        command.deleteCharAt(command.length() - 1);
//
//        CommandDetails whereCommandDetails = this.conditionCommandBuilder.build(jdbcEngineConfig);
//        if (whereCommandDetails != null) {
//            command.append(whereCommandDetails.getCommand());
//            commandDetails.addCommandParameters(whereCommandDetails.getParameterObjects());
//        }
//
//        commandDetails.setCommand(command.toString());
//
//        return commandDetails;
//    }
//
//    public static class Context extends CommandContextBuilderContext {
//
//        private final List<CommandField> setFields;
//
//        /**
//         * 是否忽略null值
//         */
//        private boolean isIgnoreNull = true;
//
//        public Context() {
//            this.setFields = new ArrayList<>();
//        }
//
//        public void addSetField(CommandField commandField) {
//            this.getSetFields().add(commandField);
//        }
//
//        public List<CommandField> getSetFields() {
//            return setFields;
//        }
//
//        public boolean isIgnoreNull() {
//            return isIgnoreNull;
//        }
//
//        public void setIgnoreNull(boolean ignoreNull) {
//            isIgnoreNull = ignoreNull;
//        }
//    }
//}

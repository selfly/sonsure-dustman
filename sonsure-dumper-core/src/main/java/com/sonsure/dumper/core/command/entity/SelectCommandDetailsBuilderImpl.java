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
//
//import com.sonsure.dumper.core.command.CommandDetails;
//import com.sonsure.dumper.core.command.AbstractQueryCommandDetailsBuilder;
//import com.sonsure.dumper.core.command.QueryCommandContextBuilderContext;
//import com.sonsure.dumper.core.config.JdbcEngineConfig;
//import com.sonsure.dumper.core.exception.SonsureJdbcException;
//import com.sonsure.dumper.core.management.CommandClass;
//import com.sonsure.dumper.core.management.CommandField;
//import com.sonsure.dumper.core.management.ModelClassFieldDetails;
//import lombok.Getter;
//import org.apache.commons.lang3.StringUtils;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
///**
// * @author liyd
// * @date 17/4/12
// */
//public class SelectCommandDetailsBuilderImpl extends AbstractQueryCommandDetailsBuilder {
//
//    private static final String COMMAND_OPEN = "select ";
//
//    @Getter
//    private final Context selectContext;
//
//    @Getter
//    private final ConditionCommandBuilderImpl conditionCommandBuilder;
//
//    private final GroupCommandBuilderImpl groupCommandBuilder;
//
//    private final OrderByCommandBuilderImpl orderByCommandBuilder;
//
//    public SelectCommandDetailsBuilderImpl(Context selectContext) {
//        super(selectContext);
//        this.selectContext = selectContext;
//        this.conditionCommandBuilder = new ConditionCommandBuilderImpl(new ConditionCommandBuilderImpl.Context());
//        this.groupCommandBuilder = new GroupCommandBuilderImpl(new GroupCommandBuilderImpl.Context());
//        this.orderByCommandBuilder = new OrderByCommandBuilderImpl(new OrderByCommandBuilderImpl.Context());
//    }
//
//    @Override
//    public void namedParameter() {
//        super.namedParameter();
//        this.conditionCommandBuilder.namedParameter();
//        this.groupCommandBuilder.namedParameter();
//        this.orderByCommandBuilder.namedParameter();
//    }
//
//    public void addSelectFields(String... fields) {
//        for (String field : fields) {
//            this.selectContext.addSelectField(this.createCommandClassField(field, true, CommandField.Type.MANUAL_FIELD));
//        }
//    }
//
//    public void addExcludeFields(String... fields) {
//        for (String field : fields) {
//            this.selectContext.addExcludeField(this.createCommandClassField(field, true, CommandField.Type.MANUAL_FIELD));
//        }
//    }
//
//    public void addFromClass(Class<?> cls) {
//        this.addFromClass(cls, null);
//    }
//
//    public void tableAlias(String alias) {
//        this.selectContext.tableAlias(alias);
//    }
//
//    public void addFromClass(Class<?> cls, String aliasName) {
//        this.selectContext.addFromClass(this.createCommandClass(cls, aliasName));
//    }
//
//    public void addGroupByField(String... fields) {
//        this.groupCommandBuilder.addGroupByField(fields);
//    }
//
//    public void addOrderByField(String... fields) {
//        this.orderByCommandBuilder.addOrderByField(fields);
//    }
//
//    public void asc() {
//        this.orderByCommandBuilder.asc();
//    }
//
//    public void desc() {
//        this.orderByCommandBuilder.desc();
//    }
//
//    @Override
//    public CommandDetails doBuild(JdbcEngineConfig jdbcEngineConfig) {
//
//        StringBuilder command = new StringBuilder(COMMAND_OPEN);
//
//        if (this.selectContext.getFromClasses().isEmpty()) {
//            throw new SonsureJdbcException("from class必须指定");
//        }
//        //如果为空没有指定，获取class的属性
//        if (this.selectContext.getSelectFields().isEmpty()) {
//            for (CommandClass fromClass : this.selectContext.getFromClasses()) {
//                Collection<ModelClassFieldDetails> classFields = this.getClassFields(fromClass.getCls());
//                for (ModelClassFieldDetails fieldMeta : classFields) {
//                    CommandField commandField = this.createCommandClassField(fieldMeta.getName(), true, CommandField.Type.MANUAL_FIELD);
//                    //黑名单
//                    if (this.selectContext.isExcludeField(commandField)) {
//                        continue;
//                    }
//                    String field = this.getTableAliasField(fromClass.getAliasName(), fieldMeta.getName());
//                    command.append(field).append(",");
//                }
//            }
//        } else {
//            for (CommandField selectField : this.selectContext.getSelectFields()) {
//                final String filedCommandName = this.getFiledCommandName(selectField, jdbcEngineConfig);
//                String field = this.getTableAliasField(selectField.getTableAlias(), filedCommandName);
//                command.append(field).append(",");
//            }
//        }
//        command.deleteCharAt(command.length() - 1);
//
//        command.append(" from ");
//        for (CommandClass fromClass : this.selectContext.getFromClasses()) {
//            command.append(this.getModelAliasName(fromClass.getCls(), fromClass.getAliasName()))
//                    .append(",");
//        }
//        command.deleteCharAt(command.length() - 1);
//
//        CommandDetails commandDetails = createCommandContext();
//
//        CommandDetails whereCommandDetails = this.conditionCommandBuilder.build(jdbcEngineConfig);
//        if (whereCommandDetails != null) {
//            command.append(whereCommandDetails.getCommand());
//            commandDetails.addCommandParameters(whereCommandDetails.getCommandParameters());
//        }
//
//        CommandDetails groupCommandDetails = this.groupCommandBuilder.build(jdbcEngineConfig);
//        if (groupCommandDetails != null) {
//            command.append(groupCommandDetails.getCommand());
//        }
//
//        CommandDetails orderByCommandDetails = this.orderByCommandBuilder.build(jdbcEngineConfig);
//        if (orderByCommandDetails != null) {
//            command.append(orderByCommandDetails.getCommand());
//        }
//
//        commandDetails.setCommand(command.toString());
//        return commandDetails;
//    }
//
//    @Getter
//    public static class Context extends QueryCommandContextBuilderContext {
//
//        private final List<CommandField> selectFields;
//
//        private final List<CommandClass> fromClasses;
//
//        private final List<CommandField> excludeFields;
//
//        public Context() {
//            this.selectFields = new ArrayList<>(16);
//            this.fromClasses = new ArrayList<>(4);
//            this.excludeFields = new ArrayList<>(16);
//        }
//
//        public void addSelectField(CommandField commandField) {
//            getSelectFields().add(commandField);
//        }
//
//        public void tableAlias(String alias) {
//            getFromClasses().get(getFromClasses().size() - 1).setAliasName(alias);
//        }
//
//        public void addFromClass(CommandClass commandClass) {
//            getFromClasses().add(commandClass);
//        }
//
//        public void addExcludeField(CommandField commandField) {
//            getExcludeFields().add(commandField);
//        }
//
//        public boolean isExcludeField(CommandField commandField) {
//            final List<CommandField> excludeFields = this.getExcludeFields();
//            if (excludeFields == null || excludeFields.isEmpty()) {
//                return false;
//            }
//            for (CommandField excludeField : excludeFields) {
//                if (StringUtils.equals(commandField.getTableAlias(), excludeField.getTableAlias()) && StringUtils.equals(commandField.getFieldName(), excludeField.getFieldName())) {
//                    return true;
//                }
//            }
//            return false;
//        }
//
//    }
//
//
//}

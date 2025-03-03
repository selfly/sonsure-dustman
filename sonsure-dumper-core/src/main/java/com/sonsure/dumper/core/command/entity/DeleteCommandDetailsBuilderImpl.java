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
//import com.sonsure.dumper.core.command.AbstractCommonCommandDetailsBuilder;
//import com.sonsure.dumper.core.command.CommandDetails;
//import com.sonsure.dumper.core.command.CommandContextBuilderContext;
//import com.sonsure.dumper.core.command.ModelClassDetails;
//import com.sonsure.dumper.core.config.JdbcEngineConfig;
//import lombok.Getter;
//
///**
// * The type Delete command context builder.
// *
// * @author liyd
// * @date 17 /4/14
// */
//@Getter
//public class DeleteCommandDetailsBuilderImpl extends AbstractCommonCommandDetailsBuilder {
//
//    private static final String COMMAND_OPEN = "delete from ";
//
//    private final ConditionCommandBuilderImpl conditionCommandBuilder;
//
//    public DeleteCommandDetailsBuilderImpl(Context deleteContext) {
//        super(deleteContext);
//        this.conditionCommandBuilder = new ConditionCommandBuilderImpl(new ConditionCommandBuilderImpl.Context());
//    }
//
//    @Override
//    public void namedParameter() {
//        super.namedParameter();
//        this.conditionCommandBuilder.namedParameter();
//    }
//
//    @Override
//    public CommandDetails doBuild(JdbcEngineConfig jdbcEngineConfig) {
//        StringBuilder command = new StringBuilder(COMMAND_OPEN);
//        ModelClassDetails modelClassDetails = this.getCommandContextBuilderContext().getUniqueModelClass();
//        command.append(modelClassDetails.getModelName());
//
//        CommandDetails commandDetails = createCommandContext();
//
//        CommandDetails whereCommandDetails = this.conditionCommandBuilder.build(jdbcEngineConfig);
//        if (whereCommandDetails != null) {
//            command.append(whereCommandDetails.getCommand());
//            commandDetails.addCommandParameters(whereCommandDetails.getCommandParameters());
//        }
//        commandDetails.setCommand(command.toString());
//        return commandDetails;
//    }
//
//    public static class Context extends CommandContextBuilderContext {
//    }
//
//}

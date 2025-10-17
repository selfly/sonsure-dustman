///*
// * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
// * You may obtain more information at
// *
// *   http://www.sonsure.com
// *
// * Designed By Selfly Lee (selfly@live.com)
// */
//
//package com.sonsure.dumper.core.command.batch;
//
//import com.sonsure.dumper.core.command.CommandDetails;
//import com.sonsure.dumper.core.command.ExecutionType;
//import com.sonsure.dumper.core.command.simple.AbstractSimpleCommandDetailsBuilder;
//import com.sonsure.dumper.core.config.JdbcEngineConfig;
//import lombok.Getter;
//
//import java.util.Collection;
//
///**
// * The type Batch update command context builder.
// *
// * @author liyd
// */
//@Getter
//public class BatchUpdateCommandDetailsBuilderImpl extends AbstractSimpleCommandDetailsBuilder<BatchUpdateCommandDetailsBuilder> implements BatchUpdateCommandDetailsBuilder {
//
//    protected Collection<?> batchData;
//    protected int batchSize;
//    protected ParameterizedSetter<?> parameterizedSetter;
//
//    @Override
//    public BatchUpdateCommandDetailsBuilder batchSize(int batchSize) {
//        this.batchSize = batchSize;
//        return getSelf();
//    }
//
//    @Override
//    public <T> BatchUpdateCommandDetailsBuilder batchData(Collection<T> batchData) {
//        this.batchData = batchData;
//        return getSelf();
//    }
//
//    @Override
//    public <T> BatchUpdateCommandDetailsBuilder parameterizedSetter(ParameterizedSetter<T> parameterizedSetter) {
//        this.parameterizedSetter = parameterizedSetter;
//        return getSelf();
//    }
//
//
//    @SuppressWarnings({"unchecked", "rawtypes"})
//    @Override
//    protected CommandDetails doCustomize(JdbcEngineConfig jdbcEngineConfig, ExecutionType executionType) {
//        BatchCommandDetails batchCommandDetails = new BatchCommandDetails();
//        batchCommandDetails.setCommand(this.getCommand());
//        batchCommandDetails.setCommandParameters(this.getCommandParameters());
//        batchCommandDetails.setBatchData(this.getBatchData());
//        batchCommandDetails.setBatchSize(this.getBatchSize());
//        batchCommandDetails.setParameterizedSetter(this.getParameterizedSetter());
//        return batchCommandDetails;
//    }
//
//}

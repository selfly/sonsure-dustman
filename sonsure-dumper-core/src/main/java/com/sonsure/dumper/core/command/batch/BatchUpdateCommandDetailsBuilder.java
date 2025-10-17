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
//import com.sonsure.dumper.core.command.simple.SimpleCommandDetailsBuilder;
//
//import java.util.Collection;
//
///**
// * The type Batch update command context builder.
// *
// * @author liyd
// */
//public interface BatchUpdateCommandDetailsBuilder extends SimpleCommandDetailsBuilder<BatchUpdateCommandDetailsBuilder> {
//
//    /**
//     * Batch size batch update command details builder.
//     *
//     * @param batchSize the batch size
//     * @return the batch update command details builder
//     */
//    BatchUpdateCommandDetailsBuilder batchSize(int batchSize);
//
//    /**
//     * Batch data batch update command details builder.
//     *
//     * @param <T>       the type parameter
//     * @param batchData the batch data
//     * @return the batch update command details builder
//     */
//    <T> BatchUpdateCommandDetailsBuilder batchData(Collection<T> batchData);
//
//    /**
//     * Parameterized setter batch update command details builder.
//     *
//     * @param <T>                 the type parameter
//     * @param parameterizedSetter the parameterized setter
//     * @return the batch update command details builder
//     */
//    <T> BatchUpdateCommandDetailsBuilder parameterizedSetter(ParameterizedSetter<T> parameterizedSetter);
//
//}

/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.batch;

import com.sonsure.dumper.core.command.CommandExecutor;

import java.util.Collection;

/**
 * The interface Batch update.
 *
 * @author liyd
 */
public interface BatchUpdateExecutor extends CommandExecutor<BatchUpdateExecutor> {

    /**
     * Execute object.
     *
     * @param <T>                 the type parameter
     * @param command             the command
     * @param batchData           the batch data
     * @param batchSize           the batch size
     * @param parameterizedSetter the parameterized setter
     * @return the object
     */
    <T> Object execute(String command, Collection<T> batchData, int batchSize, ParameterizedSetter<T> parameterizedSetter);


}

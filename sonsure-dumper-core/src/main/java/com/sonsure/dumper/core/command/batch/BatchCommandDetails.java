/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   https://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.batch;

import com.sonsure.dumper.core.command.CommandDetails;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

/**
 * The type Batch command context.
 *
 * @author liyd
 */
@Setter
@Getter
public class BatchCommandDetails<T> extends CommandDetails {

    /**
     * The Batch data.
     */
    private Collection<T> batchData;

    /**
     * The Batch size.
     */
    private int batchSize;

    /**
     * The Parameterized setter.
     */
    private ParameterizedSetter<T> parameterizedSetter;

}

package com.sonsure.dumper.core.command.batch;

import com.sonsure.dumper.core.command.build.ExecutableCmd;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

/**
 * The type Batch executable cmd.
 *
 * @author selfly
 * @param <T> the type parameter
 */
@Getter
@Setter
public class BatchExecutableCmd<T> extends ExecutableCmd {

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
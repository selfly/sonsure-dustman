package com.sonsure.dumper.core.command.build;

import java.io.Serializable;

/**
 * @author selfly
 */
@FunctionalInterface
public interface GetterFunction<T> extends Serializable {

    /**
     * Lambda getter
     *
     * @param source the source
     * @return the object
     */
    Object get(T source);
}

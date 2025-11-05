package com.sonsure.dustman.jdbc.persist;

/**
 * @author selfly
 */
@FunctionalInterface
public interface ExecutionFunction<T, R> {

    /**
     * Do in connection t.
     *
     * @param t the t
     * @return the t
     */
    R apply(T t);

}

package com.sonsure.dumper.core.persist;

import java.sql.SQLException;

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
     * @throws SQLException the sql exception
     */
    R apply(T t) throws SQLException;

}

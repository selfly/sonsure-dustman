/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   https://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.batch;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * The interface Parameterized setter.
 *
 * @author liyd
 */
public interface ParameterizedSetter<T> {

    /**
     * Sets values.
     *
     * @param ps         the ps
     * @param paramNames the param names
     * @param argument   the argument
     * @throws SQLException the sql exception
     */
    void setValues(PreparedStatement ps, List<String> paramNames, T argument) throws SQLException;
}

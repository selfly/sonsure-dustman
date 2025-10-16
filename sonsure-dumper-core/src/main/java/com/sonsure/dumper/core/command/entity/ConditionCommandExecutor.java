/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;

import com.sonsure.dumper.core.command.CommandExecutor;
import com.sonsure.dumper.core.command.SqlOperator;
import com.sonsure.dumper.core.command.SqlPart;
import com.sonsure.dumper.core.command.lambda.Function;

/**
 * 条件构建接口
 *
 * @author liyd
 * @since 17/4/11
 */
public interface ConditionCommandExecutor<E extends ConditionCommandExecutor<E>> extends CommandExecutor<E> {

    /**
     * Where c.
     *
     * @return the c
     */
    E where();

    /**
     * where 属性条件
     *
     * @param field       the field
     * @param sqlOperator the sql operator
     * @param value       the value
     * @return c c
     */
    E where(String field, SqlOperator sqlOperator, Object value);

    /**
     * where 属性条件
     *
     * @param <T>         the type parameter
     * @param <R>         the type parameter
     * @param function    the function
     * @param sqlOperator the sql operator
     * @param value       the value
     * @return c c
     */
    <T, R> E where(Function<T, R> function, SqlOperator sqlOperator, Object value);

    /**
     * Where c.
     *
     * @param <T>      the type parameter
     * @param <R>      the type parameter
     * @param function the function
     * @param value    the value
     * @return the c
     */
    <T, R> E where(Function<T, R> function, Object value);

    /**
     * Where c.
     *
     * @param field the field
     * @param value the value
     * @return the c
     */
    E where(String field, Object value);

    /**
     * Where c.
     *
     * @param sqlPart the sql part
     * @return the c
     */
    E where(SqlPart sqlPart);

    /**
     * 实体属性条件
     *
     * @param obj the obj
     * @return c c
     */
    E whereForObject(Object obj);

    /**
     * Where append t.
     *
     * @param segment the segment
     * @return the t
     */
    E whereAppend(String segment);

    /**
     * Where append t.
     *
     * @param segment the segment
     * @param value   the value
     * @return the t
     */
    E whereAppend(String segment, Object value);

    /**
     * Open paren c.
     *
     * @return the c
     */
    E openParen();

    /**
     * Close paren c.
     *
     * @return the c
     */
    E closeParen();

    /**
     * and
     *
     * @return c
     */
    E and();

    /**
     * or
     *
     * @return c
     */
    E or();

}

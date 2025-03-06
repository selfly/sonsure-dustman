/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;

import com.sonsure.dumper.core.command.SqlOperator;
import com.sonsure.dumper.core.command.lambda.Function;

/**
 * 条件构建
 * 实现标识接口
 * <p>
 *
 * @author liyd
 * @date 17/4/11
 */
public interface ConditionCommandExecutor<C extends ConditionCommandExecutor<C>> extends EntityCommandExecutor<C> {

    /**
     * where 属性条件
     *
     * @param field       the field
     * @param sqlOperator the sql operator
     * @param value       the value
     * @return c c
     */
    C where(String field, SqlOperator sqlOperator, Object value);

    /**
     * where 属性条件
     *
     * @param <E>         the type parameter
     * @param <R>         the type parameter
     * @param function    the function
     * @param sqlOperator the sql operator
     * @param value       the value
     * @return c c
     */
    <E, R> C where(Function<E, R> function, SqlOperator sqlOperator, Object value);

    /**
     * Where c.
     *
     * @param <E>      the type parameter
     * @param <R>      the type parameter
     * @param function the function
     * @param value    the value
     * @return the c
     */
    <E, R> C where(Function<E, R> function, Object value);

    /**
     * Where c.
     *
     * @param field the field
     * @param value the value
     * @return the c
     */
    C where(String field, Object value);

    /**
     * 实体属性条件
     *
     * @param obj the obj
     * @return c c
     */
    C whereForObject(Object obj);

    /**
     * Where append t.
     *
     * @param segment the segment
     * @return the t
     */
    C whereAppend(String segment);

    /**
     * Where append t.
     *
     * @param segment the segment
     * @param value   the value
     * @return the t
     */
    C whereAppend(String segment, Object value);

    /**
     * and
     *
     * @return c
     */
    C and();

    /**
     * or
     *
     * @return c
     */
    C or();

}

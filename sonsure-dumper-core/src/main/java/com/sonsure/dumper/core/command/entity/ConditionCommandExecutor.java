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

import java.util.Map;

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

    /**
     * append sql片断
     *
     * @param segment the segment
     * @param params  the params
     * @return c
     */
    C append(String segment, Object... params);

    /**
     * append sql片断
     *
     * @param segment the segment
     * @param params  the params
     * @return c c
     */
    C append(String segment, Map<String, Object> params);

    /**
     * 如果传入false，下一个条件将不构建
     *
     * @param iff the iff
     * @return the c
     */
    C iff(boolean iff);

    /**
     * 如果传入false，上一个条件将被删除
     *
     * @param with the with
     * @return the c
     */
    C with(boolean with);
}

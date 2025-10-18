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
import com.sonsure.dumper.core.command.build.GetterFunction;

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
     * Where c.
     *
     * @param field the field
     * @param value the value
     * @return the c
     */
    E where(String field, Object value);

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
     * Where c.
     *
     * @param <T>    the type parameter
     * @param getter the getter
     * @param value  the value
     * @return the c
     */
    <T> E where(GetterFunction<T> getter, Object value);

    /**
     * where 属性条件
     *
     * @param <T>         the type parameter
     * @param getter      the getter
     * @param sqlOperator the sql operator
     * @param value       the value
     * @return c c
     */
    <T> E where(GetterFunction<T> getter, SqlOperator sqlOperator, Object value);

    /**
     * condition
     *
     * @param field the field
     * @param value the value
     * @return the c
     */
    E condition(String field, Object value);

    /**
     * condition 属性条件
     *
     * @param field       the field
     * @param sqlOperator the sql operator
     * @param value       the value
     * @return c c
     */
    E condition(String field, SqlOperator sqlOperator, Object value);

    /**
     * condition
     *
     * @param <T>    the type parameter
     * @param getter the getter
     * @param value  the value
     * @return the c
     */
    <T> E condition(GetterFunction<T> getter, Object value);

    /**
     * condition 属性条件
     *
     * @param <T>         the type parameter
     * @param getter      the getter
     * @param sqlOperator the sql operator
     * @param value       the value
     * @return c c
     */
    <T> E condition(GetterFunction<T> getter, SqlOperator sqlOperator, Object value);

    /**
     * 实体属性条件
     *
     * @param bean the bean
     * @return c c
     */
    E whereForBean(Object bean);

    /**
     * Where for bean primary key e.
     *
     * @param bean the bean
     * @return the e
     */
    E whereForBeanPrimaryKey(Object bean);

    /**
     * Where append t.
     *
     * @param segment the segment
     * @return the t
     */
    E appendSegment(String segment);

    /**
     * Where append t.
     *
     * @param segment the segment
     * @param value   the value
     * @return the t
     */
    E appendSegment(String segment, Object value);

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
     * and
     *
     * @param field the field
     * @param value the value
     * @return the c
     */
    E and(String field, Object value);

    /**
     * and 属性条件
     *
     * @param field       the field
     * @param sqlOperator the sql operator
     * @param value       the value
     * @return c c
     */
    E and(String field, SqlOperator sqlOperator, Object value);

    /**
     * and
     *
     * @param <T>    the type parameter
     * @param getter the getter
     * @param value  the value
     * @return the c
     */
    <T> E and(GetterFunction<T> getter, Object value);

    /**
     * and 属性条件
     *
     * @param <T>         the type parameter
     * @param getter      the getter
     * @param sqlOperator the sql operator
     * @param value       the value
     * @return c c
     */
    <T> E and(GetterFunction<T> getter, SqlOperator sqlOperator, Object value);

    /**
     * or
     *
     * @return c
     */
    E or();

    /**
     * or
     *
     * @param field the field
     * @param value the value
     * @return the c
     */
    E or(String field, Object value);

    /**
     * or 属性条件
     *
     * @param field       the field
     * @param sqlOperator the sql operator
     * @param value       the value
     * @return c c
     */
    E or(String field, SqlOperator sqlOperator, Object value);

    /**
     * or
     *
     * @param <T>    the type parameter
     * @param getter the getter
     * @param value  the value
     * @return the c
     */
    <T> E or(GetterFunction<T> getter, Object value);

    /**
     * or 属性条件
     *
     * @param <T>         the type parameter
     * @param getter      the getter
     * @param sqlOperator the sql operator
     * @param value       the value
     * @return c c
     */
    <T> E or(GetterFunction<T> getter, SqlOperator sqlOperator, Object value);

}

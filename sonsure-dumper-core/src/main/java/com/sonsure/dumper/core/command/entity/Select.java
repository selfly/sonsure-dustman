/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;


import com.sonsure.dumper.common.model.Page;
import com.sonsure.dumper.core.command.build.OrderBy;
import com.sonsure.dumper.core.command.QueryCommandExecutor;
import com.sonsure.dumper.core.command.build.SqlOperator;
import com.sonsure.dumper.core.command.build.GetterFunction;

import java.util.List;

/**
 * The interface Select.
 *
 * @author liyd
 * @since 17 /4/12
 */
public interface Select<M> extends QueryCommandExecutor<Select<M>>, ConditionCommandExecutor<Select<M>> {

    /**
     * Table alias select.
     *
     * @param alias the alias
     * @return the select
     */
    Select<M> as(String alias);

    /**
     * Select 字段.
     *
     * @return the select
     */
    Select<M> addAllColumns();

    /**
     * Select 字段.
     *
     * @param fields the fields
     * @return the select
     */
    Select<M> addColumn(String... fields);

    /**
     * Add alias column select.
     *
     * @param tableAlias the table alias
     * @param fields     the fields
     * @return the select
     */
    Select<M> addAliasColumn(String tableAlias, String... fields);

    /**
     * include.
     *
     * @param <T>    the type parameter
     * @param getter the getter
     * @return the select
     */
    <T> Select<M> addColumn(GetterFunction<T> getter);

    /**
     * 黑名单
     *
     * @param fields the fields
     * @return select
     */
    Select<M> dropColumn(String... fields);

    /**
     * Exclude select.
     *
     * @param <T>    the type parameter
     * @param getter the getter
     * @return the select
     */
    <T> Select<M> dropColumn(GetterFunction<T> getter);

    Select<M> join(String table);

    Select<M> join(Class<?> cls);

    /**
     * Inner join select.
     *
     * @param table the table
     * @return the select
     */
    Select<M> innerJoin(String table);

    /**
     * Inner join select.
     *
     * @param cls the cls
     * @return the select
     */
    Select<M> innerJoin(Class<?> cls);

    Select<M> outerJoin(String table);

    Select<M> outerJoin(Class<?> cls);

    Select<M> leftJoin(String table);

    Select<M> leftJoin(Class<?> cls);

    Select<M> rightJoin(String table);

    Select<M> rightJoin(Class<?> cls);

    /**
     * On select.
     *
     * @param on the on
     * @return the select
     */
    Select<M> on(String on);

    /**
     * On select.
     *
     * @param <T1>        the type parameter
     * @param <T2>        the type parameter
     * @param table1Field the table 1 field
     * @param table2Field the table 2 field
     * @return the select
     */
    <T1, T2> Select<M> on(GetterFunction<T1> table1Field, GetterFunction<T2> table2Field);

    /**
     * 添加 group by属性
     *
     * @param fields the fields
     * @return select
     */
    Select<M> groupBy(String... fields);

    /**
     * Group by select.
     *
     * @param <T>    the type parameter
     * @param getter the getter
     * @return the select
     */
    <T> Select<M> groupBy(GetterFunction<T> getter);

    /**
     * Order by select.
     *
     * @param field   the field
     * @param orderBy the order by
     * @return the select
     */
    Select<M> orderBy(String field, OrderBy orderBy);

    /**
     * Order by select.
     *
     * @param <T>     the type parameter
     * @param getter  the getter
     * @param orderBy the order by
     * @return the select
     */
    <T> Select<M> orderBy(GetterFunction<T> getter, OrderBy orderBy);

    Select<M> having(String having, SqlOperator sqlOperator, Object value);

    <T> Select<M> having(GetterFunction<T> getter, SqlOperator sqlOperator, Object value);

    /**
     * Single result m.
     *
     * @return the m
     */
    M findOne();

    /**
     * First result m.
     *
     * @return the m
     */
    M findFirst();

    /**
     * List .
     *
     * @return the list
     */
    List<M> findList();

    /**
     * Page result page.
     *
     * @return the page
     */
    Page<M> findPage();

}

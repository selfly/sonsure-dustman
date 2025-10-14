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
import com.sonsure.dumper.core.command.OrderBy;
import com.sonsure.dumper.core.command.QueryCommandExecutor;
import com.sonsure.dumper.core.command.SqlOperator;
import com.sonsure.dumper.core.command.SqlPart;
import com.sonsure.dumper.core.command.lambda.Function;

import java.util.List;

/**
 * The interface Select.
 *
 * @author liyd
 * @date 17 /4/12
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
     * @param <E>      the type parameter
     * @param <R>      the type parameter
     * @param function the function
     * @return the select
     */
    <E, R> Select<M> addColumn(Function<E, R> function);

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
     * @param <E>      the type parameter
     * @param <R>      the type parameter
     * @param function the function
     * @return the select
     */
    <E, R> Select<M> dropColumn(Function<E, R> function);

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
     * @param <E1>        the type parameter
     * @param <R1>        the type parameter
     * @param <E2>        the type parameter
     * @param <R2>        the type parameter
     * @param table1Field the table 1 field
     * @param table2Field the table 2 field
     * @return the select
     */
    <E1, R1, E2, R2> Select<M> on(Function<E1, R1> table1Field, Function<E2, R2> table2Field);

    /**
     * On select.
     *
     * @param <E1>        the type parameter
     * @param <R1>        the type parameter
     * @param <E2>        the type parameter
     * @param <R2>        the type parameter
     * @param table1Field the table 1 field
     * @param sqlOperator the sql operator
     * @param table2Field the table 2 field
     * @return the select
     */
    <E1, R1, E2, R2> Select<M> on(Function<E1, R1> table1Field, SqlOperator sqlOperator, Function<E2, R2> table2Field);

    /**
     * On select.
     *
     * @param sqlPart the sql part
     * @return the select
     */
    Select<M> on(SqlPart sqlPart);

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
     * @param <E>      the type parameter
     * @param <R>      the type parameter
     * @param function the function
     * @return the select
     */
    <E, R> Select<M> groupBy(Function<E, R> function);

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
     * @param <E>      the type parameter
     * @param <R>      the type parameter
     * @param function the function
     * @param orderBy  the order by
     * @return the select
     */
    <E, R> Select<M> orderBy(Function<E, R> function, OrderBy orderBy);

    /**
     * Single result m.
     *
     * @return the m
     */
    M singleResult();

    /**
     * First result m.
     *
     * @return the m
     */
    M firstResult();

    /**
     * List .
     *
     * @return the list
     */
    List<M> list();

    /**
     * Page result page.
     *
     * @return the page
     */
    Page<M> pageResult();

}

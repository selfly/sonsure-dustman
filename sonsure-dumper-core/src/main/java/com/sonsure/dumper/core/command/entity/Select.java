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
    Select<M> tableAlias(String alias);

    /**
     * From select.
     *
     * @param cls   the cls
     * @param alias the alias
     * @return the select
     */
    Select<M> from(Class<?> cls, String alias);

    /**
     * Select 字段.
     *
     * @param fields the fields
     * @return the select
     */
    Select<M> addColumn(String... fields);

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
     * 排序属性
     *
     * @param fields the fields
     * @return select
     */
    Select<M> orderBy(String... fields);

    /**
     * Order by select.
     *
     * @param field the field
     * @param type  the type
     * @return the select
     */
    Select<M> orderBy(String field, OrderBy type);

    /**
     * 属性条件
     *
     * @param <E>      the type parameter
     * @param <R>      the type parameter
     * @param function the function
     * @return select
     */
    <E, R> Select<M> orderBy(Function<E, R> function);

    /**
     * Order by select.
     *
     * @param <E>      the type parameter
     * @param <R>      the type parameter
     * @param function the function
     * @param type     the type
     * @return the select
     */
    <E, R> Select<M> orderBy(Function<E, R> function, OrderBy type);

    /**
     * asc排序
     *
     * @return select
     */
    Select<M> asc();

    /**
     * desc 排序
     *
     * @return select
     */
    Select<M> desc();

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

/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;


import com.sonsure.dumper.core.command.QueryCommandExecutor;
import com.sonsure.dumper.core.command.lambda.Function;

/**
 * The interface Select.
 *
 * @author liyd
 * @date 17 /4/12
 */
public interface Select extends QueryCommandExecutor<Select>, ConditionCommandExecutor<Select> {

    /**
     * Select 字段.
     *
     * @param fields the fields
     * @return the select
     */
    Select select(String... fields);

    /**
     * from表
     *
     * @param cls the cls
     * @return select select
     */
    Select from(Class<?> cls);

    /**
     * from表
     *
     * @param cls         the cls
     * @param alias       the alias
     * @param clsAndAlias the cls and alias
     * @return select select
     */
    Select from(Class<?> cls, String alias, Object... clsAndAlias);

    /**
     * 黑名单
     *
     * @param fields the fields
     * @return select
     */
    Select exclude(String... fields);

    /**
     * 添加 group by属性
     *
     * @param fields the fields
     * @return select
     */
    Select groupBy(String... fields);

    /**
     * 排序属性
     *
     * @param fields the fields
     * @return select
     */
    Select orderBy(String... fields);

    /**
     * 属性条件
     *
     * @param <E>      the type parameter
     * @param <R>      the type parameter
     * @param function the function
     * @return select
     */
    <E, R> Select orderBy(Function<E, R> function);

    /**
     * asc排序
     *
     * @return select
     */
    Select asc();

    /**
     * desc 排序
     *
     * @return select
     */
    Select desc();
}

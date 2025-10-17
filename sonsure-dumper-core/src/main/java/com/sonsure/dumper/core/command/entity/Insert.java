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
import com.sonsure.dumper.core.command.lambda.Function;

/**
 *
 * @author liyd
 * @since 17/4/14
 */
public interface Insert extends CommandExecutor<Insert> {

    /**
     * into对应class
     *
     * @param cls the cls
     * @return insert
     */
    Insert into(Class<?> cls);

    /**
     * 设置属性值
     *
     * @param field the field
     * @param value the value
     * @return the insert
     */
    Insert intoField(String field, Object value);

    /**
     * 设置属性值
     *
     * @param <E>      the type parameter
     * @param <R>      the type parameter
     * @param function the function
     * @param value    the value
     * @return insert
     */
    <E, R> Insert intoField(Function<E, R> function, Object value);

    /**
     * 根据对象设置属性
     *
     * @param obj the obj
     * @return insert insert
     */
    Insert intoForObject(Object obj);

    /**
     * 执行
     *
     * @return object
     */
    Object execute();
}

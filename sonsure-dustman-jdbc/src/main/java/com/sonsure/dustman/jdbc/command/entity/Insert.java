/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.jdbc.command.entity;

import com.sonsure.dustman.jdbc.command.CommandExecutor;
import com.sonsure.dustman.jdbc.command.build.GetterFunction;

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
     * @param <T>    the type parameter
     * @param getter the getter
     * @param value  the value
     * @return insert insert
     */
    <T> Insert intoField(GetterFunction<T> getter, Object value);

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

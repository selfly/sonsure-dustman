/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;

import com.sonsure.dumper.core.command.build.GetterFunction;

/**
 * @author liyd
 * @since 17/4/14
 */
public interface Update extends ConditionCommandExecutor<Update> {

    /**
     * 指定表
     *
     * @param cls the cls
     * @return update
     */
    Update table(Class<?> cls);

    /**
     * 设置属性值
     *
     * @param field the field
     * @param value the value
     * @return the update
     */
    Update set(String field, Object value);

    /**
     * 设置属性值
     *
     * @param <T>    the type parameter
     * @param getter the getter
     * @param value  the value
     * @return update update
     */
    <T> Update set(GetterFunction<T> getter, Object value);

    /**
     * 根据实体类设置属性
     *
     * @param bean the bean
     * @return for bean
     */
    Update setForBean(Object bean);

    /**
     * 更新null值
     *
     * @return update update
     */
    Update updateNull();

    /**
     * 执行
     *
     * @return int
     */
    int execute();
}

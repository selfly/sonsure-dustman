/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;

import com.sonsure.dumper.core.command.lambda.Function;

/**
 * @author liyd
 * @date 17/4/14
 */
public interface Update extends ConditionCommandExecutor<Update> {

    /**
     * 指定表
     *
     * @param cls
     * @return
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
     * @param function
     * @param value
     * @param <E>
     * @return
     */
    <E, R> Update set(Function<E, R> function, Object value);

    /**
     * 根据实体类设置属性,主键设置成where条件
     *
     * @param entity
     * @return
     */
    Update setForEntityWhereId(Object entity);

    /**
     * 根据实体类设置属性
     *
     * @param entity
     * @return
     */
    Update setForEntity(Object entity);

    /**
     * 更新null值
     *
     * @return
     */
    Update updateNull();

    /**
     * 执行
     *
     * @return
     */
    int execute();
}

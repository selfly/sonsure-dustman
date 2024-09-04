/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.simple;


import com.sonsure.dumper.core.command.QueryCommandExecutor;
import com.sonsure.dumper.core.management.BeanParameter;

import java.io.Serializable;
import java.util.Map;

/**
 * The interface Simple command executor.
 *
 * @param <T> the type parameter
 * @author liyd
 * @date 17 /4/25
 */
public interface SimpleCommandExecutor<T extends SimpleCommandExecutor<T>> extends QueryCommandExecutor<T> {

    /**
     * 命令
     *
     * @param command the command
     * @return t
     */
    T command(String command);

    /**
     * 参数
     *
     * @param parameters the parameters
     * @return mybatis executor
     */
    T parameters(Map<String, Object> parameters);

    /**
     * 参数
     *
     * @param name  the name
     * @param value the value
     * @return mybatis executor
     */
    T parameter(String name, Object value);

    /**
     * 参数
     *
     * @param beanParameter the bean parameter
     * @return mybatis executor
     */
    T parameter(BeanParameter beanParameter);

    /**
     * 结果处理器
     *
     * @param <E>           the type parameter
     * @param resultHandler the result handler
     * @return t
     */
    <E> T resultHandler(ResultHandler<E> resultHandler);

    /**
     * 插入
     */
    Serializable insert();

    /**
     * 插入 返回主键
     *
     * @param clazz the clazz
     * @return serializable serializable
     */
    Serializable insert(Class<?> clazz);

    /**
     * 更新
     *
     * @return int
     */
    int update();

    /**
     * 执行
     */
    void execute();

    /**
     * 执行脚本
     */
    void executeScript();
}

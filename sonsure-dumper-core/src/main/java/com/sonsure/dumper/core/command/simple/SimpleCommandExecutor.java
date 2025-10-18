/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.simple;


import com.sonsure.dumper.core.command.build.BeanParameter;
import com.sonsure.dumper.core.command.QueryCommandExecutor;

import java.io.Serializable;
import java.util.Map;

/**
 * The interface Simple command executor.
 *
 * @param <E> the type parameter
 * @author liyd
 * @since  17 /4/25
 */
public interface SimpleCommandExecutor<E extends SimpleCommandExecutor<E>> extends QueryCommandExecutor<E> {

    /**
     * 命令
     *
     * @param command the command
     * @return t
     */
    E command(String command);

    /**
     * 参数
     *
     * @param parameters the parameters
     * @return mybatis executor
     */
    E parameters(Map<String, Object> parameters);

    /**
     * 参数
     *
     * @param name  the name
     * @param value the value
     * @return mybatis executor
     */
    E parameter(String name, Object value);

    /**
     * 参数
     *
     * @param beanParameter the bean parameter
     * @return mybatis executor
     */
    E parameter(BeanParameter beanParameter);

    /**
     * 结果处理器
     *
     * @param <M>           the type parameter
     * @param resultHandler the result handler
     * @return t
     */
    <M> E resultHandler(ResultHandler<M> resultHandler);

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

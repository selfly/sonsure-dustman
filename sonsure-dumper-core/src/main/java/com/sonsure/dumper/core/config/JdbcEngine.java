/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.config;


import com.sonsure.commons.model.Page;
import com.sonsure.commons.model.Pageable;
import com.sonsure.dumper.core.command.CommandExecutor;
import com.sonsure.dumper.core.command.batch.BatchUpdateExecutor;
import com.sonsure.dumper.core.command.batch.ParameterizedSetter;
import com.sonsure.dumper.core.command.entity.Delete;
import com.sonsure.dumper.core.command.entity.Insert;
import com.sonsure.dumper.core.command.entity.Select;
import com.sonsure.dumper.core.command.entity.Update;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * The interface Jdbc engine.
 *
 * @author liyd
 * @date 17 /4/12
 */
public interface JdbcEngine {

    /**
     * jdbc 配置
     *
     * @return jdbc engine config
     */
    JdbcEngineConfig getJdbcEngineConfig();

    /**
     * Gets data source.
     *
     * @return the data source
     */
    DataSource getDataSource();

    /**
     * 创建执行器
     *
     * @param <T>                  the type parameter
     * @param commandExecutorClass 执行器class
     * @return t
     */
    <T extends CommandExecutor> T createExecutor(Class<T> commandExecutorClass);

    /**
     * insert对象
     *
     * @return insert
     */
    Insert insert();

    /**
     * insert数据
     *
     * @param entity the entity
     * @return object
     */
    Object executeInsert(Object entity);

    /**
     * 创建insert对象后指定into对象
     *
     * @param cls the cls
     * @return insert
     */
    Insert insertInto(Class<?> cls);

    /**
     * select对象
     *
     * @return select
     */
    Select select();

    /**
     * select对象
     *
     * @param fields the fields
     * @return select select
     */
    Select select(String... fields);


    /**
     * 查询所有列表
     *
     * @param <T> the type parameter
     * @param cls the cls
     * @return list
     */
    <T> List<T> find(Class<T> cls);

    /**
     * 查询列表，以entity中不为null属性为where条件
     *
     * @param <T>    the type parameter
     * @param entity the entity
     * @return list
     */
    <T> List<T> find(T entity);

    /**
     * 查询分页列表,以entity中不为null属性为where条件
     *
     * @param <T>    the type parameter
     * @param entity the entity
     * @return page
     */
    <T extends Pageable> Page<T> pageResult(T entity);

    /**
     * 查询记录数
     *
     * @param entity the entity
     * @return long
     */
    long findCount(Object entity);

    /**
     * 查询记录数
     *
     * @param cls the cls
     * @return long long
     */
    long findCount(Class<?> cls);

    /**
     * 查询单个结果
     *
     * @param <T>    the type parameter
     * @param entity the entity
     * @return t
     */
    <T> T singleResult(T entity);

    /**
     * 第一个结果
     *
     * @param <T>    the type parameter
     * @param entity the entity
     * @return t
     */
    <T> T firstResult(T entity);


    /**
     * update对象
     *
     * @return update
     */
    Update update();

    /**
     * update对象
     *
     * @param cls the cls
     * @return update
     */
    Update update(Class<?> cls);

    /**
     * 更新
     *
     * @param entity the entity
     * @return int
     */
    int executeUpdate(Object entity);

    /**
     * Batch update batch update executor.
     *
     * @return the batch update executor
     */
    BatchUpdateExecutor batchUpdate();

    /**
     * Execute batch update object.
     *
     * @param <T>                 the type parameter
     * @param command             the command
     * @param batchData           the batch data
     * @param batchSize           the batch size
     * @param parameterizedSetter the parameterized setter
     * @return the object
     */
    <T> Object executeBatchUpdate(String command, Collection<T> batchData, int batchSize, ParameterizedSetter<T> parameterizedSetter);

    /**
     * delete对象
     *
     * @return delete
     */
    Delete delete();

    /**
     * delete对象
     *
     * @param cls the cls
     * @return delete delete
     */
    Delete deleteFrom(Class<?> cls);

    /**
     * delete对象，以entity中不为null属性做为条件
     *
     * @param entity the entity
     * @return int
     */
    int executeDelete(Object entity);

    /**
     * delete对象，以entity中不为null属性做为条件
     *
     * @param cls the cls
     * @param id  the id
     * @return int int
     */
    int executeDelete(Class<?> cls, Serializable id);

    /**
     * 删除对象
     *
     * @param cls the cls
     * @return int
     */
    int executeDelete(Class<?> cls);

    /**
     * 创建select后指定from
     *
     * @param cls the cls
     * @return select
     */
    Select selectFrom(Class<?> cls);


    /**
     * 直接get对象
     *
     * @param <T> the type parameter
     * @param cls the cls
     * @param id  the id
     * @return t
     */
    <T> T get(Class<T> cls, Serializable id);

    /**
     * Gets database product.
     *
     * @return the database product
     */
    String getDatabaseProduct();
}

/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.persist;

import com.sonsure.dumper.common.model.Page;
import com.sonsure.dumper.common.model.Pageable;
import com.sonsure.dumper.core.command.CommandExecutor;
import com.sonsure.dumper.core.command.batch.BatchUpdateExecutor;
import com.sonsure.dumper.core.command.batch.ParameterizedSetter;
import com.sonsure.dumper.core.command.entity.Delete;
import com.sonsure.dumper.core.command.entity.Insert;
import com.sonsure.dumper.core.command.entity.Select;
import com.sonsure.dumper.core.command.entity.Update;
import com.sonsure.dumper.core.command.mybatis.MybatisExecutor;
import com.sonsure.dumper.core.command.natives.NativeExecutor;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author liyd
 * @date 17/4/12
 */
public interface JdbcDao {

    /**
     * 指定使用的持久化jdbc对象
     *
     * @param name the name
     * @return jdbc dao
     */
    JdbcDao use(String name);

    /**
     * 获取一个实体对象
     *
     * @param <T>         the type parameter
     * @param entityClass the entity class
     * @param id          the id
     * @return t t
     */
    <T> T get(Class<T> entityClass, Serializable id);

    /**
     * 查询所有列表
     *
     * @param <T>         the type parameter
     * @param entityClass the entity class
     * @return list
     */
    <T> List<T> find(Class<T> entityClass);

    /**
     * 根据实体条件查询列表,根据默认主键desc排序
     *
     * @param <T>    the type parameter
     * @param entity the entity
     * @return list list
     */
    <T> List<T> find(T entity);

    /**
     * 查询分页列表
     *
     * @param <T>    the type parameter
     * @param entity the entity
     * @return page
     */
    <T extends Pageable> Page<T> pageResult(T entity);

    /**
     * 根据实体条件查询记录数
     *
     * @param entity the entity
     * @return long
     */
    long findCount(Object entity);

    /**
     * 根据实体条件查询记录数
     *
     * @param cls the cls
     * @return long long
     */
    long findCount(Class<?> cls);

    /**
     * 根据实体条件查询单个结果
     *
     * @param <T>    the type parameter
     * @param entity the entity
     * @return t
     */
    <T> T singleResult(T entity);

    /**
     * 根据实体条件查询首个结果
     *
     * @param <T>    the type parameter
     * @param entity the entity
     * @return t
     */
    <T> T firstResult(T entity);

    /**
     * 插入
     *
     * @param <T>    the type parameter
     * @param entity the entity
     * @return object
     */
    <T> Object executeInsert(T entity);

    /**
     * 创建insert对象
     *
     * @param cls the cls
     * @return insert
     */
    Insert insertInto(Class<?> cls);

    /**
     * 删除
     *
     * @param entityClass the entity class
     * @param id          the id
     * @return int
     */
    int executeDelete(Class<?> entityClass, Serializable id);

    /**
     * 根据实体条件删除
     *
     * @param entity the entity
     * @return int int
     */
    int executeDelete(Object entity);

    /**
     * 删除所有对象
     *
     * @param cls the cls
     * @return int int
     */
    int executeDelete(Class<?> cls);

    /**
     * 更新
     *
     * @param entity the entity
     * @return int
     */
    int executeUpdate(Object entity);

    /**
     * 创建update对象
     *
     * @param cls the cls
     * @return update
     */
    Update update(Class<?> cls);

    /**
     * 创建select对象
     *
     * @param <M> the type parameter
     * @param cls the cls
     * @return select select
     */
    <M> Select<M> selectFrom(Class<M> cls);

    /**
     * 创建insert对象
     *
     * @return insert insert
     */
    Insert insert();

    /**
     * 创建delete对象
     *
     * @return delete
     */
    Delete delete();

    /**
     * 创建delete对象
     *
     * @param cls the cls
     * @return delete delete
     */
    Delete deleteFrom(Class<?> cls);

    /**
     * 创建update对象
     *
     * @return update update
     */
    Update update();

    /**
     * Batch update batch update executor.
     *
     * @return the batch update executor
     */
    BatchUpdateExecutor batchUpdate();

    /**
     * Batch update.
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
     * 执行脚本
     *
     * @param script the script
     */
    void executeScript(String script);

    /**
     * 创建native executor对象
     *
     * @return native executor
     */
    NativeExecutor nativeExecutor();

    /**
     * 创建mybatis executor对象
     *
     * @return mybatis executor
     */
    MybatisExecutor myBatisExecutor();

    /**
     * 创建自定义executor
     *
     * @param <T>      the type parameter
     * @param executor the executor
     * @return t
     */
    <T extends CommandExecutor> T executor(Class<T> executor);

    /**
     * Gets data source.
     *
     * @return the data source
     */
    DataSource getDataSource();

    /**
     * Gets database product.
     *
     * @return the database product
     */
    String getDatabaseProduct();
}

/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command;

import com.sonsure.dumper.common.model.Page;
import com.sonsure.dumper.common.model.Pageable;
import com.sonsure.dumper.core.command.build.ExecutableCmd;
import com.sonsure.dumper.core.command.build.ExecutableCmdBuilder;
import com.sonsure.dumper.core.command.build.ExecutionType;

import java.util.List;
import java.util.Map;

/**
 * @author liyd
 */
public interface QueryCommandExecutor<E extends QueryCommandExecutor<E>> extends CommandExecutor<E> {

    /**
     * 分页信息
     *
     * @param pageNum  the page num
     * @param pageSize the page size
     * @return c
     */
    @SuppressWarnings("unchecked")
    default E paginate(int pageNum, int pageSize) {
        this.getExecutableCmdBuilder().paginate(pageNum, pageSize);
        return (E) this;
    }

    /**
     * 指定偏移量和页大小，返回所在页数据
     *
     * @param offset the offset
     * @param size   the size
     * @return select select
     */
    @SuppressWarnings("unchecked")
    default E limit(int offset, int size) {
        this.getExecutableCmdBuilder().limit(offset, size);
        return (E) this;
    }

    /**
     * 禁用count查询
     *
     * @return select c
     */
    @SuppressWarnings("unchecked")
    default E disableCount() {
        this.getExecutableCmdBuilder().disableCountQuery();
        return (E) this;
    }

    /**
     * 单个结果
     *
     * @return t object
     */
    @SuppressWarnings("unchecked")
    default Map<String, Object> findOneForMap() {
        ExecutableCmdBuilder executableCmdBuilder = this.getExecutableCmdBuilder();
        executableCmdBuilder.executionType(ExecutionType.FIND_ONE_FOR_MAP);
        executableCmdBuilder.resultType(Map.class);
        ExecutableCmd executableCmd = executableCmdBuilder.build();
        return (Map<String, Object>) executableCmdBuilder.getJdbcContext().getPersistExecutor().execute(executableCmd);
    }

    /**
     * 简单查询，返回单一的结果，例如Long、Integer、String等
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @return t
     */
    @SuppressWarnings("unchecked")
    default <T> T findOneForScalar(Class<T> clazz) {
        ExecutableCmdBuilder executableCmdBuilder = this.getExecutableCmdBuilder();
        executableCmdBuilder.executionType(ExecutionType.FIND_ONE_FOR_SCALAR);
        executableCmdBuilder.resultType(clazz);
        ExecutableCmd executableCmd = executableCmdBuilder.build();
        return (T) executableCmdBuilder.getJdbcContext().getPersistExecutor().execute(executableCmd);
    }

    /**
     * 查询结果，返回单一的结果列表，例如List<Long>
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @return list
     */
    @SuppressWarnings("unchecked")
    default <T> List<T> findListForScalar(Class<T> clazz) {
        ExecutableCmdBuilder executableCmdBuilder = this.getExecutableCmdBuilder();
        executableCmdBuilder.executionType(ExecutionType.FIND_LIST_FOR_SCALAR);
        executableCmdBuilder.resultType(clazz);
        ExecutableCmd executableCmd = executableCmdBuilder.build();
        return (List<T>) executableCmdBuilder.getJdbcContext().getPersistExecutor().execute(executableCmd);
    }

    /**
     * 列表查询
     *
     * @return list
     */
    @SuppressWarnings("unchecked")
    default List<Map<String, Object>> findListForMap() {
        ExecutableCmdBuilder executableCmdBuilder = this.getExecutableCmdBuilder();
        executableCmdBuilder.executionType(ExecutionType.FIND_LIST_FOR_MAP);
        executableCmdBuilder.resultType(List.class);
        ExecutableCmd executableCmd = executableCmdBuilder.build();
        return (List<Map<String, Object>>) executableCmdBuilder.getJdbcContext().getPersistExecutor().execute(executableCmd);
    }

    /**
     * 分页信息
     *
     * @param pageable the pageable
     * @return select c
     */
    default E paginate(Pageable pageable) {
        return this.paginate(pageable.getPageNum(), pageable.getPageSize());
    }

    /**
     * 第一条结果
     *
     * @param <T> the type parameter
     * @param cls the cls
     * @return t
     */
    default <T> T findFirst(Class<T> cls) {
        this.paginate(1, 1).disableCount();
        Page<T> page = this.findPage(cls);
        return page.getList() != null && !page.getList().isEmpty() ? page.getList().iterator().next() : null;
    }

    /**
     * 第一条结果
     *
     * @return t object
     */
    default Map<String, Object> findFirstForMap() {
        this.paginate(1, 1).disableCount();
        Page<Map<String, Object>> page = this.findPageForMap();
        return page.getList() != null && !page.getList().isEmpty() ? page.getList().iterator().next() : null;
    }

    /**
     * 简单查询，返回单一的结果，只取第一条
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @return t
     */
    default <T> T findFirstForScalar(Class<T> clazz) {
        Page<T> page = this.paginate(1, 1)
                .disableCount()
                .findPageForScalar(clazz);
        return page.getList() != null && !page.getList().isEmpty() ? page.getList().iterator().next() : null;
    }


    /**
     * 分页列表查询
     *
     * @param <T> the type parameter
     * @param cls the cls
     * @return page
     */
    <T> Page<T> findPage(Class<T> cls);

    /**
     * 分页列表查询
     *
     * @return page
     */
    Page<Map<String, Object>> findPageForMap();

    /**
     * singleColumnList分页查询
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @return page list
     */
    <T> Page<T> findPageForScalar(Class<T> clazz);

    /**
     * count查询
     *
     * @return long
     */
    long findCount();

    /**
     * 单个结果
     *
     * @param <T> the type parameter
     * @param cls the cls
     * @return t
     */
    <T> T findOne(Class<T> cls);

    /**
     * 列表查询
     *
     * @param <T> the type parameter
     * @param cls the cls
     * @return list
     */
    <T> List<T> findList(Class<T> cls);
}

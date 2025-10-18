/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command;

import com.sonsure.dumper.common.bean.BeanKit;
import com.sonsure.dumper.common.model.Page;
import com.sonsure.dumper.core.command.build.ExecutableCmd;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.persist.PersistExecutor;

import java.util.List;
import java.util.Map;

/**
 * @author liyd
 */
public abstract class AbstractQueryCommandExecutor<E extends AbstractQueryCommandExecutor<E>> extends AbstractCommandExecutor<E> implements QueryCommandExecutor<E> {

    public AbstractQueryCommandExecutor(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
    }

    @Override
    public E paginate(int pageNum, int pageSize) {
        this.getExecutableCmdBuilder().paginate(pageNum, pageSize);
        return this.getSelf();
    }

    @Override
    public E limit(int offset, int size) {
        this.getExecutableCmdBuilder().limit(offset, size);
        return this.getSelf();
    }

    @Override
    public E disableCount() {
        this.getExecutableCmdBuilder().disableCountQuery();
        return this.getSelf();
    }

    @Override
    public long count() {
        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_ONE_COL);
        this.getExecutableCmdBuilder().resultType(Long.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        PersistExecutor persistExecutor = this.getJdbcEngineConfig().getPersistExecutor();
        String countCommand = this.getJdbcEngineConfig().getPageHandler().getCountCommand(executableCmd.getCommand(), persistExecutor.getDialect());
        ExecutableCmd countExecutableCmd = BeanKit.copyProperties(new ExecutableCmd(), executableCmd);
        countExecutableCmd.setCommand(countCommand);
        countExecutableCmd.setResultType(Long.class);
        Object result = persistExecutor.execute(countExecutableCmd);
        return (Long) result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T singleResult(Class<T> cls) {
        this.registerClassToMappingHandler(cls);
        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_SINGLE_RESULT);
        this.getExecutableCmdBuilder().resultType(cls);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return (T) this.getJdbcEngineConfig().getPersistExecutor().execute(executableCmd);
    }


    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> singleMapResult() {
        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_FOR_MAP);
        this.getExecutableCmdBuilder().resultType(Map.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return (Map<String, Object>) this.getJdbcEngineConfig().getPersistExecutor().execute(executableCmd);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T oneColResult(Class<T> clazz) {
        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_ONE_COL);
        this.getExecutableCmdBuilder().resultType(clazz);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return (T) this.getJdbcEngineConfig().getPersistExecutor().execute(executableCmd);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> oneColList(Class<T> clazz) {
        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_ONE_COL_LIST);
        this.getExecutableCmdBuilder().resultType(clazz);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return (List<T>) this.getJdbcEngineConfig().getPersistExecutor().execute(executableCmd);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> list(Class<T> cls) {
        this.registerClassToMappingHandler(cls);
        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_FOR_LIST);
        this.getExecutableCmdBuilder().resultType(cls);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return (List<T>) this.getJdbcEngineConfig().getPersistExecutor().execute(executableCmd);
    }


    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> listMaps() {
        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_FOR_MAP_LIST);
        this.getExecutableCmdBuilder().resultType(List.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return (List<Map<String, Object>>) this.getJdbcEngineConfig().getPersistExecutor().execute(executableCmd);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Page<T> pageResult(Class<T> cls) {
        this.registerClassToMappingHandler(cls);
        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_FOR_LIST);
        this.getExecutableCmdBuilder().resultType(cls);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return this.doPageResult(executableCmd, cmd -> (List<T>) getJdbcEngineConfig().getPersistExecutor().execute(cmd));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Page<Map<String, Object>> pageMapResult() {
        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_FOR_MAP_LIST);
        this.getExecutableCmdBuilder().resultType(Page.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return this.doPageResult(executableCmd, cmd -> (List<Map<String, Object>>) getJdbcEngineConfig().getPersistExecutor().execute(cmd));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Page<T> oneColPageResult(Class<T> clazz) {
        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_ONE_COL_LIST);
        this.getExecutableCmdBuilder().resultType(clazz);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return this.doPageResult(executableCmd, cmd -> (List<T>) getJdbcEngineConfig().getPersistExecutor().execute(cmd));
    }

}

/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.simple;

import com.sonsure.dumper.common.model.Page;
import com.sonsure.dumper.core.command.*;
import com.sonsure.dumper.core.command.build.ExecutableCmd;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The type Abstract simple command executor.
 *
 * @param <C> the type parameter
 * @author liyd
 * @since 17 /4/25
 */
@Getter
public abstract class AbstractSimpleCommandExecutor<C extends SimpleCommandExecutor<C>> extends AbstractCommandExecutor<C> implements SimpleCommandExecutor<C> {

    protected ResultHandler<?> resultHandler;

    public AbstractSimpleCommandExecutor(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
    }

    @Override
    public C command(String command) {
        this.getExecutableCmdBuilder().command(command);
        return this.getSelf();
    }


    @Override
    public C parameters(Map<String, Object> parameters) {
        this.getExecutableCmdBuilder().addParameters(parameters);
        return this.getSelf();
    }

    @Override
    public C parameter(String name, Object value) {
        this.getExecutableCmdBuilder().addParameter(name, value);
        return this.getSelf();
    }

    @Override
    public C parameter(BeanParameter beanParameter) {
        throw new UnsupportedOperationException("暂不支持");
    }

    @Override
    public <T> C resultHandler(ResultHandler<T> resultHandler) {
        this.resultHandler = resultHandler;
        return this.getSelf();
    }

    @Override
    public C disableCount() {
        this.getExecutableCmdBuilder().disableCountQuery();
        return this.getSelf();
    }

    @Override
    public long count() {
        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_ONE_COL);
        this.getExecutableCmdBuilder().resultType(Long.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return (Long) getJdbcEngineConfig().getPersistExecutor().execute(executableCmd);
    }

    @Override
    public <T> T singleResult(Class<T> cls) {
        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_FOR_MAP);
        this.getExecutableCmdBuilder().resultType(Map.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        Object result = getJdbcEngineConfig().getPersistExecutor().execute(executableCmd);
        return this.handleResult(result, getResultHandler(cls));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> singleMapResult() {
        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_FOR_MAP);
        this.getExecutableCmdBuilder().resultType(Map.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return (Map<String, Object>) getJdbcEngineConfig().getPersistExecutor().execute(executableCmd);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T oneColResult(Class<T> clazz) {
        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_ONE_COL);
        this.getExecutableCmdBuilder().resultType(clazz);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return (T) getJdbcEngineConfig().getPersistExecutor().execute(executableCmd);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> listMaps() {
        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_FOR_MAP_LIST);
        this.getExecutableCmdBuilder().resultType(List.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return (List<Map<String, Object>>) getJdbcEngineConfig().getPersistExecutor().execute(executableCmd);
    }

    @Override
    public C paginate(int pageNum, int pageSize) {
        this.getExecutableCmdBuilder().paginate(pageNum, pageSize);
        return this.getSelf();
    }

    @Override
    public C limit(int offset, int size) {
        this.getExecutableCmdBuilder().limit(offset, size);
        return this.getSelf();
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
        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_FOR_MAP_LIST);
        this.getExecutableCmdBuilder().resultType(List.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        List<Map<String, Object>> mapList = (List<Map<String, Object>>) this.getJdbcEngineConfig().getPersistExecutor().execute(executableCmd);
        return this.handleResult(mapList, getResultHandler(cls));
    }

    @Override
    public <T> Page<T> pageResult(Class<T> cls) {
        Page<Map<String, Object>> page = this.pageMapResult();
        return this.handleResult(page, getResultHandler(cls));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Page<Map<String, Object>> pageMapResult() {

        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_FOR_MAP_LIST);
        this.getExecutableCmdBuilder().resultType(Page.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        Page<Map<String, Object>> page = this.doPageResult(executableCmd, commandContext1 -> (List<Map<String, Object>>) getJdbcEngineConfig().getPersistExecutor().execute(commandContext1));
        Page<Map<String, Object>> resultPage = new Page<>(page.getPagination());
        if (page.getList() != null) {
            List<Map<String, Object>> list = new ArrayList<>(page.getList());
            resultPage.setList(list);
        }
        return resultPage;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Page<T> oneColPageResult(Class<T> clazz) {
        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_ONE_COL_LIST);
        this.getExecutableCmdBuilder().resultType(clazz);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return this.doPageResult(executableCmd, executableCmd1 -> (List<T>) getJdbcEngineConfig().getPersistExecutor().execute(executableCmd1));
    }

    @Override
    public Serializable insert() {
        this.getExecutableCmdBuilder().executionType(ExecutionType.INSERT);
        this.getExecutableCmdBuilder().resultType(Object.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return (Serializable) getJdbcEngineConfig().getPersistExecutor().execute(executableCmd);
    }

    @Override
    public Serializable insert(Class<?> clazz) {
        this.getExecutableCmdBuilder().executionType(ExecutionType.INSERT);
        this.getExecutableCmdBuilder().resultType(Object.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return (Serializable) this.getJdbcEngineConfig().getPersistExecutor().execute(executableCmd);
    }

    @Override
    public int update() {
        this.getExecutableCmdBuilder().executionType(ExecutionType.UPDATE);
        this.getExecutableCmdBuilder().resultType(Integer.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return (Integer) getJdbcEngineConfig().getPersistExecutor().execute(executableCmd);
    }

    @Override
    public void execute() {
        this.getExecutableCmdBuilder().executionType(ExecutionType.EXECUTE);
        this.getExecutableCmdBuilder().resultType(Void.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        getJdbcEngineConfig().getPersistExecutor().execute(executableCmd);
    }

    @Override
    public void executeScript() {
        this.getExecutableCmdBuilder().executionType(ExecutionType.EXECUTE_SCRIPT);
        this.getExecutableCmdBuilder().resultType(Void.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        getJdbcEngineConfig().getPersistExecutor().execute(executableCmd);
    }

    @SuppressWarnings("unchecked")
    protected <E> ResultHandler<E> getResultHandler(Class<E> cls) {
        if (this.resultHandler == null) {
            return DefaultResultHandler.newInstance(cls);
        }
        return (ResultHandler<E>) this.resultHandler;
    }
}

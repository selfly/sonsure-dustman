/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.simple;

import com.sonsure.commons.model.Page;
import com.sonsure.commons.model.Pageable;
import com.sonsure.dumper.core.command.AbstractCommonCommandExecutor;
import com.sonsure.dumper.core.command.CommandContext;
import com.sonsure.dumper.core.command.CommandType;
import com.sonsure.dumper.core.command.GenerateKey;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.management.BeanParameter;
import com.sonsure.dumper.core.mapping.MappingHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The type Abstract simple command executor.
 *
 * @param <T> the type parameter
 * @author liyd
 * @date 17 /4/25
 */
public abstract class AbstractSimpleCommandExecutor<T extends SimpleCommandExecutor<T>> extends AbstractCommonCommandExecutor<T> implements SimpleCommandExecutor<T> {

    protected ResultHandler<?> resultHandler;

    public AbstractSimpleCommandExecutor(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
    }

    @Override
    public T command(String command) {
        this.getCommandExecutorContext().setCommand(command);
        return (T) this;
    }


    @Override
    public T parameters(Map<String, Object> parameters) {
        if (parameters == null) {
            return (T) this;
        }
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            this.getCommandExecutorContext().addCommandParameter(entry.getKey(), entry.getValue());
        }
        return (T) this;
    }

    @Override
    public T parameter(String name, Object value) {
        this.getCommandExecutorContext().addCommandParameter(name, value);
        return (T) this;
    }

    @Override
    public T parameter(BeanParameter beanParameter) {
        throw new UnsupportedOperationException("暂不支持");
    }

    @Override
    public <E> T resultHandler(ResultHandler<E> resultHandler) {
        this.resultHandler = resultHandler;
        return (T) this;
    }

    @Override
    public long count() {
        CommandContext commandContext = this.commandContextBuilder.build(this.getCommandExecutorContext(), getJdbcEngineConfig());
        commandContext.setResultType(Long.class);
        return (Long) getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_ONE_COL);
    }

    @Override
    public Map<String, Object> singleResult() {
        CommandContext commandContext = this.commandContextBuilder.build(this.getCommandExecutorContext(), getJdbcEngineConfig());
        return (Map<String, Object>) getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_FOR_MAP);
    }

    @Override
    public <E> E oneColResult(Class<E> clazz) {
        CommandContext commandContext = this.commandContextBuilder.build(this.getCommandExecutorContext(), getJdbcEngineConfig());
        commandContext.setResultType(clazz);
        return (E) getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_ONE_COL);
    }

    @Override
    public List<Map<String, Object>> list() {
        CommandContext commandContext = this.commandContextBuilder.build(this.getCommandExecutorContext(), getJdbcEngineConfig());
        return (List<Map<String, Object>>) getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_FOR_MAP_LIST);
    }

    @Override
    public T paginate(int pageNum, int pageSize) {
        this.getCommandExecutorContext().paginate(pageNum, pageSize);
        return (T) this;
    }

    @Override
    public T paginate(Pageable pageable) {
        this.getCommandExecutorContext().paginate(pageable);
        return (T) this;
    }

    @Override
    public T limit(int offset, int size) {
        this.getCommandExecutorContext().limit(offset, size);
        return (T) this;
    }

    @Override
    public T isCount(boolean isCount) {
        this.getCommandExecutorContext().setCount(isCount);
        return (T) this;
    }

    @Override
    public <T> T singleResult(Class<T> cls) {
        CommandContext commandContext = this.commandContextBuilder.build(this.getCommandExecutorContext(), getJdbcEngineConfig());
        Object result = getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_FOR_MAP);
        return this.handleResult(result, getResultHandler(cls));
    }

    @Override
    public <T> T firstResult(Class<T> cls) {
        this.paginate(1, 1).isCount(false);
        Page<T> page = this.pageResult(cls);
        return page.getList() != null && !page.getList().isEmpty() ? page.getList().iterator().next() : null;
    }

    @Override
    public Map<String, Object> firstResult() {
        this.paginate(1, 1).isCount(false);
        Page<Map<String, Object>> page = this.pageResult();
        return page.getList() != null && !page.getList().isEmpty() ? page.getList().iterator().next() : null;
    }

    @Override
    public <T> List<T> oneColList(Class<T> clazz) {
        CommandContext commandContext = this.commandContextBuilder.build(this.getCommandExecutorContext(), getJdbcEngineConfig());
        commandContext.setResultType(clazz);
        return (List<T>) this.getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_ONE_COL_LIST);

    }

    @Override
    public <T> List<T> list(Class<T> cls) {
        CommandContext commandContext = this.commandContextBuilder.build(this.getCommandExecutorContext(), getJdbcEngineConfig());
        List<Map<String, Object>> mapList = (List<Map<String, Object>>) this.getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_FOR_MAP_LIST);
        return this.handleResult(mapList, getResultHandler(cls));
    }

    @Override
    public <T> Page<T> pageResult(Class<T> cls) {
        Page<Map<String, Object>> page = this.pageResult();
        return this.handleResult(page, getResultHandler(cls));
    }

    @Override
    public Page<Map<String, Object>> pageResult() {
        CommandContext commandContext = this.commandContextBuilder.build(this.getCommandExecutorContext(), getJdbcEngineConfig());
        Page<Map<String, Object>> page = this.doPageResult(commandContext, getCommandExecutorContext().getPagination(), getCommandExecutorContext().isCount(), commandContext1 -> (List<Map<String, Object>>) getJdbcEngineConfig().getPersistExecutor().execute(commandContext1, CommandType.QUERY_FOR_MAP_LIST));
        Page<Map<String, Object>> resultPage = new Page<>(page.getPagination());
        if (page.getList() != null) {
            List<Map<String, Object>> list = new ArrayList<>(page.getList());
            resultPage.setList(list);
        }
        return resultPage;
    }

    @Override
    public <E> E oneColFirstResult(Class<E> clazz) {
        this.paginate(1, 1).isCount(false);
        Page<E> page = this.oneColPageResult(clazz);
        return page.getList() != null && !page.getList().isEmpty() ? page.getList().iterator().next() : null;
    }

    @Override
    public <E> Page<E> oneColPageResult(Class<E> clazz) {
        CommandContext commandContext = this.commandContextBuilder.build(this.getCommandExecutorContext(), getJdbcEngineConfig());
        commandContext.setResultType(clazz);
        return this.doPageResult(commandContext, getCommandExecutorContext().getPagination(), getCommandExecutorContext().isCount(), commandContext1 -> (List<E>) getJdbcEngineConfig().getPersistExecutor().execute(commandContext1, CommandType.QUERY_ONE_COL_LIST));
    }

    @Override
    public void insert() {
        CommandContext commandContext = this.commandContextBuilder.build(this.getCommandExecutorContext(), getJdbcEngineConfig());
        GenerateKey generateKey = new GenerateKey();
        generateKey.setParameter(false);
        commandContext.setGenerateKey(generateKey);
        getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.INSERT);
    }

    @Override
    public Serializable insert(Class<?> clazz) {
        CommandContext commandContext = this.commandContextBuilder.build(this.getCommandExecutorContext(), getJdbcEngineConfig());
        MappingHandler mappingHandler = getJdbcEngineConfig().getMappingHandler();
        String pkField = mappingHandler.getPkField(clazz);
        String pkColumn = mappingHandler.getColumn(clazz, pkField);
        GenerateKey generateKey = new GenerateKey();
        generateKey.setClazz(clazz);
        generateKey.setColumn(pkColumn);
        generateKey.setParameter(false);
        commandContext.setGenerateKey(generateKey);
        return (Long) this.getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.INSERT);
    }

    @Override
    public int update() {
        CommandContext commandContext = this.commandContextBuilder.build(this.getCommandExecutorContext(), getJdbcEngineConfig());
        return (Integer) getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.UPDATE);
    }

    @Override
    public void execute() {
        CommandContext commandContext = this.commandContextBuilder.build(this.getCommandExecutorContext(), getJdbcEngineConfig());
        getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.EXECUTE);
    }

    @Override
    public void executeScript() {
        CommandContext commandContext = this.commandContextBuilder.build(this.getCommandExecutorContext(), getJdbcEngineConfig());
        getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.EXECUTE_SCRIPT);
    }

    protected <E> ResultHandler<E> getResultHandler(Class<E> cls) {
        if (this.resultHandler == null) {
            return DefaultResultHandler.newInstance(cls);
        }
        return (ResultHandler<E>) this.resultHandler;
    }
}

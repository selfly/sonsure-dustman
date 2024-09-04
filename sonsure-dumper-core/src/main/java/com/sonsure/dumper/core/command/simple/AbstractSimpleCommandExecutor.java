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
 * @param <C> the type parameter
 * @author liyd
 * @date 17 /4/25
 */
@SuppressWarnings("unchecked")
public abstract class AbstractSimpleCommandExecutor<C extends SimpleCommandExecutor<C>> extends AbstractCommonCommandExecutor<C> implements SimpleCommandExecutor<C> {

    protected AbstractSimpleCommandContextBuilder simpleCommandContextBuilder;

    protected ResultHandler<?> resultHandler;

    public AbstractSimpleCommandExecutor(JdbcEngineConfig jdbcEngineConfig, AbstractSimpleCommandContextBuilder simpleCommandContextBuilder) {
        super(jdbcEngineConfig);
        this.simpleCommandContextBuilder = simpleCommandContextBuilder;
    }

    public AbstractSimpleCommandContextBuilder getSimpleCommandContextBuilder() {
        return simpleCommandContextBuilder;
    }

    @Override
    protected <T extends AbstractCommonCommandContextBuilder> T getCommandContextBuilder() {
        return (T) this.simpleCommandContextBuilder;
    }

    @Override
    public C command(String command) {
        this.simpleCommandContextBuilder.command(command);
        return (C) this;
    }


    @Override
    public C parameters(Map<String, Object> parameters) {
        this.simpleCommandContextBuilder.parameters(parameters);
        return (C) this;
    }

    @Override
    public C parameter(String name, Object value) {
        this.simpleCommandContextBuilder.parameter(name, value);
        return (C) this;
    }

    @Override
    public C parameter(BeanParameter beanParameter) {
        throw new UnsupportedOperationException("暂不支持");
    }

    @Override
    public <T> C resultHandler(ResultHandler<T> resultHandler) {
        this.resultHandler = resultHandler;
        return (C) this;
    }

    @Override
    public long count() {
        CommandContext commandContext = this.simpleCommandContextBuilder.build(getJdbcEngineConfig());
        commandContext.setResultType(Long.class);
        return (Long) getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_ONE_COL);
    }

    @Override
    public <T> T singleResult(Class<T> cls) {
        CommandContext commandContext = this.simpleCommandContextBuilder.build(getJdbcEngineConfig());
        Object result = getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_FOR_MAP);
        return this.handleResult(result, getResultHandler(cls));
    }

    @Override
    public Map<String, Object> singleMapResult() {
        CommandContext commandContext = this.simpleCommandContextBuilder.build(getJdbcEngineConfig());
        return (Map<String, Object>) getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_FOR_MAP);
    }

    @Override
    public <T> T oneColResult(Class<T> clazz) {
        CommandContext commandContext = this.simpleCommandContextBuilder.build(getJdbcEngineConfig());
        commandContext.setResultType(clazz);
        return (T) getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_ONE_COL);
    }

    @Override
    public List<Map<String, Object>> listMaps() {
        CommandContext commandContext = this.simpleCommandContextBuilder.build(getJdbcEngineConfig());
        return (List<Map<String, Object>>) getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_FOR_MAP_LIST);
    }

    @Override
    public C paginate(int pageNum, int pageSize) {
        this.simpleCommandContextBuilder.paginate(pageNum, pageSize);
        return (C) this;
    }

    @Override
    public C limit(int offset, int size) {
        this.simpleCommandContextBuilder.limit(offset, size);
        return (C) this;
    }

    @Override
    public C isCount(boolean isCount) {
        this.simpleCommandContextBuilder.setCount(isCount);
        return (C) this;
    }

    @Override
    public <T> List<T> oneColList(Class<T> clazz) {
        CommandContext commandContext = this.simpleCommandContextBuilder.build(getJdbcEngineConfig());
        commandContext.setResultType(clazz);
        return (List<T>) this.getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_ONE_COL_LIST);
    }

    @Override
    public <T> List<T> list(Class<T> cls) {
        CommandContext commandContext = this.simpleCommandContextBuilder.build(getJdbcEngineConfig());
        List<Map<String, Object>> mapList = (List<Map<String, Object>>) this.getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_FOR_MAP_LIST);
        return this.handleResult(mapList, getResultHandler(cls));
    }

    @Override
    public <T> Page<T> pageResult(Class<T> cls) {
        Page<Map<String, Object>> page = this.pageMapResult();
        return this.handleResult(page, getResultHandler(cls));
    }

    @Override
    public Page<Map<String, Object>> pageMapResult() {
        CommandContext commandContext = this.simpleCommandContextBuilder.build(getJdbcEngineConfig());
        Page<Map<String, Object>> page = this.doPageResult(commandContext, commandContext.getPagination(), commandContext.isCount(), commandContext1 -> (List<Map<String, Object>>) getJdbcEngineConfig().getPersistExecutor().execute(commandContext1, CommandType.QUERY_FOR_MAP_LIST));
        Page<Map<String, Object>> resultPage = new Page<>(page.getPagination());
        if (page.getList() != null) {
            List<Map<String, Object>> list = new ArrayList<>(page.getList());
            resultPage.setList(list);
        }
        return resultPage;
    }

    @Override
    public <T> Page<T> oneColPageResult(Class<T> clazz) {
        CommandContext commandContext = this.simpleCommandContextBuilder.build(getJdbcEngineConfig());
        commandContext.setResultType(clazz);
        return this.doPageResult(commandContext, commandContext.getPagination(), commandContext.isCount(), commandContext1 -> (List<T>) getJdbcEngineConfig().getPersistExecutor().execute(commandContext1, CommandType.QUERY_ONE_COL_LIST));
    }

    @Override
    public Serializable insert() {
        CommandContext commandContext = this.simpleCommandContextBuilder.build(getJdbcEngineConfig());
        GenerateKey generateKey = new GenerateKey();
        generateKey.setPkIsParamVal(false);
        commandContext.setGenerateKey(generateKey);
        return (Serializable) getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.INSERT);
    }

    @Override
    public Serializable insert(Class<?> clazz) {
        CommandContext commandContext = this.simpleCommandContextBuilder.build(getJdbcEngineConfig());
        MappingHandler mappingHandler = getJdbcEngineConfig().getMappingHandler();
        String pkField = mappingHandler.getPkField(clazz);
        String pkColumn = mappingHandler.getColumn(clazz, pkField);
        GenerateKey generateKey = new GenerateKey();
        generateKey.setClazz(clazz);
        generateKey.setColumn(pkColumn);
        generateKey.setPkIsParamVal(false);
        commandContext.setGenerateKey(generateKey);
        return (Serializable) this.getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.INSERT);
    }

    @Override
    public int update() {
        CommandContext commandContext = this.simpleCommandContextBuilder.build(getJdbcEngineConfig());
        return (Integer) getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.UPDATE);
    }

    @Override
    public void execute() {
        CommandContext commandContext = this.simpleCommandContextBuilder.build(getJdbcEngineConfig());
        getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.EXECUTE);
    }

    @Override
    public void executeScript() {
        CommandContext commandContext = this.simpleCommandContextBuilder.build(getJdbcEngineConfig());
        getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.EXECUTE_SCRIPT);
    }

    protected <E> ResultHandler<E> getResultHandler(Class<E> cls) {
        if (this.resultHandler == null) {
            return DefaultResultHandler.newInstance(cls);
        }
        return (ResultHandler<E>) this.resultHandler;
    }
}

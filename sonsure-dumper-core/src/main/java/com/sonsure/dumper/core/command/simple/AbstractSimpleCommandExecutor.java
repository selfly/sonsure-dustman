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
import com.sonsure.dumper.core.command.AbstractCommonCommandExecutor;
import com.sonsure.dumper.core.command.CommandDetails;
import com.sonsure.dumper.core.command.CommandDetailsBuilder;
import com.sonsure.dumper.core.command.CommandType;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.management.BeanParameter;
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
 * @date 17 /4/25
 */
@Getter
public abstract class AbstractSimpleCommandExecutor<C extends SimpleCommandExecutor<C>> extends AbstractCommonCommandExecutor<C> implements SimpleCommandExecutor<C> {

    protected SimpleCommandDetailsBuilder<?> simpleCommandDetailsBuilder;

    protected ResultHandler<?> resultHandler;

    public AbstractSimpleCommandExecutor(JdbcEngineConfig jdbcEngineConfig, SimpleCommandDetailsBuilder<?> simpleCommandDetailsBuilder) {
        super(jdbcEngineConfig);
        this.simpleCommandDetailsBuilder = simpleCommandDetailsBuilder;
    }

    @Override
    protected <T extends CommandDetailsBuilder<T>> T getCommandDetailsBuilder() {
        //noinspection unchecked
        return (T) this.simpleCommandDetailsBuilder;
    }

    @Override
    public C command(String command) {
        this.simpleCommandDetailsBuilder.command(command);
        return this.getSelf();
    }


    @Override
    public C parameters(Map<String, Object> parameters) {
        this.simpleCommandDetailsBuilder.parameters(parameters);
        return this.getSelf();
    }

    @Override
    public C parameter(String name, Object value) {
        this.simpleCommandDetailsBuilder.parameter(name, value);
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
        this.simpleCommandDetailsBuilder.disableCountQuery();
        return this.getSelf();
    }

    @Override
    public long count() {
        CommandDetails commandDetails = this.simpleCommandDetailsBuilder.build(getJdbcEngineConfig(), CommandType.QUERY_ONE_COL);
        commandDetails.setResultType(Long.class);
        return (Long) getJdbcEngineConfig().getPersistExecutor().execute(commandDetails);
    }

    @Override
    public <T> T singleResult(Class<T> cls) {
        CommandDetails commandDetails = this.simpleCommandDetailsBuilder.build(getJdbcEngineConfig(), CommandType.QUERY_FOR_MAP);
        commandDetails.setResultType(Object.class);
        Object result = getJdbcEngineConfig().getPersistExecutor().execute(commandDetails);
        return this.handleResult(result, getResultHandler(cls));
    }

    @Override
    public Map<String, Object> singleMapResult() {
        CommandDetails commandDetails = this.simpleCommandDetailsBuilder.build(getJdbcEngineConfig(), CommandType.QUERY_FOR_MAP);
        commandDetails.setResultType(Map.class);
        //noinspection unchecked
        return (Map<String, Object>) getJdbcEngineConfig().getPersistExecutor().execute(commandDetails);
    }

    @Override
    public <T> T oneColResult(Class<T> clazz) {
        CommandDetails commandDetails = this.simpleCommandDetailsBuilder.build(getJdbcEngineConfig(), CommandType.QUERY_ONE_COL);
        commandDetails.setResultType(clazz);
        //noinspection unchecked
        return (T) getJdbcEngineConfig().getPersistExecutor().execute(commandDetails);
    }

    @Override
    public List<Map<String, Object>> listMaps() {
        CommandDetails commandDetails = this.simpleCommandDetailsBuilder.build(getJdbcEngineConfig(), CommandType.QUERY_FOR_MAP_LIST);
        commandDetails.setResultType(List.class);
        //noinspection unchecked
        return (List<Map<String, Object>>) getJdbcEngineConfig().getPersistExecutor().execute(commandDetails);
    }

    @Override
    public C paginate(int pageNum, int pageSize) {
        this.simpleCommandDetailsBuilder.paginate(pageNum, pageSize);
        return this.getSelf();
    }

    @Override
    public C limit(int offset, int size) {
        this.simpleCommandDetailsBuilder.limit(offset, size);
        return this.getSelf();
    }

    @Override
    public <T> List<T> oneColList(Class<T> clazz) {
        CommandDetails commandDetails = this.simpleCommandDetailsBuilder.build(getJdbcEngineConfig(), CommandType.QUERY_ONE_COL_LIST);
        commandDetails.setResultType(clazz);
        //noinspection unchecked
        return (List<T>) this.getJdbcEngineConfig().getPersistExecutor().execute(commandDetails);
    }

    @Override
    public <T> List<T> list(Class<T> cls) {
        CommandDetails commandDetails = this.simpleCommandDetailsBuilder.build(getJdbcEngineConfig(), CommandType.QUERY_FOR_MAP_LIST);
        commandDetails.setResultType(List.class);
        //noinspection unchecked
        List<Map<String, Object>> mapList = (List<Map<String, Object>>) this.getJdbcEngineConfig().getPersistExecutor().execute(commandDetails);
        return this.handleResult(mapList, getResultHandler(cls));
    }

    @Override
    public <T> Page<T> pageResult(Class<T> cls) {
        Page<Map<String, Object>> page = this.pageMapResult();
        return this.handleResult(page, getResultHandler(cls));
    }

    @Override
    public Page<Map<String, Object>> pageMapResult() {
        CommandDetails commandDetails = this.simpleCommandDetailsBuilder.build(getJdbcEngineConfig(), CommandType.QUERY_FOR_MAP_LIST);
        commandDetails.setResultType(Page.class);
        //noinspection unchecked
        Page<Map<String, Object>> page = this.doPageResult(commandDetails, commandContext1 -> (List<Map<String, Object>>) getJdbcEngineConfig().getPersistExecutor().execute(commandContext1));
        Page<Map<String, Object>> resultPage = new Page<>(page.getPagination());
        if (page.getList() != null) {
            List<Map<String, Object>> list = new ArrayList<>(page.getList());
            resultPage.setList(list);
        }
        return resultPage;
    }

    @Override
    public <T> Page<T> oneColPageResult(Class<T> clazz) {
        CommandDetails commandDetails = this.simpleCommandDetailsBuilder.build(getJdbcEngineConfig(), CommandType.QUERY_ONE_COL_LIST);
        commandDetails.setResultType(clazz);
        //noinspection unchecked
        return this.doPageResult(commandDetails, commandContext1 -> (List<T>) getJdbcEngineConfig().getPersistExecutor().execute(commandContext1));
    }

    @Override
    public Serializable insert() {
        CommandDetails commandDetails = this.simpleCommandDetailsBuilder.build(getJdbcEngineConfig(), CommandType.INSERT);
        commandDetails.setResultType(Serializable.class);
        return (Serializable) getJdbcEngineConfig().getPersistExecutor().execute(commandDetails);
    }

    @Override
    public Serializable insert(Class<?> clazz) {
        CommandDetails commandDetails = this.simpleCommandDetailsBuilder.build(getJdbcEngineConfig(), CommandType.INSERT);
        commandDetails.setResultType(Serializable.class);
        return (Serializable) this.getJdbcEngineConfig().getPersistExecutor().execute(commandDetails);
    }

    @Override
    public int update() {
        CommandDetails commandDetails = this.simpleCommandDetailsBuilder.build(getJdbcEngineConfig(), CommandType.UPDATE);
        commandDetails.setResultType(Integer.class);
        return (Integer) getJdbcEngineConfig().getPersistExecutor().execute(commandDetails);
    }

    @Override
    public void execute() {
        CommandDetails commandDetails = this.simpleCommandDetailsBuilder.build(getJdbcEngineConfig(), CommandType.EXECUTE);
        commandDetails.setResultType(Void.class);
        getJdbcEngineConfig().getPersistExecutor().execute(commandDetails);
    }

    @Override
    public void executeScript() {
        CommandDetails commandDetails = this.simpleCommandDetailsBuilder.build(getJdbcEngineConfig(), CommandType.EXECUTE_SCRIPT);
        commandDetails.setResultType(Void.class);
        getJdbcEngineConfig().getPersistExecutor().execute(commandDetails);
    }

    protected boolean isNamedParameter() {
        if (!(this.simpleCommandDetailsBuilder instanceof AbstractSimpleCommandDetailsBuilder)) {
            return false;
        }
        return ((AbstractSimpleCommandDetailsBuilder<?>) this.simpleCommandDetailsBuilder).isNamedParameter();
    }

    protected <E> ResultHandler<E> getResultHandler(Class<E> cls) {
        if (this.resultHandler == null) {
            return DefaultResultHandler.newInstance(cls);
        }
        //noinspection unchecked
        return (ResultHandler<E>) this.resultHandler;
    }
}

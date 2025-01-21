/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;


import com.sonsure.dumper.common.bean.BeanKit;
import com.sonsure.dumper.common.model.Page;
import com.sonsure.dumper.core.command.AbstractCommonCommandContextBuilder;
import com.sonsure.dumper.core.command.CommandContext;
import com.sonsure.dumper.core.command.CommandType;
import com.sonsure.dumper.core.command.OrderBy;
import com.sonsure.dumper.core.command.lambda.Function;
import com.sonsure.dumper.core.command.lambda.LambdaMethod;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.persist.PersistExecutor;

import java.util.List;
import java.util.Map;

/**
 * @author liyd
 * @date 17/4/12
 */
public class SelectImpl<M> extends AbstractConditionCommandExecutor<Select<M>> implements Select<M> {

    private final Class<M> cls;

    private final SelectCommandContextBuilderImpl selectCommandContextBuilder;

    public SelectImpl(JdbcEngineConfig jdbcEngineConfig, Class<M> cls) {
        super(jdbcEngineConfig);
        this.cls = cls;
        this.selectCommandContextBuilder = new SelectCommandContextBuilderImpl(new SelectCommandContextBuilderImpl.Context());
        this.selectCommandContextBuilder.addFromClass(cls);
        this.selectCommandContextBuilder.addModelClass(cls);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends AbstractCommonCommandContextBuilder> T getCommandContextBuilder() {
        return (T) selectCommandContextBuilder;
    }

    @Override
    protected ConditionCommandBuilderImpl getConditionCommandBuilder() {
        return this.selectCommandContextBuilder.getConditionCommandBuilder();
    }

    @Override
    public Select<M> tableAlias(String alias) {
        this.selectCommandContextBuilder.tableAlias(alias);
        return this;
    }

    @Override
    public Select<M> from(Class<?> cls, String alias) {
        this.selectCommandContextBuilder.addFromClass(cls, alias);
        return this;
    }

    @Override
    public Select<M> addColumn(String... fields) {
        this.selectCommandContextBuilder.addSelectFields(fields);
        return this;
    }

    @Override
    public final <E, R> Select<M> addColumn(Function<E, R> function) {
        String[] fields = LambdaMethod.getFields(function);
        this.addColumn(fields);
        return this;
    }

    @Override
    public Select<M> dropColumn(String... fields) {
        this.selectCommandContextBuilder.addExcludeFields(fields);
        return this;
    }

    @Override
    public <E, R> Select<M> dropColumn(Function<E, R> function) {
        String[] fields = LambdaMethod.getFields(function);
        this.dropColumn(fields);
        return this;
    }

    @Override
    public Select<M> groupBy(String... fields) {
        this.selectCommandContextBuilder.addGroupByField(fields);
        return this;
    }

    @Override
    public <E, R> Select<M> groupBy(Function<E, R> function) {
        String[] fields = LambdaMethod.getFields(function);
        this.groupBy(fields);
        return this;
    }

    @Override
    public Select<M> orderBy(String... fields) {
        this.selectCommandContextBuilder.addOrderByField(fields);
        return this;
    }

    @Override
    public Select<M> orderBy(String fields, OrderBy type) {
        this.orderBy(fields);
        if (type == OrderBy.ASC) {
            this.asc();
        } else {
            this.desc();
        }
        return this;
    }

    @Override
    public <E, R> Select<M> orderBy(Function<E, R> function) {
        String[] fields = LambdaMethod.getFields(function);
        this.orderBy(fields);
        return this;
    }

    @Override
    public <E, R> Select<M> orderBy(Function<E, R> function, OrderBy type) {
        String[] fields = LambdaMethod.getFields(function);
        this.orderBy(fields[0], type);
        return this;
    }

    @Override
    public Select<M> asc() {
        this.selectCommandContextBuilder.asc();
        return this;
    }

    @Override
    public Select<M> desc() {
        this.selectCommandContextBuilder.desc();
        return this;
    }


    @Override
    public Select<M> paginate(int pageNum, int pageSize) {
        this.selectCommandContextBuilder.paginate(pageNum, pageSize);
        return this;
    }

    @Override
    public Select<M> limit(int offset, int size) {
        this.selectCommandContextBuilder.limit(offset, size);
        return this;
    }

    @Override
    public Select<M> isCount(boolean isCount) {
        this.selectCommandContextBuilder.setCount(isCount);
        return this;
    }

    @Override
    public long count() {
        CommandContext commandContext = this.selectCommandContextBuilder.build(getJdbcEngineConfig());
        PersistExecutor persistExecutor = this.jdbcEngineConfig.getPersistExecutor();
        String countCommand = this.jdbcEngineConfig.getPageHandler().getCountCommand(commandContext.getCommand(), persistExecutor.getDialect());
        CommandContext countCommandContext = BeanKit.copyProperties(new CommandContext(), commandContext);
        countCommandContext.setCommand(countCommand);
        countCommandContext.setResultType(Long.class);
        Object result = persistExecutor.execute(countCommandContext, CommandType.QUERY_ONE_COL);
        return (Long) result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T singleResult(Class<T> cls) {
        CommandContext commandContext = this.selectCommandContextBuilder.build(getJdbcEngineConfig());
        commandContext.setResultType(cls);
        return (T) this.getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_SINGLE_RESULT);
    }


    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> singleMapResult() {
        CommandContext commandContext = this.selectCommandContextBuilder.build(getJdbcEngineConfig());
        return (Map<String, Object>) this.getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_FOR_MAP);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> E oneColResult(Class<E> clazz) {
        CommandContext commandContext = this.selectCommandContextBuilder.build(getJdbcEngineConfig());
        commandContext.setResultType(clazz);
        return (E) this.getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_ONE_COL);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> oneColList(Class<E> clazz) {
        CommandContext commandContext = this.selectCommandContextBuilder.build(getJdbcEngineConfig());
        commandContext.setResultType(clazz);
        return (List<E>) this.getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_ONE_COL_LIST);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> list(Class<T> cls) {
        CommandContext commandContext = this.selectCommandContextBuilder.build(getJdbcEngineConfig());
        commandContext.setResultType(cls);
        return (List<T>) this.getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_FOR_LIST);
    }


    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> listMaps() {
        CommandContext commandContext = this.selectCommandContextBuilder.build(getJdbcEngineConfig());
        return (List<Map<String, Object>>) this.getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_FOR_MAP_LIST);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Page<T> pageResult(Class<T> cls) {
        SelectCommandContextBuilderImpl.Context selectContext = this.selectCommandContextBuilder.getSelectContext();
        CommandContext commandContext = this.selectCommandContextBuilder.build(getJdbcEngineConfig());
        commandContext.setResultType(cls);
        return this.doPageResult(commandContext, selectContext.getPagination(), selectContext.isCount(), commandContext1 -> (List<T>) getJdbcEngineConfig().getPersistExecutor().execute(commandContext1, CommandType.QUERY_FOR_LIST));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Page<Map<String, Object>> pageMapResult() {
        SelectCommandContextBuilderImpl.Context selectContext = this.selectCommandContextBuilder.getSelectContext();
        CommandContext commandContext = this.selectCommandContextBuilder.build(getJdbcEngineConfig());
        return this.doPageResult(commandContext, selectContext.getPagination(), selectContext.isCount(), commandContext1 -> (List<Map<String, Object>>) getJdbcEngineConfig().getPersistExecutor().execute(commandContext1, CommandType.QUERY_FOR_MAP_LIST));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Page<T> oneColPageResult(Class<T> clazz) {
        SelectCommandContextBuilderImpl.Context selectContext = this.selectCommandContextBuilder.getSelectContext();
        CommandContext commandContext = this.selectCommandContextBuilder.build(getJdbcEngineConfig());
        commandContext.setResultType(clazz);
        return this.doPageResult(commandContext, selectContext.getPagination(), selectContext.isCount(), commandContext1 -> (List<T>) getJdbcEngineConfig().getPersistExecutor().execute(commandContext1, CommandType.QUERY_ONE_COL_LIST));
    }

    @Override
    public M singleResult() {
        return this.singleResult(this.cls);
    }

    @Override
    public M firstResult() {
        return this.firstResult(this.cls);
    }

    @Override
    public List<M> list() {
        return this.list(cls);
    }

    @Override
    public Page<M> pageResult() {
        return this.pageResult(cls);
    }
}

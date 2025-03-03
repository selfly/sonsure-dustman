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
import com.sonsure.dumper.core.command.CommandDetails;
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

    public SelectImpl(JdbcEngineConfig jdbcEngineConfig, Class<M> cls) {
        super(jdbcEngineConfig);
        this.cls = cls;
        this.getCommandDetailsBuilder().from(cls);
    }

//    @Override
//    public Select<M> tableAlias(String alias) {
//        this.selectCommandContextBuilder.tableAlias(alias);
//        return this;
//    }

//    @Override
//    public Select<M> from(Class<?> cls, String alias) {
//        this.selectCommandContextBuilder.addFromClass(cls, alias);
//        return this;
//    }

    @Override
    public Select<M> addColumn(String... fields) {
        this.getCommandDetailsBuilder().addSelectFields(fields);
        return this;
    }

    @Override
    public final <E, R> Select<M> addColumn(Function<E, R> function) {
        this.getCommandDetailsBuilder().addSelectFields(function);
        return this;
    }

    @Override
    public Select<M> dropColumn(String... fields) {
        this.getCommandDetailsBuilder().dropSelectFields(fields);
        return this;
    }

    @Override
    public <E, R> Select<M> dropColumn(Function<E, R> function) {
        this.getCommandDetailsBuilder().dropSelectFields(function);
        return this;
    }

    @Override
    public Select<M> groupBy(String... fields) {
        this.getCommandDetailsBuilder().groupBy(fields);
        return this;
    }

    @Override
    public <E, R> Select<M> groupBy(Function<E, R> function) {
        this.getCommandDetailsBuilder().groupBy(function);
        return this;
    }

    @Override
    public Select<M> orderBy(String field, OrderBy orderBy) {
        this.getCommandDetailsBuilder().orderBy(field, orderBy);
        return this;
    }

    @Override
    public <E, R> Select<M> orderBy(Function<E, R> function, OrderBy orderBy) {
        String field = LambdaMethod.getField(function);
        this.orderBy(field, orderBy);
        return this;
    }

    @Override
    public Select<M> paginate(int pageNum, int pageSize) {
        this.getCommandDetailsBuilder().paginate(pageNum, pageSize);
        return this;
    }

    @Override
    public Select<M> limit(int offset, int size) {
        this.getCommandDetailsBuilder().limit(offset, size);
        return this;
    }

    @Override
    public Select<M> disableCount() {
        this.getCommandDetailsBuilder().disableCountQuery();
        return this;
    }

    @Override
    public long count() {
        CommandDetails commandDetails = this.getCommandDetailsBuilder().build(getJdbcEngineConfig());
        PersistExecutor persistExecutor = this.jdbcEngineConfig.getPersistExecutor();
        String countCommand = this.jdbcEngineConfig.getPageHandler().getCountCommand(commandDetails.getCommand(), persistExecutor.getDialect());
        CommandDetails countCommandDetails = BeanKit.copyProperties(new CommandDetails(), commandDetails);
        countCommandDetails.setCommand(countCommand);
        countCommandDetails.setResultType(Long.class);
        Object result = persistExecutor.execute(countCommandDetails, CommandType.QUERY_ONE_COL);
        return (Long) result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T singleResult(Class<T> cls) {
        CommandDetails commandDetails = this.getCommandDetailsBuilder().build(getJdbcEngineConfig());
        commandDetails.setResultType(cls);
        return (T) this.getJdbcEngineConfig().getPersistExecutor().execute(commandDetails, CommandType.QUERY_SINGLE_RESULT);
    }


    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> singleMapResult() {
        CommandDetails commandDetails = this.getCommandDetailsBuilder().build(getJdbcEngineConfig());
        return (Map<String, Object>) this.getJdbcEngineConfig().getPersistExecutor().execute(commandDetails, CommandType.QUERY_FOR_MAP);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> E oneColResult(Class<E> clazz) {
        CommandDetails commandDetails = this.getCommandDetailsBuilder().build(getJdbcEngineConfig());
        commandDetails.setResultType(clazz);
        return (E) this.getJdbcEngineConfig().getPersistExecutor().execute(commandDetails, CommandType.QUERY_ONE_COL);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> oneColList(Class<E> clazz) {
        CommandDetails commandDetails = this.getCommandDetailsBuilder().build(getJdbcEngineConfig());
        commandDetails.setResultType(clazz);
        return (List<E>) this.getJdbcEngineConfig().getPersistExecutor().execute(commandDetails, CommandType.QUERY_ONE_COL_LIST);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> list(Class<T> cls) {
        CommandDetails commandDetails = this.getCommandDetailsBuilder().build(getJdbcEngineConfig());
        commandDetails.setResultType(cls);
        return (List<T>) this.getJdbcEngineConfig().getPersistExecutor().execute(commandDetails, CommandType.QUERY_FOR_LIST);
    }


    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> listMaps() {
        CommandDetails commandDetails = this.getCommandDetailsBuilder().build(getJdbcEngineConfig());
        return (List<Map<String, Object>>) this.getJdbcEngineConfig().getPersistExecutor().execute(commandDetails, CommandType.QUERY_FOR_MAP_LIST);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Page<T> pageResult(Class<T> cls) {
        CommandDetails commandDetails = this.getCommandDetailsBuilder().build(getJdbcEngineConfig());
        commandDetails.setResultType(cls);
        return this.doPageResult(commandDetails, commandContext1 -> (List<T>) getJdbcEngineConfig().getPersistExecutor().execute(commandContext1, CommandType.QUERY_FOR_LIST));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Page<Map<String, Object>> pageMapResult() {
        CommandDetails commandDetails = this.getCommandDetailsBuilder().build(getJdbcEngineConfig());
        return this.doPageResult(commandDetails, commandContext1 -> (List<Map<String, Object>>) getJdbcEngineConfig().getPersistExecutor().execute(commandContext1, CommandType.QUERY_FOR_MAP_LIST));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Page<T> oneColPageResult(Class<T> clazz) {
        CommandDetails commandDetails = this.getCommandDetailsBuilder().build(getJdbcEngineConfig());
        commandDetails.setResultType(clazz);
        return this.doPageResult(commandDetails, commandContext1 -> (List<T>) getJdbcEngineConfig().getPersistExecutor().execute(commandContext1, CommandType.QUERY_ONE_COL_LIST));
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

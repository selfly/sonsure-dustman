/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;


import com.sonsure.commons.bean.BeanKit;
import com.sonsure.commons.model.Page;
import com.sonsure.commons.model.Pageable;
import com.sonsure.dumper.core.command.CommandContext;
import com.sonsure.dumper.core.command.CommandType;
import com.sonsure.dumper.core.command.lambda.Function;
import com.sonsure.dumper.core.command.lambda.LambdaMethod;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import com.sonsure.dumper.core.persist.PersistExecutor;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.Map;

/**
 * @author liyd
 * @date 17/4/12
 */
public class SelectImpl extends AbstractConditionCommandExecutor<Select> implements Select {

    private final SelectCommandContextBuilderImpl selectCommandContextBuilder;

    public SelectImpl(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
        this.selectCommandContextBuilder = new SelectCommandContextBuilderImpl(new SelectCommandContextBuilderImpl.Context());
    }

    @Override
    protected AbstractCommandContextBuilder getCommandContextBuilder() {
        return this.selectCommandContextBuilder;
    }

    @Override
    public Select select(String... fields) {
        this.selectCommandContextBuilder.addSelectFields(fields);
        return this;
    }

    @Override
    public Select from(Class<?> cls) {
        this.selectCommandContextBuilder.addFromClass(cls);
        this.selectCommandContextBuilder.addModelClass(cls);
        return this;
    }

    @Override
    public Select from(Class<?> cls, String alias, Object... clsAndAlias) {
        this.selectCommandContextBuilder.addFromClass(cls, alias);
        if (ArrayUtils.isNotEmpty(clsAndAlias)) {
            if (ArrayUtils.getLength(clsAndAlias) % 2 != 0) {
                throw new SonsureJdbcException("指定多表必须一个class一个别名对应");
            }
            for (int i = 0; i < clsAndAlias.length; i++) {
                Object clazz = clsAndAlias[i];
                Object aliasName = clsAndAlias[++i];
                if (!(clazz instanceof Class) || !(aliasName instanceof String)) {
                    throw new SonsureJdbcException("指定多表必须一个class(Class类型)一个别名(String类型)对应");
                }
                selectCommandContextBuilder.addFromClass(((Class<?>) clazz), ((String) aliasName));
            }
        }
        return this;
    }

    @Override
    public Select exclude(String... fields) {
        this.selectCommandContextBuilder.addExcludeFields(fields);
        return this;
    }

    @Override
    public Select groupBy(String... fields) {
        this.selectCommandContextBuilder.addGroupByField(fields);
        return this;
    }

    @Override
    public Select orderBy(String... fields) {
        this.selectCommandContextBuilder.addOrderByField(fields);
        return this;
    }

    @Override
    public <E, R> Select orderBy(Function<E, R> function) {
        String[] fields = LambdaMethod.getFields(function);
        this.orderBy(fields);
        return this;
    }

    @Override
    public Select asc() {
        this.selectCommandContextBuilder.asc();
        return this;
    }

    @Override
    public Select desc() {
        this.selectCommandContextBuilder.desc();
        return this;
    }

    @Override
    public Select paginate(int pageNum, int pageSize) {
        this.selectCommandContextBuilder.paginate(pageNum, pageSize);
        return this;
    }

    @Override
    public Select paginate(Pageable pageable) {
        this.selectCommandContextBuilder.paginate(pageable);
        return this;
    }

    @Override
    public Select limit(int offset, int size) {
        this.selectCommandContextBuilder.limit(offset, size);
        return this;
    }

    @Override
    public Select isCount(boolean isCount) {
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
    public Map<String, Object> singleResult() {
        CommandContext commandContext = this.selectCommandContextBuilder.build(getJdbcEngineConfig());
        return (Map<String, Object>) this.getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.QUERY_FOR_MAP);
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

    @Override
    public <T> T oneColFirstResult(Class<T> clazz) {
        this.paginate(1, 1).isCount(false);
        Page<T> page = this.oneColPageResult(clazz);
        return page.getList() != null && !page.getList().isEmpty() ? page.getList().iterator().next() : null;
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
    public List<Map<String, Object>> list() {
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
    public Page<Map<String, Object>> pageResult() {
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

}

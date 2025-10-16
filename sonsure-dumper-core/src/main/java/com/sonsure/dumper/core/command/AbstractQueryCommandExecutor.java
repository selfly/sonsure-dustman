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
import com.sonsure.dumper.core.command.entity.Select;
import com.sonsure.dumper.core.command.lambda.Function;
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
        CommandDetails commandDetails = this.getExecutableCmdBuilder().build(getJdbcEngineConfig(), CommandType.QUERY_ONE_COL);
        PersistExecutor persistExecutor = this.getJdbcEngineConfig().getPersistExecutor();
        String countCommand = this.getJdbcEngineConfig().getPageHandler().getCountCommand(commandDetails.getCommand(), persistExecutor.getDialect());
        CommandDetails countCommandDetails = BeanKit.copyProperties(new CommandDetails(), commandDetails);
        countCommandDetails.setCommand(countCommand);
        countCommandDetails.setResultType(Long.class);
        Object result = persistExecutor.execute(countCommandDetails);
        return (Long) result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T singleResult(Class<T> cls) {
        this.registerClassToMappingHandler(cls);
        CommandDetails commandDetails = this.getCommandDetailsBuilder().build(getJdbcEngineConfig(), CommandType.QUERY_SINGLE_RESULT);
        commandDetails.setResultType(cls);
        return (T) this.getJdbcEngineConfig().getPersistExecutor().execute(commandDetails);
    }


    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> singleMapResult() {
        CommandDetails commandDetails = this.getCommandDetailsBuilder().build(getJdbcEngineConfig(), CommandType.QUERY_FOR_MAP);
        commandDetails.setResultType(Map.class);
        return (Map<String, Object>) this.getJdbcEngineConfig().getPersistExecutor().execute(commandDetails);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T oneColResult(Class<T> clazz) {
        CommandDetails commandDetails = this.getCommandDetailsBuilder().build(getJdbcEngineConfig(), CommandType.QUERY_ONE_COL);
        commandDetails.setResultType(clazz);
        return (T) this.getJdbcEngineConfig().getPersistExecutor().execute(commandDetails);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> oneColList(Class<T> clazz) {
        CommandDetails commandDetails = this.getCommandDetailsBuilder().build(getJdbcEngineConfig(), CommandType.QUERY_ONE_COL_LIST);
        commandDetails.setResultType(clazz);
        return (List<T>) this.getJdbcEngineConfig().getPersistExecutor().execute(commandDetails);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> list(Class<T> cls) {
        this.registerClassToMappingHandler(cls);
        CommandDetails commandDetails = this.getCommandDetailsBuilder().build(getJdbcEngineConfig(), CommandType.QUERY_FOR_LIST);
        commandDetails.setResultType(cls);
        return (List<T>) this.getJdbcEngineConfig().getPersistExecutor().execute(commandDetails);
    }


    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> listMaps() {
        CommandDetails commandDetails = this.getCommandDetailsBuilder().build(getJdbcEngineConfig(), CommandType.QUERY_FOR_MAP_LIST);
        commandDetails.setResultType(List.class);
        return (List<Map<String, Object>>) this.getJdbcEngineConfig().getPersistExecutor().execute(commandDetails);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Page<T> pageResult(Class<T> cls) {
        this.registerClassToMappingHandler(cls);
        CommandDetails commandDetails = this.getCommandDetailsBuilder().build(getJdbcEngineConfig(), CommandType.QUERY_FOR_LIST);
        commandDetails.setResultType(cls);
        return this.doPageResult(commandDetails, commandContext1 -> (List<T>) getJdbcEngineConfig().getPersistExecutor().execute(commandContext1));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Page<Map<String, Object>> pageMapResult() {
        CommandDetails commandDetails = this.getCommandDetailsBuilder().build(getJdbcEngineConfig(), CommandType.QUERY_FOR_MAP_LIST);
        commandDetails.setResultType(Page.class);
        return this.doPageResult(commandDetails, commandContext1 -> (List<Map<String, Object>>) getJdbcEngineConfig().getPersistExecutor().execute(commandContext1));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Page<T> oneColPageResult(Class<T> clazz) {
        CommandDetails commandDetails = this.getCommandDetailsBuilder().build(getJdbcEngineConfig(), CommandType.QUERY_ONE_COL_LIST);
        commandDetails.setResultType(clazz);
        return this.doPageResult(commandDetails, commandContext1 -> (List<T>) getJdbcEngineConfig().getPersistExecutor().execute(commandContext1));
    }

}

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
import com.sonsure.dumper.common.model.Pagination;
import com.sonsure.dumper.core.command.simple.ResultHandler;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The type Abstract command executor.
 *
 * @author liyd
 * @date 17 /4/19
 */
@Getter
@Setter
public abstract class AbstractCommonCommandExecutor<E extends CommonCommandExecutor<E>> implements CommonCommandExecutor<E> {

    private JdbcEngineConfig jdbcEngineConfig;

    public AbstractCommonCommandExecutor(JdbcEngineConfig jdbcEngineConfig) {
        this.jdbcEngineConfig = jdbcEngineConfig;
    }

    @Override
    public E forceNative() {
        this.getCommandDetailsBuilder().forceNative();
        return this.getSelf();
    }

//    @Override
//    public E namedParameter() {
//        this.getCommandDetailsBuilder().namedParameter();
//        return (E) this;
//    }

    protected <T> Page<T> doPageResult(CommandDetails commandDetails, PageQueryHandler<T> pageQueryHandler) {
        Pagination pagination = commandDetails.getPagination();
        if (pagination == null) {
            throw new SonsureJdbcException("查询分页列表请设置分页信息");
        }
        String dialect = getJdbcEngineConfig().getPersistExecutor().getDialect();
        long count = Long.MAX_VALUE;
        if (!commandDetails.isDisableCountQuery()) {
            String countCommand = getJdbcEngineConfig().getPageHandler().getCountCommand(commandDetails.getCommand(), dialect);
            CommandDetails countCommandDetails = BeanKit.copyProperties(new CommandDetails(), commandDetails);
            countCommandDetails.setCommand(countCommand);
            countCommandDetails.setCommandType(CommandType.QUERY_ONE_COL);
            countCommandDetails.setResultType(Long.class);
            Object result = getJdbcEngineConfig().getPersistExecutor().execute(countCommandDetails);
            count = (Long) result;
        }
        pagination.setTotalItems((int) count);
        String pageCommand = getJdbcEngineConfig().getPageHandler().getPageCommand(commandDetails.getCommand(), pagination, dialect);
        CommandDetails pageCommandDetails = BeanKit.copyProperties(new CommandDetails(), commandDetails);
        pageCommandDetails.setCommand(pageCommand);
        List<T> list = pageQueryHandler.queryList(pageCommandDetails);

        return new Page<>(list, pagination);
    }

    protected <T> T handleResult(Object result, ResultHandler<T> resultHandler) {
        if (result == null) {
            return null;
        }
        return resultHandler.handle(result);
    }

    protected <T> List<T> handleResult(List<?> result, ResultHandler<T> resultHandler) {
        if (result == null) {
            return Collections.emptyList();
        }
        List<T> resultList = new ArrayList<>();
        for (Object obj : result) {
            T e = this.handleResult(obj, resultHandler);
            resultList.add(e);
        }
        return resultList;
    }

    protected <T> Page<T> handleResult(Page<?> page, ResultHandler<T> resultHandler) {
        Page<T> newPage = new Page<>(page.getPagination());
        if (page.getList() == null || page.getList().isEmpty()) {
            return newPage;
        }
        List<T> resultList = new ArrayList<>();
        for (Object obj : page.getList()) {
            T e = this.handleResult(obj, resultHandler);
            resultList.add(e);
        }
        newPage.setList(resultList);
        return newPage;
    }

    protected E getSelf() {
        //noinspection unchecked
        return (E) this;
    }

    /**
     * Gets command details builder.
     *
     * @param <T> the type parameter
     * @return the command details builder
     */
    protected abstract <T extends CommandDetailsBuilder<T>> T getCommandDetailsBuilder();

    protected interface PageQueryHandler<T> {

        /**
         * Query list.
         *
         * @param commandDetails the command context
         * @return the list
         */
        List<T> queryList(CommandDetails commandDetails);
    }
}

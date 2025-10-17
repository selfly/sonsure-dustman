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
import com.sonsure.dumper.core.command.build.ExecutableCmd;
import com.sonsure.dumper.core.command.build.ExecutableCmdBuilder;
import com.sonsure.dumper.core.command.build.ExecutableCmdBuilderImpl;
import com.sonsure.dumper.core.command.simple.ResultHandler;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import com.sonsure.dumper.core.mapping.AbstractMappingHandler;
import com.sonsure.dumper.core.mapping.MappingHandler;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The type Abstract command executor.
 *
 * @author liyd
 * @since 17 /4/19
 */
@Getter
@Setter
public abstract class AbstractCommandExecutor<E extends CommandExecutor<E>> implements CommandExecutor<E> {

    private JdbcEngineConfig jdbcEngineConfig;
    private ExecutableCmdBuilder executableCmdBuilder;

    public AbstractCommandExecutor(JdbcEngineConfig jdbcEngineConfig) {
        this.jdbcEngineConfig = jdbcEngineConfig;
        this.executableCmdBuilder = new ExecutableCmdBuilderImpl();
        this.executableCmdBuilder.jdbcEngineConfig(jdbcEngineConfig);
    }

    @Override
    public E forceNative() {
        this.getExecutableCmdBuilder().forceNative();
        return this.getSelf();
    }

    @Override
    public E namedParameter() {
        this.getExecutableCmdBuilder().namedParameter();
        return this.getSelf();
    }

    protected <T> Page<T> doPageResult(ExecutableCmd executableCmd, PageQueryHandler<T> pageQueryHandler) {
        Pagination pagination = executableCmd.getPagination();
        if (pagination == null) {
            throw new SonsureJdbcException("查询分页列表请设置分页信息");
        }
        String dialect = getJdbcEngineConfig().getPersistExecutor().getDialect();
        long count = Long.MAX_VALUE;
        if (!executableCmd.isDisableCountQuery()) {
            String countCommand = getJdbcEngineConfig().getPageHandler().getCountCommand(executableCmd.getCommand(), dialect);
            ExecutableCmd countExecutableCmd = BeanKit.copyProperties(new ExecutableCmd(), executableCmd);
            countExecutableCmd.setCommand(countCommand);
            countExecutableCmd.setExecutionType(ExecutionType.QUERY_ONE_COL);
            countExecutableCmd.setResultType(Long.class);
            Object result = getJdbcEngineConfig().getPersistExecutor().execute(countExecutableCmd);
            count = (Long) result;
        }
        pagination.setTotalItems((int) count);
        String pageCommand = getJdbcEngineConfig().getPageHandler().getPageCommand(executableCmd.getCommand(), pagination, dialect);
        ExecutableCmd pageExecutableCmd = BeanKit.copyProperties(new ExecutableCmd(), executableCmd);
        pageExecutableCmd.setCommand(pageCommand);
        List<T> list = pageQueryHandler.queryList(pageExecutableCmd);

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

    @SuppressWarnings("unchecked")
    protected E getSelf() {
        return (E) this;
    }

    protected void registerClassToMappingHandler(Class<?> cls) {
        MappingHandler mappingHandler = this.getJdbcEngineConfig().getMappingHandler();
        if (mappingHandler instanceof AbstractMappingHandler) {
            ((AbstractMappingHandler) mappingHandler).addClassMapping(cls);
        }
    }

    protected interface PageQueryHandler<T> {
        /**
         * Query list.
         *
         * @param executableCmd the executable cmd
         * @return the list
         */
        List<T> queryList(ExecutableCmd executableCmd);
    }
}

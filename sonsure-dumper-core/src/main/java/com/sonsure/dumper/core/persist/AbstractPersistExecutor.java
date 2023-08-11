/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.persist;


import com.sonsure.dumper.core.command.CommandContext;
import com.sonsure.dumper.core.command.CommandType;
import com.sonsure.dumper.core.command.batch.BatchCommandContext;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.convert.JdbcTypeConverterComposite;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import com.sonsure.dumper.core.interceptor.PersistInterceptor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * The type Abstract persist executor.
 *
 * @author liyd
 * @date 17 /4/12
 */
public abstract class AbstractPersistExecutor implements PersistExecutor {

    protected JdbcEngineConfig jdbcEngineConfig;

    protected String dialect;

    @Override
    public String getDialect() {
        if (StringUtils.isBlank(dialect)) {
            dialect = this.doGetDialect();
        }
        return dialect;
    }


    @Override
    public Object execute(CommandContext commandContext, CommandType commandType) {
        if (getJdbcEngineConfig().getJdbcTypeConverters() != null) {
            JdbcTypeConverterComposite jdbcTypeConverterComposite = new JdbcTypeConverterComposite(getJdbcEngineConfig().getJdbcTypeConverters());
            if (jdbcTypeConverterComposite.support(this.getDialect())) {
                jdbcTypeConverterComposite.convert(this.getDialect(), commandContext, commandType);
            }
        }
        final List<PersistInterceptor> persistInterceptors = getJdbcEngineConfig().getPersistInterceptors();
        boolean process = true;
        if (persistInterceptors != null) {
            for (PersistInterceptor persistInterceptor : persistInterceptors) {
                if (!persistInterceptor.executeBefore(this.getDialect(), commandContext, commandType)) {
                    process = false;
                }
            }
        }
        Object result = null;
        if (process) {
            switch (commandType) {
                case INSERT:
                    result = this.insert(commandContext);
                    break;
                case QUERY_FOR_LIST:
                    result = this.queryForList(commandContext);
                    break;
                case QUERY_SINGLE_RESULT:
                    result = this.querySingleResult(commandContext);
                    break;
                case QUERY_FOR_MAP:
                    result = this.queryForMap(commandContext);
                    break;
                case QUERY_FOR_MAP_LIST:
                    result = this.queryForMapList(commandContext);
                    break;
                case QUERY_ONE_COL:
                    result = this.queryOneCol(commandContext);
                    break;
                case QUERY_ONE_COL_LIST:
                    result = this.queryOneColList(commandContext);
                    break;
                case UPDATE:
                    result = this.update(commandContext);
                    break;
                case BATCH_UPDATE:
                    result = this.batchUpdate(((BatchCommandContext<?>) commandContext));
                    break;
                case DELETE:
                    result = this.delete(commandContext);
                    break;
                case EXECUTE:
                    result = this.doExecute(commandContext);
                    break;
                case EXECUTE_SCRIPT:
                    result = this.doExecuteScript(commandContext);
                    break;
                default:
                    throw new SonsureJdbcException("不支持的CommandType:" + commandType);
            }
        }
        if (persistInterceptors != null) {
            for (PersistInterceptor persistInterceptor : persistInterceptors) {
                result = persistInterceptor.executeAfter(this.getDialect(), commandContext, commandType, result);
            }
        }
        return result;
    }


    /**
     * insert操作，返回主键值
     *
     * @param commandContext the command context
     * @return object
     */
    protected abstract Object insert(CommandContext commandContext);

    /**
     * 列表查询，泛型object为某个实体对象
     *
     * @param commandContext the command context
     * @return list
     */
    protected abstract List<?> queryForList(CommandContext commandContext);

    /**
     * 查询单个结果对象，返回结果为某个实体对象
     *
     * @param commandContext the command context
     * @return object
     */
    protected abstract Object querySingleResult(CommandContext commandContext);

    /**
     * 查询单条记录的map结果集，key=列名，value=列值
     *
     * @param commandContext the command context
     * @return map
     */
    protected abstract Map<String, Object> queryForMap(CommandContext commandContext);

    /**
     * 查询列表记录的map结果集，key=列名，value=列值
     *
     * @param commandContext the command context
     * @return list
     */
    protected abstract List<Map<String, Object>> queryForMapList(CommandContext commandContext);

    /**
     * 查询某一列的值
     *
     * @param commandContext the command context
     * @return object
     */
    protected abstract Object queryOneCol(CommandContext commandContext);

    /**
     * 查询某一列的值列表
     *
     * @param commandContext the command context
     * @return list list
     */
    protected abstract List<?> queryOneColList(CommandContext commandContext);

    /**
     * 更新操作
     *
     * @param commandContext the command context
     * @return int
     */
    protected abstract int update(CommandContext commandContext);

    /**
     * 批量更新操作
     *
     * @param <T>            the type parameter
     * @param commandContext the command context
     * @return int int
     */
    protected abstract <T> Object batchUpdate(BatchCommandContext<T> commandContext);

    /**
     * 删除操作
     *
     * @param commandContext the command context
     * @return int
     */
    protected abstract int delete(CommandContext commandContext);

    /**
     * 执行代码
     *
     * @param commandContext the command context
     * @return object
     */
    protected abstract Object doExecute(CommandContext commandContext);

    /**
     * 执行代码
     *
     * @param commandContext the command context
     * @return object
     */
    protected abstract Object doExecuteScript(CommandContext commandContext);


    /**
     * Do get dialect string.
     *
     * @return the string
     */
    protected abstract String doGetDialect();

    public JdbcEngineConfig getJdbcEngineConfig() {
        return jdbcEngineConfig;
    }

    public void setJdbcEngineConfig(JdbcEngineConfig jdbcEngineConfig) {
        this.jdbcEngineConfig = jdbcEngineConfig;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }
    
}

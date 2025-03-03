/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.persist;


import com.sonsure.dumper.core.command.CommandDetails;
import com.sonsure.dumper.core.command.CommandType;
import com.sonsure.dumper.core.command.batch.BatchCommandDetails;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.convert.JdbcTypeConverterComposite;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import com.sonsure.dumper.core.interceptor.PersistInterceptor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * The type Abstract persist executor.
 *
 * @author liyd
 * @date 17 /4/12
 */
@Getter
@Setter
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
    public Object execute(CommandDetails commandDetails, CommandType commandType) {
        if (getJdbcEngineConfig().getJdbcTypeConverters() != null) {
            JdbcTypeConverterComposite jdbcTypeConverterComposite = new JdbcTypeConverterComposite(getJdbcEngineConfig().getJdbcTypeConverters());
            if (jdbcTypeConverterComposite.support(this.getDialect())) {
                jdbcTypeConverterComposite.convert(this.getDialect(), commandDetails, commandType);
            }
        }
        final List<PersistInterceptor> persistInterceptors = getJdbcEngineConfig().getPersistInterceptors();
        boolean process = true;
        if (persistInterceptors != null) {
            for (PersistInterceptor persistInterceptor : persistInterceptors) {
                if (!persistInterceptor.executeBefore(this.getDialect(), commandDetails, commandType)) {
                    process = false;
                }
            }
        }
        Object result = null;
        if (process) {
            switch (commandType) {
                case INSERT:
                    result = this.insert(commandDetails);
                    break;
                case QUERY_FOR_LIST:
                    result = this.queryForList(commandDetails);
                    break;
                case QUERY_SINGLE_RESULT:
                    result = this.querySingleResult(commandDetails);
                    break;
                case QUERY_FOR_MAP:
                    result = this.queryForMap(commandDetails);
                    break;
                case QUERY_FOR_MAP_LIST:
                    result = this.queryForMapList(commandDetails);
                    break;
                case QUERY_ONE_COL:
                    result = this.queryOneCol(commandDetails);
                    break;
                case QUERY_ONE_COL_LIST:
                    result = this.queryOneColList(commandDetails);
                    break;
                case UPDATE:
                    result = this.update(commandDetails);
                    break;
                case BATCH_UPDATE:
                    result = this.batchUpdate(((BatchCommandDetails<?>) commandDetails));
                    break;
                case DELETE:
                    result = this.delete(commandDetails);
                    break;
                case EXECUTE:
                    result = this.doExecute(commandDetails);
                    break;
                case EXECUTE_SCRIPT:
                    result = this.doExecuteScript(commandDetails);
                    break;
                default:
                    throw new SonsureJdbcException("不支持的CommandType:" + commandType);
            }
        }
        if (persistInterceptors != null) {
            for (PersistInterceptor persistInterceptor : persistInterceptors) {
                result = persistInterceptor.executeAfter(this.getDialect(), commandDetails, commandType, result);
            }
        }
        return result;
    }


    /**
     * insert操作，返回主键值
     *
     * @param commandDetails the command context
     * @return object
     */
    protected abstract Object insert(CommandDetails commandDetails);

    /**
     * 列表查询，泛型object为某个实体对象
     *
     * @param commandDetails the command context
     * @return list
     */
    protected abstract List<?> queryForList(CommandDetails commandDetails);

    /**
     * 查询单个结果对象，返回结果为某个实体对象
     *
     * @param commandDetails the command context
     * @return object
     */
    protected abstract Object querySingleResult(CommandDetails commandDetails);

    /**
     * 查询单条记录的map结果集，key=列名，value=列值
     *
     * @param commandDetails the command context
     * @return map
     */
    protected abstract Map<String, Object> queryForMap(CommandDetails commandDetails);

    /**
     * 查询列表记录的map结果集，key=列名，value=列值
     *
     * @param commandDetails the command context
     * @return list
     */
    protected abstract List<Map<String, Object>> queryForMapList(CommandDetails commandDetails);

    /**
     * 查询某一列的值
     *
     * @param commandDetails the command context
     * @return object
     */
    protected abstract Object queryOneCol(CommandDetails commandDetails);

    /**
     * 查询某一列的值列表
     *
     * @param commandDetails the command context
     * @return list list
     */
    protected abstract List<?> queryOneColList(CommandDetails commandDetails);

    /**
     * 更新操作
     *
     * @param commandDetails the command context
     * @return int
     */
    protected abstract int update(CommandDetails commandDetails);

    /**
     * 批量更新操作
     *
     * @param <T>            the type parameter
     * @param commandContext the command context
     * @return int int
     */
    protected abstract <T> Object batchUpdate(BatchCommandDetails<T> commandContext);

    /**
     * 删除操作
     *
     * @param commandDetails the command context
     * @return int
     */
    protected abstract int delete(CommandDetails commandDetails);

    /**
     * 执行代码
     *
     * @param commandDetails the command context
     * @return object
     */
    protected abstract Object doExecute(CommandDetails commandDetails);

    /**
     * 执行代码
     *
     * @param commandDetails the command context
     * @return object
     */
    protected abstract Object doExecuteScript(CommandDetails commandDetails);


    /**
     * Do get dialect string.
     *
     * @return the string
     */
    protected abstract String doGetDialect();
    
}

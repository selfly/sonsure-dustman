/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.persist;


import com.sonsure.dumper.core.command.batch.BatchExecutableCmd;
import com.sonsure.dumper.core.command.build.ExecutableCmd;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import com.sonsure.dumper.core.interceptor.InterceptorChain;
import com.sonsure.dumper.core.interceptor.PersistContext;
import com.sonsure.dumper.core.interceptor.PersistInterceptor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * The type Abstract persist executor.
 *
 * @author liyd
 * @since 17 /4/12
 */
@Getter
@Setter
public abstract class AbstractPersistExecutor implements PersistExecutor {

    @Override
    public Object execute(ExecutableCmd executableCmd) {
        List<PersistInterceptor> persistInterceptors = executableCmd.getJdbcContext().getPersistInterceptors();
        PersistContext persistContext = new PersistContext(this.getDatabaseProduct(), executableCmd);
        InterceptorChain interceptorChain = new InterceptorChain(persistInterceptors);
        interceptorChain.execute(persistContext);
        Object result;
        if (persistContext.isSkipExecution()) {
            result = persistContext.getResult();
        } else {
            switch (executableCmd.getExecutionType()) {
                case INSERT:
                    result = this.insert(executableCmd);
                    break;
                case QUERY_FOR_LIST:
                    result = this.queryForList(executableCmd);
                    break;
                case QUERY_SINGLE_RESULT:
                    result = this.querySingleResult(executableCmd);
                    break;
                case QUERY_FOR_MAP:
                    result = this.queryForMap(executableCmd);
                    break;
                case QUERY_FOR_MAP_LIST:
                    result = this.queryForMapList(executableCmd);
                    break;
                case QUERY_ONE_COL:
                    result = this.queryOneCol(executableCmd);
                    break;
                case QUERY_ONE_COL_LIST:
                    result = this.queryOneColList(executableCmd);
                    break;
                case UPDATE:
                    result = this.update(executableCmd);
                    break;
                case BATCH_UPDATE:
                    result = this.batchUpdate(((BatchExecutableCmd<?>) executableCmd));
                    break;
                case DELETE:
                    result = this.delete(executableCmd);
                    break;
                case EXECUTE:
                    result = this.doExecute(executableCmd);
                    break;
                case EXECUTE_SCRIPT:
                    result = this.doExecuteScript(executableCmd);
                    break;
                default:
                    throw new SonsureJdbcException("不支持的CommandType:" + executableCmd.getExecutionType());
            }
        }
        interceptorChain.reset(InterceptorChain.EXECUTION_AFTER);
        interceptorChain.execute(persistContext);
        return result;
    }


    /**
     * insert操作，返回主键值
     *
     * @param executableCmd the executable cmd
     * @return object object
     */
    protected abstract Object insert(ExecutableCmd executableCmd);

    /**
     * 列表查询，泛型object为某个实体对象
     *
     * @param executableCmd the executable cmd
     * @return list list
     */
    protected abstract List<?> queryForList(ExecutableCmd executableCmd);

    /**
     * 查询单个结果对象，返回结果为某个实体对象
     *
     * @param executableCmd the executable cmd
     * @return object object
     */
    protected abstract Object querySingleResult(ExecutableCmd executableCmd);

    /**
     * 查询单条记录的map结果集，key=列名，value=列值
     *
     * @param executableCmd the executable cmd
     * @return map map
     */
    protected abstract Map<String, Object> queryForMap(ExecutableCmd executableCmd);

    /**
     * 查询列表记录的map结果集，key=列名，value=列值
     *
     * @param executableCmd the executable cmd
     * @return list list
     */
    protected abstract List<Map<String, Object>> queryForMapList(ExecutableCmd executableCmd);

    /**
     * 查询某一列的值
     *
     * @param executableCmd the executable cmd
     * @return object object
     */
    protected abstract Object queryOneCol(ExecutableCmd executableCmd);

    /**
     * 查询某一列的值列表
     *
     * @param executableCmd the executable cmd
     * @return list
     */
    protected abstract List<?> queryOneColList(ExecutableCmd executableCmd);

    /**
     * 更新操作
     *
     * @param executableCmd the executable cmd
     * @return int int
     */
    protected abstract int update(ExecutableCmd executableCmd);

    /**
     * 批量更新操作
     *
     * @param <T>            the type parameter
     * @param commandContext the command context
     * @return int int
     */
    protected abstract <T> Object batchUpdate(BatchExecutableCmd<T> commandContext);

    /**
     * 删除操作
     *
     * @param executableCmd the executable cmd
     * @return int int
     */
    protected abstract int delete(ExecutableCmd executableCmd);

    /**
     * 执行代码
     *
     * @param executableCmd the executable cmd
     * @return object object
     */
    protected abstract Object doExecute(ExecutableCmd executableCmd);

    /**
     * 执行代码
     *
     * @param executableCmd the executable cmd
     * @return object object
     */
    protected abstract Object doExecuteScript(ExecutableCmd executableCmd);

}

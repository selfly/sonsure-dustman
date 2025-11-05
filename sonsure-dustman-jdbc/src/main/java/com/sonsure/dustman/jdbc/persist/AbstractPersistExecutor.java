/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.jdbc.persist;


import com.sonsure.dustman.jdbc.command.batch.BatchExecutableCmd;
import com.sonsure.dustman.jdbc.command.build.ExecutableCmd;
import com.sonsure.dustman.jdbc.exception.SonsureJdbcException;
import com.sonsure.dustman.jdbc.interceptor.InterceptorChain;
import com.sonsure.dustman.jdbc.interceptor.PersistContext;
import com.sonsure.dustman.jdbc.interceptor.PersistInterceptor;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * The type Abstract persist executor.
 *
 * @author liyd
 * @since 17 /4/12
 */
public abstract class AbstractPersistExecutor implements PersistExecutor {

    private String databaseProduct;

    @Override
    public Object execute(ExecutableCmd executableCmd) {
        List<PersistInterceptor> persistInterceptors = executableCmd.getJdbcContext().getPersistInterceptors();
        PersistContext persistContext = new PersistContext(executableCmd);
        InterceptorChain interceptorChain = new InterceptorChain(persistInterceptors);
        interceptorChain.execute(persistContext);
        if (!persistContext.isSkipExecution()) {
            Object result;
            switch (executableCmd.getExecutionType()) {
                case INSERT:
                    result = this.insert(executableCmd);
                    break;
                case FIND_LIST:
                    result = this.findList(executableCmd);
                    break;
                case FIND_ONE:
                    result = this.findOne(executableCmd);
                    break;
                case FIND_ONE_FOR_MAP:
                    result = this.findOneForMap(executableCmd);
                    break;
                case FIND_LIST_FOR_MAP:
                    result = this.findListForMap(executableCmd);
                    break;
                case FIND_ONE_FOR_SCALAR:
                    result = this.findOneForScalar(executableCmd);
                    break;
                case FIND_LIST_FOR_SCALAR:
                    result = this.findListForScalar(executableCmd);
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
                case EXECUTE_IN_CONNECTION:
                    result = this.doExecuteInConnection(executableCmd);
                    break;
                case EXECUTE_IN_RAW:
                    result = this.doExecutionInRaw(executableCmd);
                    break;
                case GET_DATABASE_PRODUCT:
                    if (this.databaseProduct == null) {
                        if (executableCmd.getExecutionFunction() == null) {
                            executableCmd.setExecutionFunction(connection -> {
                                try {
                                    Connection conn = (Connection) connection;
                                    final DatabaseMetaData metaData = conn.getMetaData();
                                    return metaData.getDatabaseProductName().toLowerCase() + "/" + metaData.getDatabaseProductVersion();
                                } catch (SQLException e) {
                                    throw new SonsureJdbcException("GetDatabaseProduct失败", e);
                                }
                            });
                        }
                        this.databaseProduct = (String) this.doExecuteInConnection(executableCmd);
                    }
                    result = this.databaseProduct;
                    break;
                case EXECUTE_SCRIPT:
                    result = this.doExecuteScript(executableCmd);
                    break;
                default:
                    throw new SonsureJdbcException("不支持的CommandType:" + executableCmd.getExecutionType());
            }
            persistContext.setResult(result);
        }
        interceptorChain.reset(InterceptorChain.EXECUTION_AFTER);
        interceptorChain.execute(persistContext);
        return persistContext.getResult();
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
     * @return list
     */
    protected abstract List<?> findList(ExecutableCmd executableCmd);

    /**
     * 查询单个结果对象，返回结果为某个实体对象
     *
     * @param executableCmd the executable cmd
     * @return object object
     */
    protected abstract Object findOne(ExecutableCmd executableCmd);

    /**
     * 查询单条记录的map结果集，key=列名，value=列值
     *
     * @param executableCmd the executable cmd
     * @return map map
     */
    protected abstract Map<String, Object> findOneForMap(ExecutableCmd executableCmd);

    /**
     * 查询列表记录的map结果集，key=列名，value=列值
     *
     * @param executableCmd the executable cmd
     * @return list
     */
    protected abstract List<Map<String, Object>> findListForMap(ExecutableCmd executableCmd);

    /**
     * 查询某一列的值
     *
     * @param executableCmd the executable cmd
     * @return object object
     */
    protected abstract Object findOneForScalar(ExecutableCmd executableCmd);

    /**
     * 查询某一列的值列表
     *
     * @param executableCmd the executable cmd
     * @return list
     */
    protected abstract List<?> findListForScalar(ExecutableCmd executableCmd);

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
     * Do execute in connection.
     *
     * @param executableCmd the executable cmd
     * @return the object
     */
    protected abstract Object doExecuteInConnection(ExecutableCmd executableCmd);

    /**
     * Do execution in raw object.
     *
     * @param executableCmd the executable cmd
     * @return the object
     */
    protected abstract Object doExecutionInRaw(ExecutableCmd executableCmd);

    /**
     * 执行代码
     *
     * @param executableCmd the executable cmd
     * @return object object
     */
    protected abstract Object doExecuteScript(ExecutableCmd executableCmd);

}

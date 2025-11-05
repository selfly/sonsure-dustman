/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.jdbc.command.simple;

import com.sonsure.dustman.common.model.Page;
import com.sonsure.dustman.jdbc.command.AbstractCommandExecutor;
import com.sonsure.dustman.jdbc.command.build.*;
import com.sonsure.dustman.jdbc.config.JdbcContext;
import com.sonsure.dustman.jdbc.exception.SonsureJdbcException;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * The type Abstract simple command executor.
 *
 * @param <C> the type parameter
 * @author liyd
 * @since 17 /4/25
 */
@Getter
public abstract class AbstractSimpleCommandExecutor<C extends SimpleCommandExecutor<C>> extends AbstractCommandExecutor<C> implements SimpleCommandExecutor<C> {

    protected ResultHandler<?> resultHandler;

    public AbstractSimpleCommandExecutor(JdbcContext jdbcContext) {
        super(jdbcContext);
        this.getExecutableCmdBuilder().addCustomizer(new NativeInsertExecutableCustomizer());
    }

    @Override
    public C command(String command) {
        this.getExecutableCmdBuilder().command(command);
        return this.getSelf();
    }


    @Override
    public C parameters(Map<String, Object> parameters) {
        this.getExecutableCmdBuilder().addParameters(parameters);
        return this.getSelf();
    }

    @Override
    public C parameter(String name, Object value) {
        this.getExecutableCmdBuilder().addParameter(name, value);
        return this.getSelf();
    }

    @Override
    public C parameter(BeanParameter beanParameter) {
        if (!this.getExecutableCmdBuilder().isNamedParameter()) {
            throw new SonsureJdbcException("BeanParameter需要named方式传参");
        }
        Map<String, Object> propMap = CommandBuildHelper.obj2PropMap(beanParameter.getBean(), getExecutableCmdBuilder().isUpdateNull());
        return this.parameters(propMap);
    }

    @Override
    public <T> C resultHandler(ResultHandler<T> resultHandler) {
        this.resultHandler = resultHandler;
        return this.getSelf();
    }

    @Override
    public long findCount() {
        this.getExecutableCmdBuilder().executionType(ExecutionType.FIND_ONE_FOR_SCALAR);
        this.getExecutableCmdBuilder().resultType(Long.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return (Long) getJdbcContext().getPersistExecutor().execute(executableCmd);
    }

    @Override
    public <T> T findOne(Class<T> cls) {
        this.getExecutableCmdBuilder().executionType(ExecutionType.FIND_ONE_FOR_MAP);
        this.getExecutableCmdBuilder().resultType(Map.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        Object result = getJdbcContext().getPersistExecutor().execute(executableCmd);
        return this.handleResult(result, getResultHandler(cls));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findList(Class<T> cls) {
        this.getExecutableCmdBuilder().executionType(ExecutionType.FIND_LIST_FOR_MAP);
        this.getExecutableCmdBuilder().resultType(List.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        List<Map<String, Object>> mapList = (List<Map<String, Object>>) this.getJdbcContext().getPersistExecutor().execute(executableCmd);
        return this.handleResult(mapList, getResultHandler(cls));
    }

    @Override
    public <T> Page<T> findPage(Class<T> cls) {
        Page<Map<String, Object>> page = this.findPageForMap();
        return this.handleResult(page, getResultHandler(cls));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Page<Map<String, Object>> findPageForMap() {
        this.getExecutableCmdBuilder().executionType(ExecutionType.FIND_LIST_FOR_MAP);
        this.getExecutableCmdBuilder().resultType(Page.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return this.doPageResult(executableCmd, cmd -> (List<Map<String, Object>>) getJdbcContext().getPersistExecutor().execute(cmd));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Page<T> findPageForScalar(Class<T> clazz) {
        this.getExecutableCmdBuilder().executionType(ExecutionType.FIND_LIST_FOR_SCALAR);
        this.getExecutableCmdBuilder().resultType(clazz);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return this.doPageResult(executableCmd, cmd -> (List<T>) getJdbcContext().getPersistExecutor().execute(cmd));
    }

    @Override
    public Serializable insert() {
        this.getExecutableCmdBuilder().executionType(ExecutionType.INSERT);
        this.getExecutableCmdBuilder().resultType(Object.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return (Serializable) getJdbcContext().getPersistExecutor().execute(executableCmd);
    }

    @Override
    public Serializable insert(Class<?> clazz) {
        this.getExecutableCmdBuilder().executionType(ExecutionType.INSERT);
        this.getExecutableCmdBuilder().resultType(Object.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return (Serializable) this.getJdbcContext().getPersistExecutor().execute(executableCmd);
    }

    @Override
    public int update() {
        this.getExecutableCmdBuilder().executionType(ExecutionType.UPDATE);
        this.getExecutableCmdBuilder().resultType(Integer.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return (Integer) getJdbcContext().getPersistExecutor().execute(executableCmd);
    }

    @Override
    public void execute() {
        this.getExecutableCmdBuilder().executionType(ExecutionType.EXECUTE);
        this.getExecutableCmdBuilder().resultType(Void.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        getJdbcContext().getPersistExecutor().execute(executableCmd);
    }

    @Override
    public void executeScript() {
        this.getExecutableCmdBuilder().executionType(ExecutionType.EXECUTE_SCRIPT);
        this.getExecutableCmdBuilder().resultType(Void.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        getJdbcContext().getPersistExecutor().execute(executableCmd);
    }

    @SuppressWarnings("unchecked")
    protected <E> ResultHandler<E> getResultHandler(Class<E> cls) {
        if (this.resultHandler == null) {
            return DefaultResultHandler.newInstance(cls);
        }
        return (ResultHandler<E>) this.resultHandler;
    }


    private static class NativeInsertExecutableCustomizer implements ExecutableCustomizer {

        @Override
        public void customize(ExecutableCmdBuilder executableCmdBuilder) {
            if (ExecutionType.INSERT == executableCmdBuilder.getExecutionType()) {
                GenerateKey generateKey = new GenerateKey();
                generateKey.setPrimaryKeyParameter(false);
                executableCmdBuilder.generateKey(generateKey);
            }
        }
    }
}

/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.persist;

import com.sonsure.dumper.common.model.Page;
import com.sonsure.dumper.common.model.Pageable;
import com.sonsure.dumper.core.command.CommandExecutor;
import com.sonsure.dumper.core.command.batch.BatchUpdateExecutor;
import com.sonsure.dumper.core.command.batch.ParameterizedSetter;
import com.sonsure.dumper.core.command.build.ExecutableCmd;
import com.sonsure.dumper.core.command.build.ExecutionType;
import com.sonsure.dumper.core.command.build.OrderBy;
import com.sonsure.dumper.core.command.entity.Delete;
import com.sonsure.dumper.core.command.entity.Insert;
import com.sonsure.dumper.core.command.entity.Select;
import com.sonsure.dumper.core.command.entity.Update;
import com.sonsure.dumper.core.command.mybatis.MybatisExecutor;
import com.sonsure.dumper.core.command.natives.NativeExecutor;
import com.sonsure.dumper.core.config.JdbcContext;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author liyd
 * @since 17/4/13
 */
@Setter
public abstract class AbstractJdbcDaoImpl implements JdbcDao {

    protected JdbcContext jdbcContext;

    @Getter
    protected Map<String, JdbcContext> jdbcContextMap;

    @Override
    public JdbcDao use(String name) {
        if (jdbcContextMap == null) {
            throw new SonsureJdbcException("使用多数据源模式请先初始化jdbcContextMap属性");
        }
        JdbcContext jdbcContext = jdbcContextMap.get(name);
        if (jdbcContext == null) {
            throw new SonsureJdbcException("指定的数据源上下文对象不存在");
        }
        return new FlexibleJdbcDaoImpl(jdbcContext);
    }

    @Override
    public <T> T get(Class<T> entityClass, Serializable id) {
        String pkField = this.getJdbcContext().getMappingHandler().getPkField(entityClass);
        return this.selectFrom(entityClass).where(pkField, id).findOne(entityClass);
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        String pkField = this.getJdbcContext().getMappingHandler().getPkField(entityClass);
        return this.selectFrom(entityClass).orderBy(pkField, OrderBy.DESC).findList(entityClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findList(T entity) {
        String pkField = this.getJdbcContext().getMappingHandler().getPkField(entity.getClass());
        return (List<T>) this.selectFrom(entity.getClass()).whereForBean(entity).orderBy(pkField, OrderBy.DESC).findList(entity.getClass());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Pageable> Page<T> findPage(T entity) {
        String pkField = this.getJdbcContext().getMappingHandler().getPkField(entity.getClass());
        return (Page<T>) this.selectFrom(entity.getClass()).whereForBean(entity).paginate(entity).orderBy(pkField, OrderBy.DESC).findPage(entity.getClass());
    }

    @Override
    public long findCount(Object entity) {
        return this.selectFrom(entity.getClass()).whereForBean(entity).findCount();
    }

    @Override
    public long findCount(Class<?> cls) {
        return this.selectFrom(cls).findCount();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T findOne(T entity) {
        return (T) this.selectFrom(entity.getClass()).whereForBean(entity).findOne(entity.getClass());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T findFirst(T entity) {
        return (T) this.selectFrom(entity.getClass()).whereForBean(entity).findFirst(entity.getClass());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, R> R executeInsert(T entity) {
        return (R) this.insertInto(entity.getClass()).intoForObject(entity).execute();
    }

    @Override
    public Insert insertInto(Class<?> cls) {
        return this.insert().into(cls);
    }

    @Override
    public int executeDelete(Class<?> entityClass, Serializable id) {
        String pkField = this.getJdbcContext().getMappingHandler().getPkField(entityClass);
        return this.deleteFrom(entityClass).where(pkField, id).execute();
    }

    @Override
    public int executeDelete(Object entity) {
        return this.delete().from(entity.getClass()).whereForBean(entity).execute();
    }

    @Override
    public int executeDelete(Class<?> cls) {
        return this.delete().from(cls).execute();
    }

    @Override
    public int executeUpdate(Object entity) {
        return this.update(entity.getClass())
                .setForBean(entity)
                .whereForBeanPrimaryKey(entity)
                .execute();
    }

    @Override
    public Update update(Class<?> cls) {
        return this.update().table(cls);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <M> Select<M> selectFrom(Class<M> cls) {
        return this.createExecutor(Select.class, cls);
    }

    @Override
    public Insert insert() {
        return this.createExecutor(Insert.class);
    }

    @Override
    public Delete delete() {
        return this.createExecutor(Delete.class);
    }

    @Override
    public Delete deleteFrom(Class<?> cls) {
        return this.delete().from(cls);
    }

    @Override
    public Update update() {
        return this.createExecutor(Update.class);
    }

    @Override
    public BatchUpdateExecutor batchUpdate() {
        return this.createExecutor(BatchUpdateExecutor.class);
    }

    @Override
    public <T> Object executeBatchUpdate(String command, Collection<T> batchData, int batchSize, ParameterizedSetter<T> parameterizedSetter) {
        return this.batchUpdate().execute(command, batchData, batchSize, parameterizedSetter);
    }

    @Override
    public void executeScript(String script) {
        this.nativeExecutor().command(script).forceNative().executeScript();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T executeInConnection(ExecutionFunction<Connection, T> execution) {
        ExecutableCmd executableCmd = new ExecutableCmd();
        executableCmd.setExecutionType(ExecutionType.EXECUTE_IN_CONNECTION);
        executableCmd.setJdbcContext(this.getJdbcContext());
        executableCmd.setExecutionFunction(connection -> {
            Connection conn = (Connection) connection;
            return execution.apply(conn);
        });
        return (T) this.getJdbcContext().getPersistExecutor().execute(executableCmd);
    }

    @Override
    public Object executeInRaw(ExecutionFunction<Object, Object> function) {
        ExecutableCmd executableCmd = new ExecutableCmd();
        executableCmd.setExecutionType(ExecutionType.EXECUTE_IN_RAW);
        executableCmd.setJdbcContext(this.getJdbcContext());
        executableCmd.setExecutionFunction(function);
        return this.getJdbcContext().getPersistExecutor().execute(executableCmd);
    }

    @Override
    public NativeExecutor nativeExecutor() {
        return this.createExecutor(NativeExecutor.class);
    }

    @Override
    public MybatisExecutor myBatisExecutor() {
        return this.createExecutor(MybatisExecutor.class);
    }

    @Override
    public <T extends CommandExecutor<?>> T createExecutor(Class<T> executor, Object... params) {
        return this.getJdbcContext()
                .getCommandExecutorFactory()
                .createCommandExecutor(getJdbcContext(), executor, params);
    }

    @Override
    public JdbcContext getJdbcContext() {
        if (this.jdbcContext == null) {
            throw new SonsureJdbcException("jdbcContext不能为空");
        }
        return this.jdbcContext;
    }

}

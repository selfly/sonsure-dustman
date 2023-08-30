/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.config;


import com.sonsure.commons.model.Page;
import com.sonsure.commons.model.Pageable;
import com.sonsure.dumper.core.command.CommandExecutor;
import com.sonsure.dumper.core.command.batch.BatchUpdateExecutor;
import com.sonsure.dumper.core.command.batch.ParameterizedSetter;
import com.sonsure.dumper.core.command.entity.Delete;
import com.sonsure.dumper.core.command.entity.Insert;
import com.sonsure.dumper.core.command.entity.Select;
import com.sonsure.dumper.core.command.entity.Update;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * The type Jdbc engine.
 *
 * @author liyd
 * @date 17 /4/12
 */
public class JdbcEngineImpl implements JdbcEngine {

    private JdbcEngineConfig jdbcEngineConfig;

    public JdbcEngineImpl() {
    }

    public JdbcEngineImpl(JdbcEngineConfig jdbcEngineConfig) {
        this.jdbcEngineConfig = jdbcEngineConfig;
    }

    @Override
    public <T extends CommandExecutor> T createExecutor(Class<T> commandExecutorClass) {
        return this.jdbcEngineConfig.getCommandExecutorFactory().getCommandExecutor(commandExecutorClass, this.jdbcEngineConfig);
    }

    @Override
    public JdbcEngineConfig getJdbcEngineConfig() {
        return jdbcEngineConfig;
    }

    @Override
    public DataSource getDataSource() {
        return jdbcEngineConfig.getDataSource();
    }

    @Override
    public Select select() {
        return this.createExecutor(Select.class);
    }

    @Override
    public Select select(String... fields) {
        Select select = this.createExecutor(Select.class);
        select.select(fields);
        return select;
    }

    @Override
    public Select selectFrom(Class<?> cls) {
        return this.select().from(cls);
    }

    @Override
    public <T> List<T> find(Class<T> cls) {
        String pkField = this.getJdbcEngineConfig().getMappingHandler().getPkField(cls);
        return this.selectFrom(cls).orderBy(pkField).desc().list(cls);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> find(T entity) {
        String pkField = this.getJdbcEngineConfig().getMappingHandler().getPkField(entity.getClass());
        return (List<T>) this.selectFrom(entity.getClass()).where().conditionEntity(entity).orderBy(pkField).desc().list(entity.getClass());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Pageable> Page<T> pageResult(T entity) {
        String pkField = this.getJdbcEngineConfig().getMappingHandler().getPkField(entity.getClass());
        return (Page<T>) this.selectFrom(entity.getClass()).where().conditionEntity(entity).paginate(entity).orderBy(pkField).desc().pageResult(entity.getClass());
    }

    @Override
    public long findCount(Object entity) {
        return this.selectFrom(entity.getClass()).where().conditionEntity(entity).count();
    }

    @Override
    public long findCount(Class<?> cls) {
        return this.selectFrom(cls).count();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T singleResult(T entity) {
        return (T) this.selectFrom(entity.getClass()).where().conditionEntity(entity).singleResult(entity.getClass());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T firstResult(T entity) {
        return (T) this.selectFrom(entity.getClass()).where().conditionEntity(entity).firstResult(entity.getClass());
    }

    @Override
    public <T> T get(Class<T> cls, Serializable id) {
        String pkField = this.getJdbcEngineConfig().getMappingHandler().getPkField(cls);
        return this.selectFrom(cls).where(pkField, id).singleResult(cls);
    }

    @Override
    public Insert insert() {
        return this.createExecutor(Insert.class);
    }

    @Override
    public Object executeInsert(Object entity) {
        return this.insertInto(entity.getClass()).forEntity(entity).execute();
    }

    @Override
    public Insert insertInto(Class<?> cls) {
        return this.insert().into(cls);
    }

    @Override
    public Update update() {
        return this.createExecutor(Update.class);
    }

    @Override
    public Update update(Class<?> cls) {
        return this.update().table(cls);
    }

    @Override
    public int executeUpdate(Object entity) {
        return this.update(entity.getClass()).setForEntityWhereId(entity).execute();
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
    public Delete delete() {
        return this.createExecutor(Delete.class);
    }

    @Override
    public Delete deleteFrom(Class<?> cls) {
        return this.delete().from(cls);
    }

    @Override
    public int executeDelete(Object entity) {
        return this.delete().from(entity.getClass()).where().conditionEntity(entity).execute();
    }

    @Override
    public int executeDelete(Class<?> cls, Serializable id) {
        String pkField = this.getJdbcEngineConfig().getMappingHandler().getPkField(cls);
        return this.deleteFrom(cls).where(pkField, id).execute();
    }

    @Override
    public int executeDelete(Class<?> cls) {
        return this.delete().from(cls).execute();
    }

    @Override
    public String getDatabaseProduct() {
        return this.getJdbcEngineConfig().getPersistExecutor().getDialect();
    }

    public void setJdbcEngineConfig(JdbcEngineConfig jdbcEngineConfig) {
        this.jdbcEngineConfig = jdbcEngineConfig;
    }
}

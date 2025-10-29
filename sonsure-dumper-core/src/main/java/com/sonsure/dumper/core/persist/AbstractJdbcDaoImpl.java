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
import com.sonsure.dumper.core.command.build.OrderBy;
import com.sonsure.dumper.core.command.batch.BatchUpdateExecutor;
import com.sonsure.dumper.core.command.batch.ParameterizedSetter;
import com.sonsure.dumper.core.command.entity.Delete;
import com.sonsure.dumper.core.command.entity.Insert;
import com.sonsure.dumper.core.command.entity.Select;
import com.sonsure.dumper.core.command.entity.Update;
import com.sonsure.dumper.core.command.mybatis.MybatisExecutor;
import com.sonsure.dumper.core.command.natives.NativeExecutor;
import com.sonsure.dumper.core.config.JdbcExecutor;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import lombok.Getter;
import lombok.Setter;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author liyd
 * @since 17/4/13
 */
@Setter
public abstract class AbstractJdbcDaoImpl implements JdbcDao {

    protected DataSource dataSource;

    protected JdbcExecutor defaultJdbcExecutor;

    @Getter
    protected Map<String, JdbcExecutor> jdbcExecutorMap;

    @Override
    public JdbcDao use(String name) {
        if (jdbcExecutorMap == null) {
            throw new SonsureJdbcException("使用多数据源模式请先初始化jdbcExecutorMap属性");
        }
        JdbcExecutor jdbcExecutor = jdbcExecutorMap.get(name);
        if (jdbcExecutor == null) {
            throw new SonsureJdbcException("指定的数据源操作对象不存在");
        }
        return new FlexibleJdbcDaoImpl(jdbcExecutor);
    }

    @Override
    public <T> T get(Class<T> entityClass, Serializable id) {
        String pkField = this.getDefaultJdbcExecutor().getConfig().getMappingHandler().getPkField(entityClass);
        return this.selectFrom(entityClass).where(pkField, id).singleResult(entityClass);
    }

    @Override
    public <T> List<T> find(Class<T> entityClass) {
        String pkField = this.getDefaultJdbcExecutor().getConfig().getMappingHandler().getPkField(entityClass);
        return this.selectFrom(entityClass).orderBy(pkField, OrderBy.DESC).list(entityClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> find(T entity) {
        String pkField = this.getDefaultJdbcExecutor().getConfig().getMappingHandler().getPkField(entity.getClass());
        return (List<T>) this.selectFrom(entity.getClass()).whereForBean(entity).orderBy(pkField, OrderBy.DESC).list(entity.getClass());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Pageable> Page<T> pageResult(T entity) {
        String pkField = this.getDefaultJdbcExecutor().getConfig().getMappingHandler().getPkField(entity.getClass());
        return (Page<T>) this.selectFrom(entity.getClass()).whereForBean(entity).paginate(entity).orderBy(pkField, OrderBy.DESC).pageResult(entity.getClass());
    }

    @Override
    public long findCount(Object entity) {
        return this.selectFrom(entity.getClass()).whereForBean(entity).count();
    }

    @Override
    public long findCount(Class<?> cls) {
        return this.selectFrom(cls).count();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T singleResult(T entity) {
        return (T) this.selectFrom(entity.getClass()).whereForBean(entity).singleResult(entity.getClass());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T firstResult(T entity) {
        return (T) this.selectFrom(entity.getClass()).whereForBean(entity).firstResult(entity.getClass());
    }

    @Override
    public Object executeInsert(Object entity) {
        return this.insertInto(entity.getClass()).intoForObject(entity).execute();
    }

    @Override
    public Insert insertInto(Class<?> cls) {
        return this.insert().into(cls);
    }

    @Override
    public int executeDelete(Class<?> entityClass, Serializable id) {
        String pkField = this.getDefaultJdbcExecutor().getConfig().getMappingHandler().getPkField(entityClass);
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
        return this.getDefaultJdbcExecutor().createExecutor(Select.class, cls);
    }

    @Override
    public Insert insert() {
        return this.getDefaultJdbcExecutor().createExecutor(Insert.class, null);
    }

    @Override
    public Delete delete() {
        return this.getDefaultJdbcExecutor().createExecutor(Delete.class, null);
    }

    @Override
    public Delete deleteFrom(Class<?> cls) {
        return this.delete().from(cls);
    }

    @Override
    public Update update() {
        return this.getDefaultJdbcExecutor().createExecutor(Update.class, null);
    }

    @Override
    public BatchUpdateExecutor batchUpdate() {
        return this.getDefaultJdbcExecutor().createExecutor(BatchUpdateExecutor.class, null);
    }

    @Override
    public <T> Object executeBatchUpdate(String command, Collection<T> batchData, int batchSize, ParameterizedSetter<T> parameterizedSetter) {
        return this.batchUpdate().execute(command, batchData, batchSize, parameterizedSetter);
    }

    @Override
    public void executeScript(String script) {
        this.nativeExecutor().command(script).forceNative().executeScript();
    }

    @Override
    public NativeExecutor nativeExecutor() {
        return this.getDefaultJdbcExecutor().createExecutor(NativeExecutor.class, null);
    }

    @Override
    public MybatisExecutor myBatisExecutor() {
        return this.getDefaultJdbcExecutor().createExecutor(MybatisExecutor.class, null);
    }

    @Override
    public <T extends CommandExecutor<?>> T executor(Class<T> executor) {
        return this.getDefaultJdbcExecutor().createExecutor(executor, null);
    }

    @Override
    public DataSource getDataSource() {
        if (this.dataSource != null) {
            return this.dataSource;
        }
        return this.getDefaultJdbcExecutor().getDataSource();
    }

    @Override
    public String getDatabaseProduct() {
        return this.getDefaultJdbcExecutor().getDatabaseProduct();
    }

    public JdbcExecutor getDefaultJdbcExecutor() {
        if (this.defaultJdbcExecutor == null) {
            throw new SonsureJdbcException("jdbcExecutor不能为空");
        }
        return this.defaultJdbcExecutor;
    }

}

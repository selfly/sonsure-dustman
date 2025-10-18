/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;

import com.sonsure.dumper.common.utils.ClassUtils;
import com.sonsure.dumper.core.command.*;
import com.sonsure.dumper.core.command.lambda.Function;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

import java.util.Map;

/**
 * The type Abstract condition command executor.
 *
 * @param <T> the type parameter
 * @author liyd
 * @since 17 /4/11
 */
public abstract class AbstractConditionCommandExecutor<T extends ConditionCommandExecutor<T>> extends AbstractCommandExecutor<T> implements ConditionCommandExecutor<T> {

    public AbstractConditionCommandExecutor(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
    }

    @Override
    public T where() {
        this.getExecutableCmdBuilder().where();
        return this.getSelf();
    }

    @Override
    public T where(String field, SqlOperator sqlOperator, Object value) {
        this.getExecutableCmdBuilder().where(field, sqlOperator, value);
        return this.getSelf();
    }

    @Override
    public <E, R> T where(Function<E, R> function, SqlOperator sqlOperator, Object value) {
        this.getExecutableCmdBuilder().where(function, sqlOperator, value);
        return this.getSelf();
    }

    @Override
    public <E, R> T where(Function<E, R> function, Object value) {
        return this.where(function, value == null ? SqlOperator.IS : SqlOperator.EQ, value);
    }

    @Override
    public T where(String field, Object value) {
        this.where(field, value == null ? SqlOperator.IS : SqlOperator.EQ, value);
        return this.getSelf();
    }

    @Override
    public T condition(String field, Object value) {
        return this.condition(field, SqlOperator.EQ, value);
    }

    @Override
    public <T1, R> T condition(Function<T1, R> function, Object value) {
        this.getExecutableCmdBuilder().condition(function, SqlOperator.EQ, value);
        return this.getSelf();
    }

    @Override
    public T condition(String field, SqlOperator sqlOperator, Object value) {
        this.getExecutableCmdBuilder().condition(field, sqlOperator, value);
        return this.getSelf();
    }

    @Override
    public <T1, R> T condition(Function<T1, R> function, SqlOperator sqlOperator, Object value) {
        this.getExecutableCmdBuilder().condition(function, sqlOperator, value);
        return this.getSelf();
    }

    @Override
    public T whereForBean(Object bean) {
        Map<String, Object> propMap = CommandBuildHelper.obj2PropMap(bean, !getExecutableCmdBuilder().isUpdateNull());
        String tableAlias = getExecutableCmdBuilder().resolveTableAlias(bean.getClass().getSimpleName());
        for (Map.Entry<String, Object> entry : propMap.entrySet()) {
            String field = CommandBuildHelper.getTableAliasFieldName(tableAlias, entry.getKey());
            this.where(field, entry.getValue());
        }
        return this.getSelf();
    }

    @Override
    public T whereForBeanPrimaryKey(Object bean) {
        ModelClassWrapper uniqueModelClass = new ModelClassWrapper(bean.getClass());
        ModelClassFieldDetails pkField = uniqueModelClass.getPrimaryKeyField();
        String fieldName = pkField.getFieldName();
        Object value = ClassUtils.getFieldValue(bean, fieldName);
        return this.where(fieldName, value);
    }

    @Override
    public T appendSegment(String segment) {
        this.getExecutableCmdBuilder().appendSegment(segment);
        return getSelf();
    }

    @Override
    public T appendSegment(String segment, Object value) {
        this.getExecutableCmdBuilder().appendSegment(segment, value);
        return getSelf();
    }

    @Override
    public T openParen() {
        this.getExecutableCmdBuilder().openParen();
        return this.getSelf();
    }

    @Override
    public T closeParen() {
        this.getExecutableCmdBuilder().closeParen();
        return this.getSelf();
    }

    @Override
    public T and() {
        this.getExecutableCmdBuilder().and();
        return this.getSelf();
    }

    @Override
    public T and(String field, Object value) {
        return this.and(field, SqlOperator.EQ, value);
    }

    @Override
    public T and(String field, SqlOperator sqlOperator, Object value) {
        this.getExecutableCmdBuilder().and(field, sqlOperator, value);
        return this.getSelf();
    }

    @Override
    public <T1, R> T and(Function<T1, R> function, Object value) {
        this.and(function, SqlOperator.EQ, value);
        return this.getSelf();
    }

    @Override
    public <T1, R> T and(Function<T1, R> function, SqlOperator sqlOperator, Object value) {
        this.getExecutableCmdBuilder().and(function, sqlOperator, value);
        return this.getSelf();
    }

    @Override
    public T or() {
        this.getExecutableCmdBuilder().or();
        return this.getSelf();
    }

    @Override
    public T or(String field, Object value) {
        return this.or(field, SqlOperator.EQ, value);
    }

    @Override
    public T or(String field, SqlOperator sqlOperator, Object value) {
        this.getExecutableCmdBuilder().or(field, sqlOperator, value);
        return this.getSelf();
    }

    @Override
    public <T1, R> T or(Function<T1, R> function, Object value) {
        return this.or(function, SqlOperator.EQ, value);
    }

    @Override
    public <T1, R> T or(Function<T1, R> function, SqlOperator sqlOperator, Object value) {
        this.getExecutableCmdBuilder().or(function, sqlOperator, value);
        return this.getSelf();
    }
}

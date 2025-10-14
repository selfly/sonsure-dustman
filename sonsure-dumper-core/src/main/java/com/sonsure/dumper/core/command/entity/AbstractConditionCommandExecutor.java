/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;

import com.sonsure.dumper.core.command.SqlOperator;
import com.sonsure.dumper.core.command.SqlPart;
import com.sonsure.dumper.core.command.lambda.Function;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

/**
 * The type Abstract condition command executor.
 *
 * @param <T> the type parameter
 * @author liyd
 * @since 17 /4/11
 */
public abstract class AbstractConditionCommandExecutor<T extends ConditionCommandExecutor<T>> extends AbstractEntityCommandExecutor<T> implements ConditionCommandExecutor<T> {

    public AbstractConditionCommandExecutor(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
    }

    @Override
    public T where() {
        this.getEntityCommandDetailsBuilder().where();
        return this.getSelf();
    }

    @Override
    public T where(String field, SqlOperator sqlOperator, Object value) {
        this.getEntityCommandDetailsBuilder().where(field, sqlOperator, value);
        return this.getSelf();
    }

    @Override
    public <E, R> T where(Function<E, R> function, SqlOperator sqlOperator, Object value) {
        this.getEntityCommandDetailsBuilder().where(function, sqlOperator, value);
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
    public T where(SqlPart sqlPart) {
        this.getEntityCommandDetailsBuilder().where(sqlPart);
        return this.getSelf();
    }

    @Override
    public T whereForObject(Object obj) {
        this.getEntityCommandDetailsBuilder().whereForObject(obj);
        return this.getSelf();
    }

    @Override
    public T whereAppend(String segment) {
        this.getEntityCommandDetailsBuilder().whereAppend(segment);
        return getSelf();
    }

    @Override
    public T whereAppend(String segment, Object value) {
        this.getEntityCommandDetailsBuilder().whereAppend(segment, value);
        return getSelf();
    }

    @Override
    public T openParen() {
        this.getEntityCommandDetailsBuilder().openParen();
        return this.getSelf();
    }

    @Override
    public T closeParen() {
        this.getEntityCommandDetailsBuilder().closeParen();
        return this.getSelf();
    }

    @Override
    public T and() {
        this.getEntityCommandDetailsBuilder().and();
        return this.getSelf();
    }

    @Override
    public T or() {
        this.getEntityCommandDetailsBuilder().or();
        return this.getSelf();
    }

}

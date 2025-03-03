/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;

import com.sonsure.dumper.core.command.AbstractCommonCommandExecutor;
import com.sonsure.dumper.core.command.SqlOperator;
import com.sonsure.dumper.core.command.lambda.Function;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

import java.util.Map;

/**
 * The type Abstract condition command executor.
 *
 * @param <T> the type parameter
 * @author liyd
 * @date 17 /4/11
 */
@SuppressWarnings("unchecked")
public abstract class AbstractConditionCommandExecutor<T extends ConditionCommandExecutor<T>> extends AbstractCommonCommandExecutor<T> implements ConditionCommandExecutor<T> {

    public AbstractConditionCommandExecutor(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
    }

    @Override
    public T where(String field, SqlOperator sqlOperator, Object value) {
        this.getCommandDetailsBuilder().where(field, sqlOperator, value);
        return (T) this;
    }

    @Override
    public <E, R> T where(Function<E, R> function, SqlOperator sqlOperator, Object value) {
        this.getCommandDetailsBuilder().where(function, sqlOperator, value);
        return (T) this;
    }

    @Override
    public <E, R> T where(Function<E, R> function, Object value) {
        return this.where(function, value == null ? SqlOperator.IS : SqlOperator.EQ, value);
    }

    @Override
    public T where(String field, Object value) {
        this.where(field, value == null ? SqlOperator.IS : SqlOperator.EQ, value);
        return (T) this;
    }

    @Override
    public T whereForObject(Object obj) {
//        if (!this.getConditionBuilder().isIf()) {
//            iff(true);
//            return (T) this;
//        }
        Map<String, Object> propMap = this.obj2PropMap(obj);
        for (Map.Entry<String, Object> entry : propMap.entrySet()) {
            //忽略掉null
            if (entry.getValue() == null) {
                continue;
            }
            this.getCommandDetailsBuilder().where(entry.getKey(), SqlOperator.EQ, entry.getValue());
        }
        return (T) this;
    }

    @Override
    public T and() {
        this.getCommandDetailsBuilder().and();
        return (T) this;
    }

    @Override
    public T or() {
        this.getCommandDetailsBuilder().or();
        return (T) this;
    }

    @Override
    public T append(String segment, Object... params) {
//        if (this.getConditionBuilder().getCommandContextBuilderContext().isNamedParameter()) {
//            throw new SonsureJdbcException("Named Parameter 方式不能使用数组传参");
//        }
//        this.addWhereField(null, segment, null, params, CommandField.Type.WHERE_APPEND);
        return (T) this;
    }

    @Override
    public T append(String segment, Map<String, Object> params) {
//        this.addWhereField(null, segment, null, params, CommandField.Type.WHERE_APPEND);
        return (T) this;
    }

    @Override
    public T iff(boolean iff) {
//        this.getConditionBuilder().setIf(iff);
        return (T) this;
    }

    @Override
    public T with(boolean with) {
//        if (!with) {
//            this.getConditionBuilder().removeLastWhereFields();
//        }
        return (T) this;
    }

}

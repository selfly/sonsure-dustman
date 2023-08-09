/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;

import com.sonsure.commons.utils.ClassUtils;
import com.sonsure.dumper.core.annotation.Transient;
import com.sonsure.dumper.core.command.AbstractCommonCommandExecutor;
import com.sonsure.dumper.core.command.lambda.Function;
import com.sonsure.dumper.core.command.lambda.LambdaMethod;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import com.sonsure.dumper.core.management.CommandField;

import java.util.ArrayList;
import java.util.List;
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

    private final ConditionCommandBuilderImpl conditionCommandBuilder;

    public AbstractConditionCommandExecutor(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
        this.conditionCommandBuilder = new ConditionCommandBuilderImpl(new ConditionCommandBuilderImpl.Context());
    }

    @Override
    public T where() {
        this.conditionCommandBuilder.addWhereField("where", null, null, null, null);
        return (T) this;
    }

    @Override
    public T where(String field, Object[] value) {
        this.where(field, value == null ? "is" : "=", value);
        return (T) this;
    }

    @Override
    public <E, R> T where(Function<E, R> function, Object value) {
        String field = LambdaMethod.getField(function);
        this.where(field, value == null ? "is" : "=", value);
        return (T) this;
    }

    @Override
    public <E, R> T where(Function<E, R> function, Object[] value) {
        String field = LambdaMethod.getField(function);
        this.where(field, value == null ? "is" : "=", value);
        return (T) this;
    }

    @Override
    public T where(String field, Object value) {
        Object[] values = value == null ? null : (value instanceof Object[] ? (Object[]) value : new Object[]{value});
        this.where(field, value == null ? "is" : "=", values);
        return (T) this;
    }

    @Override
    public T where(String field, String operator, Object... values) {
        this.conditionCommandBuilder.addWhereField("where", field, operator, values, null);
        return (T) this;
    }

    @Override
    public <E, R> T where(Function<E, R> function, String operator, Object... values) {
        String field = LambdaMethod.getField(function);
        this.where(field, operator, values);
        return (T) this;
    }

    @Override
    public T condition(String field, Object value) {
        Object[] values = value instanceof Object[] ? (Object[]) value : new Object[]{value};
        return this.condition(field, value == null ? "is" : "=", values);
    }

    @Override
    public <E, R> T condition(Function<E, R> function, Object value) {
        String field = LambdaMethod.getField(function);
        this.condition(field, value);
        return (T) this;
    }

    @Override
    public T condition(String field, Object[] value) {
        return this.condition(field, value == null ? "is" : "=", value);
    }

    @Override
    public <E, R> T condition(Function<E, R> function, Object[] value) {
        String field = LambdaMethod.getField(function);
        this.condition(field, value);
        return (T) this;
    }

    @Override
    public T condition(String field, String operator, Object... values) {
        this.conditionCommandBuilder.addWhereField(null, field, operator, values, null);
        return (T) this;
    }

    @Override
    public <E, R> T condition(Function<E, R> function, String operator, Object... values) {
        String field = LambdaMethod.getField(function);
        this.condition(field, operator, values);
        return (T) this;
    }

    @Override
    public T conditionEntity(Object entity) {
        return conditionEntity(entity, null, "and");
    }

    @Override
    public T andConditionEntity(Object entity) {
        return conditionEntity(entity, "and", "and");
    }

    @Override
    public T conditionEntity(Object entity, String wholeLogicalOperator, String fieldLogicalOperator) {

        Map<String, Object> beanPropMap = ClassUtils.getSelfBeanPropMap(entity, Transient.class);

        int count = 1;
        List<CommandField> fieldList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : beanPropMap.entrySet()) {
            //忽略掉null
            if (entry.getValue() == null) {
                continue;
            }
            CommandField commandField = this.conditionCommandBuilder.createCommandClassField(entry.getKey(), false, CommandField.Type.ENTITY_FIELD, entity.getClass());
            commandField.setLogicalOperator(count > 1 ? fieldLogicalOperator : null);
            commandField.setFieldOperator("=");
            commandField.setValue(entry.getValue());
            fieldList.add(commandField);
            count++;
        }
        //防止属性全为null的情况
        if (!fieldList.isEmpty()) {
            this.conditionCommandBuilder.addWhereField(wholeLogicalOperator, null, null, null, null);
            this.begin();
            this.conditionCommandBuilder.addWhereFields(fieldList);
            this.end();
        }
        return (T) this;
    }

    @Override
    public T and() {
        this.conditionCommandBuilder.addWhereField("and", null, null, null, null);
        return (T) this;
    }

    @Override
    public T and(String field, Object value) {
        Object[] values = value instanceof Object[] ? (Object[]) value : new Object[]{value};
        this.and(field, value == null ? "is" : "=", values);
        return (T) this;
    }

    @Override
    public <E, R> T and(Function<E, R> function, Object value) {
        String field = LambdaMethod.getField(function);
        this.and(field, value);
        return (T) this;
    }

    @Override
    public T and(String field, Object[] value) {
        this.and(field, value == null ? "is" : "=", value);
        return (T) this;
    }

    @Override
    public <E, R> T and(Function<E, R> function, Object[] value) {
        String field = LambdaMethod.getField(function);
        this.and(field, value);
        return (T) this;
    }

    @Override
    public T and(String field, String operator, Object... values) {
        this.conditionCommandBuilder.addWhereField("and", field, operator, values, null);
        return (T) this;
    }

    @Override
    public <E, R> T and(Function<E, R> function, String operator, Object... values) {
        String field = LambdaMethod.getField(function);
        this.and(field, operator, values);
        return (T) this;
    }

    @Override
    public T or() {
        this.conditionCommandBuilder.addWhereField("or", null, null, null, null);
        return (T) this;
    }

    @Override
    public T or(String field, Object value) {
        Object[] values = value instanceof Object[] ? (Object[]) value : new Object[]{value};
        return this.or(field, value == null ? "is" : "=", values);
    }

    @Override
    public <E, R> T or(Function<E, R> function, Object value) {
        String field = LambdaMethod.getField(function);
        this.or(field, value);
        return (T) this;
    }

    @Override
    public T or(String field, Object[] values) {
        return this.or(field, values == null ? "is" : "=", values);
    }

    @Override
    public <E, R> T or(Function<E, R> function, Object[] value) {
        String field = LambdaMethod.getField(function);
        this.or(field, value);
        return (T) this;
    }

    @Override
    public T or(String field, String operator, Object... values) {
        this.conditionCommandBuilder.addWhereField("or", field, operator, values, null);
        return (T) this;
    }

    @Override
    public <E, R> T or(Function<E, R> function, String operator, Object... values) {
        String field = LambdaMethod.getField(function);
        this.or(field, operator, values);
        return (T) this;
    }

    @Override
    public T begin() {
        this.conditionCommandBuilder.addWhereField("(", null, null, null, null);
        return (T) this;
    }

    @Override
    public T end() {
        this.conditionCommandBuilder.addWhereField(")", null, null, null, null);
        return (T) this;
    }

    @Override
    public T append(String segment, Object... params) {
        if (this.conditionCommandBuilder.getCommandContextBuilderContext().isNamedParameter()) {
            throw new SonsureJdbcException("Named Parameter 方式不能使用数组传参");
        }
        this.conditionCommandBuilder.addWhereField(null, segment, null, params, CommandField.Type.WHERE_APPEND);
        return (T) this;
    }

    @Override
    public T append(String segment, Map<String, Object> params) {
        this.conditionCommandBuilder.addWhereField(null, segment, null, params, CommandField.Type.WHERE_APPEND);
        return (T) this;
    }
}

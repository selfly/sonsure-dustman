/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command;

import com.sonsure.dumper.common.utils.ClassUtils;
import com.sonsure.dumper.core.annotation.Transient;
import com.sonsure.dumper.core.command.lambda.Function;
import com.sonsure.dumper.core.command.lambda.LambdaMethod;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import lombok.Getter;

import java.util.Map;

/**
 * CommandContext构建
 * <p>
 *
 * @param <T> the type parameter
 * @author liyd
 * @date 17 /4/11
 */
@Getter
public abstract class AbstractDynamicCommandDetailsBuilder<T extends DynamicCommandDetailsBuilder<T>> extends AbstractCommandDetailsBuilder<T> implements DynamicCommandDetailsBuilder<T> {

    protected boolean updateNull = false;
    protected boolean clearedSelectColumns = false;

    public AbstractDynamicCommandDetailsBuilder(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
    }

    @Override
    public T from(String entity) {
        this.getCommandSql().FROM(entity);
        return this.getSelf();
    }

    @Override
    public T insertInto(String entity) {
        this.getCommandSql().INSERT_INTO(entity);
        return this.getSelf();
    }

    @Override
    public T update(String entity) {
        this.getCommandSql().UPDATE(entity);
        return this.getSelf();
    }

    @Override
    public T deleteFrom(String entity) {
        this.getCommandSql().DELETE_FROM(entity);
        return this.getSelf();
    }

    @Override
    public T addSelectFields(String... fields) {
        if (!clearedSelectColumns) {
            this.clearedSelectColumns = true;
            this.getCommandSql().clearSelectColumns();
        }
        this.getCommandSql().SELECT(fields);
        return this.getSelf();
    }

    @Override
    public <E, R> T addSelectFields(Function<E, R> function) {
        String[] fields = LambdaMethod.getFields(function);
        return this.addSelectFields(fields);
    }

    @Override
    public <E, R> T dropSelectFields(Function<E, R> function) {
        String[] fields = LambdaMethod.getFields(function);
        return this.dropSelectFields(fields);
    }

    @Override
    public T dropSelectFields(String... fields) {
        this.getCommandSql().dropSelectColumns(fields);
        return this.getSelf();
    }

    @Override
    public T intoField(String field, Object value) {
        this.getCommandSql().INTO_COLUMNS(field).INTO_VALUES(PARAM_PLACEHOLDER);
        this.getParameters().add(value);
        return this.getSelf();
    }

    @Override
    public <E, R> T intoField(Function<E, R> function, Object value) {
        String field = LambdaMethod.getField(function);
        this.intoField(field, value);
        return this.getSelf();
    }

    @Override
    public T intoFieldForObject(Object object) {
        Map<String, Object> propMap = obj2PropMap(object);
        for (Map.Entry<String, Object> entry : propMap.entrySet()) {
            //忽略掉null
            if (entry.getValue() == null) {
                continue;
            }
            this.intoField(entry.getKey(), entry.getValue());
        }
        return this.getSelf();
    }

    @Override
    public T setField(String field, Object value) {
        this.getCommandSql().SET(String.format("%s %s %s", field, SqlOperator.EQ, PARAM_PLACEHOLDER));
        return this.getSelf();
    }

    @Override
    public <E, R> T setField(Function<E, R> function, Object value) {
        String field = LambdaMethod.getField(function);
        return this.setField(field, value);
    }

    @Override
    public T setFieldForObject(Object object) {
        Map<String, Object> propMap = this.obj2PropMap(object);
        for (Map.Entry<String, Object> entry : propMap.entrySet()) {
            //不忽略null，最后构建时根据updateNull设置处理null值
            this.setField(entry.getKey(), entry.getValue());
        }
        return this.getSelf();
    }

    @Override
    public T where(String field, Object value) {
        return this.where(field, SqlOperator.EQ, value);
    }

    @Override
    public T where(String field, SqlOperator sqlOperator, Object value) {
        this.getCommandSql().WHERE(String.format("%s %s %s", field, sqlOperator.getCode(), PARAM_PLACEHOLDER));
        this.getParameters().add(value);
        return this.getSelf();
    }

    @Override
    public <E, R> T where(Function<E, R> function, SqlOperator sqlOperator, Object value) {
        String field = LambdaMethod.getField(function);
        return this.where(field, sqlOperator, value);
    }

    @Override
    public T whereForObject(Object object) {
        Map<String, Object> propMap = this.obj2PropMap(object);
        for (Map.Entry<String, Object> entry : propMap.entrySet()) {
            this.where(entry.getKey(), entry.getValue());
        }
        return this.getSelf();
    }

    @Override
    public T and() {
        this.getCommandSql().AND();
        return this.getSelf();
    }

    @Override
    public T or() {
        this.getCommandSql().OR();
        return this.getSelf();
    }

    @Override
    public T orderBy(String field, OrderBy orderBy) {
        this.getCommandSql().ORDER_BY(String.format("%s %s", field, orderBy.getCode()));
        return this.getSelf();
    }

    @Override
    public <E, R> T orderBy(Function<E, R> function, OrderBy orderBy) {
        String field = LambdaMethod.getField(function);
        return this.orderBy(field, orderBy);
    }

    @Override
    public T groupBy(String... fields) {
        this.getCommandSql().GROUP_BY(fields);
        return this.getSelf();
    }

    @Override
    public <E, R> T groupBy(Function<E, R> function) {
        String field = LambdaMethod.getField(function);
        return this.groupBy(field);
    }

    @Override
    public T updateNull(boolean updateNull) {
        this.updateNull = updateNull;
        return this.getSelf();
    }

    @Override
    public CommandDetails doBuild(JdbcEngineConfig jdbcEngineConfig) {
        CommandDetails commandDetails = new CommandDetails();
        String command = this.getCommandSql().toString();
        commandDetails.setCommand(command);
        commandDetails.setForceNative(this.isForceNative());
        commandDetails.setNamedParameter(false);
        commandDetails.setParameters(this.getParameters());
        commandDetails.setPagination(this.getPagination());
        commandDetails.setDisableCountQuery(this.isDisableCountQuery());

        //commandDetails.setNamedParamNames(false);
//        commandDetails.commandParameters();
//        private Class<?> resultType;
//        private GenerateKey generateKey;
        return commandDetails;
    }

    protected Map<String, Object> obj2PropMap(Object obj) {
        Map<String, Object> propMap;
        if (obj instanceof Map) {
            //noinspection unchecked
            propMap = (Map<String, Object>) obj;
        } else {
            propMap = ClassUtils.getSelfBeanPropMap(obj, Transient.class);
        }
        return propMap;
    }

}

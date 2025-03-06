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
import com.sonsure.dumper.core.management.NativeContentWrapper;
import com.sonsure.dumper.core.third.mybatis.CommandSql;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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

    protected final CommandSql commandSql;
    protected final CommandParameters commandParameters;
    protected boolean ignoreNull = true;
    protected String latestTableAlias;

    public AbstractDynamicCommandDetailsBuilder(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
        this.commandSql = new CommandSql();
        this.commandParameters = new CommandParameters();
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
    public T tableAlias(String aliasName) {
        this.getCommandSql().tableAlias(aliasName);
        this.latestTableAlias = aliasName;
        return this.getSelf();
    }

    @Override
    public T addSelectFields(String... fields) {
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
        NativeContentWrapper nativeContentWrapper = new NativeContentWrapper(field);
        if (nativeContentWrapper.isNatives()) {
            this.getCommandSql().INTO_COLUMNS(nativeContentWrapper.getActualContent()).INTO_VALUES(String.valueOf(value));
        } else {
            this.getCommandSql().INTO_COLUMNS(field).INTO_VALUES(PARAM_PLACEHOLDER);
            this.getCommandParameters().addParameter(field, value);
        }
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
        Map<String, Object> propMap = obj2PropMap(object, this.isIgnoreNull());
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
        NativeContentWrapper nativeContentWrapper = new NativeContentWrapper(field);
        if (nativeContentWrapper.isNatives()) {
            this.getCommandSql().SET(String.format("%s %s %s", nativeContentWrapper.getActualContent(), SqlOperator.EQ.getCode(), value));
        } else {
            this.getCommandSql().SET(String.format("%s %s %s", field, SqlOperator.EQ.getCode(), PARAM_PLACEHOLDER));
            this.getCommandParameters().addParameter(field, value);
        }
        return this.getSelf();
    }

    @Override
    public <E, R> T setField(Function<E, R> function, Object value) {
        String field = LambdaMethod.getField(function);
        return this.setField(field, value);
    }

    @Override
    public T setFieldForObject(Object object) {
        Map<String, Object> propMap = this.obj2PropMap(object, this.isIgnoreNull());
        for (Map.Entry<String, Object> entry : propMap.entrySet()) {
            //不忽略null，最后构建时根据updateNull设置处理null值
            this.setField(entry.getKey(), entry.getValue());
        }
        return this.getSelf();
    }

    @Override
    public T where(String field, SqlOperator sqlOperator, Object value) {
        NativeContentWrapper nativeContentWrapper = new NativeContentWrapper(field);
        if (nativeContentWrapper.isNatives()) {
            this.getCommandSql().WHERE(String.format("%s %s %s", nativeContentWrapper.getActualContent(), sqlOperator.getCode(), value));
        } else {
            if (value != null && value.getClass().isArray()) {
                Object[] valArray = (Object[]) value;
                StringBuilder paramPlaceholder = new StringBuilder("(");
                List<ParameterObject> params = new ArrayList<>(valArray.length);
                int count = 1;
                for (Object val : valArray) {
                    paramPlaceholder.append(PARAM_PLACEHOLDER).append(",");
                    params.add(new ParameterObject(field + (count++), val));
                }
                paramPlaceholder.deleteCharAt(paramPlaceholder.length() - 1);
                paramPlaceholder.append(")");
                this.getCommandSql().WHERE(String.format("%s %s %s", field, sqlOperator.getCode(), paramPlaceholder));
                this.getCommandParameters().addParameters(params);
            } else {
                this.getCommandSql().WHERE(String.format("%s %s %s", field, sqlOperator.getCode(), PARAM_PLACEHOLDER));
                this.getCommandParameters().addParameter(field, value);
            }
        }
        return this.getSelf();
    }

    @Override
    public T where(String field, Object value) {
        return this.where(field, SqlOperator.EQ, value);
    }

    @Override
    public <E, R> T where(Function<E, R> function, SqlOperator sqlOperator, Object value) {
        String field = LambdaMethod.getField(function);
        return this.where(field, sqlOperator, value);
    }

    @Override
    public T whereForObject(Object object) {
        Map<String, Object> propMap = this.obj2PropMap(object, this.isIgnoreNull());
        for (Map.Entry<String, Object> entry : propMap.entrySet()) {
            this.where(entry.getKey(), entry.getValue());
        }
        return this.getSelf();
    }

    @Override
    public T whereAppend(String segment) {
        this.getCommandSql().WHERE(segment);
        return this.getSelf();
    }

    @Override
    public T whereAppend(String segment, Object value) {
        this.getCommandSql().WHERE(segment);
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
    public T ignoreNull(boolean ignoreNull) {
        this.ignoreNull = ignoreNull;
        return this.getSelf();
    }

    protected Map<String, Object> obj2PropMap(Object obj, boolean ignoreNull) {
        //noinspection unchecked
        Map<String, Object> propMap = obj instanceof Map ? (Map<String, Object>) obj : ClassUtils.getSelfBeanPropMap(obj, Transient.class);
        Map<String, Object> resultMap = new LinkedHashMap<>(propMap.size());
        for (Map.Entry<String, Object> entry : propMap.entrySet()) {
            Object value = entry.getValue();
            if (value != null || !ignoreNull) {
                resultMap.put(entry.getKey(), value);
            }
        }
        return resultMap;
    }

}

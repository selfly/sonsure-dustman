/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command;

import com.sonsure.dumper.common.model.Pageable;
import com.sonsure.dumper.common.model.Pagination;
import com.sonsure.dumper.common.utils.ClassUtils;
import com.sonsure.dumper.core.annotation.Transient;
import com.sonsure.dumper.core.command.lambda.Function;
import com.sonsure.dumper.core.command.lambda.LambdaMethod;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import com.sonsure.dumper.core.third.mybatis.CommandSql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CommandContext构建
 * <p>
 *
 * @author liyd
 * @date 17/4/11
 */
public class CommandDetailsBuilderImpl implements CommandDetailsBuilder {

    private static final String PARAM_PLACEHOLDER = " ? ";

    protected final JdbcEngineConfig jdbcEngineConfig;

    private final CommandSql commandSql;

    private final List<Object> parameters;

    private final List<ModelClassDetails> modelClassDetailsList;

    private Pagination pagination;

    private boolean disableCountQuery = false;

    private boolean forceNative = false;

    private boolean updateNull = false;

    private boolean namedParameter = false;

    private boolean clearedSelectColumns = false;

    public CommandDetailsBuilderImpl(JdbcEngineConfig jdbcEngineConfig) {
        this.jdbcEngineConfig = jdbcEngineConfig;
        this.commandSql = new CommandSql();
        this.parameters = new ArrayList<>(16);
        this.modelClassDetailsList = new ArrayList<>(8);
    }

    @Override
    public CommandDetailsBuilder from(Class<?> cls) {
        ModelClassDetails modelClassDetails = this.createModelClassDetails(cls);
        String[] fields = modelClassDetails.getModelFields().stream()
                .map(ModelFieldDetails::getFieldName).toArray(String[]::new);
        this.commandSql.SELECT(fields);
        this.commandSql.FROM(modelClassDetails.getModelName());
        return this;
    }

    @Override
    public CommandDetailsBuilder addSelectFields(String... fields) {
        if (!clearedSelectColumns) {
            this.clearedSelectColumns = true;
            this.commandSql.clearSelectColumns();
        }
        this.commandSql.SELECT(fields);
        return this;
    }

    @Override
    public <E, R> CommandDetailsBuilder addSelectFields(Function<E, R> function) {
        String[] fields = LambdaMethod.getFields(function);
        return this.addSelectFields(fields);
    }

    @Override
    public <E, R> CommandDetailsBuilder dropSelectFields(Function<E, R> function) {
        String[] fields = LambdaMethod.getFields(function);
        return this.dropSelectFields(fields);
    }

    @Override
    public CommandDetailsBuilder dropSelectFields(String... fields) {
        this.commandSql.dropSelectColumns(fields);
        return this;
    }

    @Override
    public CommandDetailsBuilder insertInto(Class<?> cls) {
        ModelClassDetails modelClassDetails = this.createModelClassDetails(cls);
        this.commandSql.INSERT_INTO(modelClassDetails.getModelName());
        return this;
    }

    @Override
    public CommandDetailsBuilder intoField(String field, Object value) {
        this.commandSql.INTO_COLUMNS(field).INTO_VALUES(PARAM_PLACEHOLDER);
        this.parameters.add(value);
        return this;
    }

    @Override
    public <E, R> CommandDetailsBuilder intoField(Function<E, R> function, Object value) {
        String field = LambdaMethod.getField(function);
        this.intoField(field, value);
        return this;
    }

    @Override
    public CommandDetailsBuilder intoFieldForObject(Object object) {
        Map<String, Object> propMap = obj2PropMap(object);
        for (Map.Entry<String, Object> entry : propMap.entrySet()) {
            //忽略掉null
            if (entry.getValue() == null) {
                continue;
            }
            this.intoField(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public CommandDetailsBuilder update(Class<?> cls) {
        ModelClassDetails modelClassDetails = this.createModelClassDetails(cls);
        this.commandSql.UPDATE(modelClassDetails.getModelName());
        return this;
    }

    @Override
    public CommandDetailsBuilder setField(String field, Object value) {
        this.commandSql.SET(String.format("%s %s %s", field, SqlOperator.EQ, PARAM_PLACEHOLDER));
        return this;
    }

    @Override
    public <E, R> CommandDetailsBuilder setField(Function<E, R> function, Object value) {
        String field = LambdaMethod.getField(function);
        return this.setField(field, value);
    }

    @Override
    public CommandDetailsBuilder setFieldForObject(Object object) {
        Map<String, Object> propMap = this.obj2PropMap(object);
        for (Map.Entry<String, Object> entry : propMap.entrySet()) {
            //不忽略null，最后构建时根据updateNull设置处理null值
            this.setField(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public CommandDetailsBuilder setFieldForObjectWherePk(Object object) {
        Map<String, Object> propMap = this.obj2PropMap(object);
        ModelClassDetails uniqueModelClassDetails = this.getUniqueModelClassDetails();
        ModelFieldDetails primaryKeyFiled = uniqueModelClassDetails.getPrimaryKeyFiled();
        //处理主键成where条件
        Object pkValue = propMap.get(primaryKeyFiled.getFieldName());
        if (pkValue == null) {
            throw new SonsureJdbcException("主键属性值不能为空:" + primaryKeyFiled.getFieldName());
        }
        propMap.remove(primaryKeyFiled.getFieldName());
        for (Map.Entry<String, Object> entry : propMap.entrySet()) {
            //不忽略null，最后构建时根据updateNull设置处理null值
            this.setField(entry.getKey(), entry.getValue());
        }
        this.where(primaryKeyFiled.getFieldName(), pkValue);
        return this;
    }

    @Override
    public CommandDetailsBuilder deleteFrom(Class<?> cls) {
        ModelClassDetails modelClassDetails = this.createModelClassDetails(cls);
        this.commandSql.DELETE_FROM(modelClassDetails.getModelName());
        return this;
    }

    @Override
    public CommandDetailsBuilder where(String field, Object value) {
        return this.where(field, SqlOperator.EQ, value);
    }

    @Override
    public CommandDetailsBuilder where(String field, SqlOperator sqlOperator, Object value) {
        this.commandSql.WHERE(String.format("%s %s %s", field, sqlOperator.getCode(), PARAM_PLACEHOLDER));
        this.parameters.add(value);
        return this;
    }

    @Override
    public <E, R> CommandDetailsBuilder where(Function<E, R> function, SqlOperator sqlOperator, Object value) {
        String field = LambdaMethod.getField(function);
        return this.where(field, sqlOperator, value);
    }

    @Override
    public CommandDetailsBuilder and() {
        this.commandSql.AND();
        return this;
    }

    @Override
    public CommandDetailsBuilder or() {
        this.commandSql.OR();
        return this;
    }

    @Override
    public CommandDetailsBuilder orderBy(String field, OrderBy orderBy) {
        this.commandSql.ORDER_BY(String.format("%s %s", field, orderBy.getCode()));
        return this;
    }

    @Override
    public <E, R> CommandDetailsBuilder orderBy(Function<E, R> function, OrderBy orderBy) {
        String field = LambdaMethod.getField(function);
        return this.orderBy(field, orderBy);
    }

    @Override
    public CommandDetailsBuilder groupBy(String... fields) {
        this.commandSql.GROUP_BY(fields);
        return this;
    }

    @Override
    public <E, R> CommandDetailsBuilder groupBy(Function<E, R> function) {
        String field = LambdaMethod.getField(function);
        return this.groupBy(field);
    }

    @Override
    public CommandDetailsBuilder updateNull() {
        this.updateNull = true;
        return this;
    }

    @Override
    public CommandDetailsBuilder forceNative() {
        this.forceNative = true;
        return this;
    }

    @Override
    public CommandDetailsBuilder namedParameter() {
        this.namedParameter = true;
        return this;
    }

    @Override
    public CommandDetailsBuilder paginate(int pageNum, int pageSize) {
        this.pagination = new Pagination();
        pagination.setPageSize(pageSize);
        pagination.setPageNum(pageNum);
        return this;
    }

    @Override
    public CommandDetailsBuilder paginate(Pageable pageable) {
        this.paginate(pageable.getPageNum(), pageable.getPageSize());
        return this;
    }

    @Override
    public CommandDetailsBuilder limit(int offset, int size) {
        this.pagination = new Pagination();
        pagination.setPageSize(size);
        pagination.setOffset(offset);
        return this;
    }

    @Override
    public CommandDetailsBuilder disableCountQuery() {
        this.disableCountQuery = true;
        return this;
    }

    @Override
    public CommandDetails build(JdbcEngineConfig jdbcEngineConfig) {

        CommandDetails commandDetails = new CommandDetails();
        String command = this.commandSql.toString();

        commandDetails.setCommand(command);
        commandDetails.setForceNative(this.forceNative);
        commandDetails.setNamedParameter(this.namedParameter);
//        commandDetails.setNamedParamNames(this.);
//        commandDetails.commandParameters();
        commandDetails.setParameters(this.parameters);
        commandDetails.setPagination(this.pagination);
        commandDetails.setDisableCountQuery(this.disableCountQuery);

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

    private ModelClassDetails createModelClassDetails(Class<?> cls) {
        EntityClassDetailsImpl classDetails = new EntityClassDetailsImpl(cls, this.jdbcEngineConfig.getMappingHandler());
        this.modelClassDetailsList.add(classDetails);
        return classDetails;
    }

    public ModelClassDetails getUniqueModelClassDetails() {
        if (this.modelClassDetailsList.size() != 1) {
            throw new SonsureJdbcException("当前执行业务必须且只能有一个Model Class");
        }
        return this.modelClassDetailsList.iterator().next();
    }
}

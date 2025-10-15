/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command;

import com.sonsure.dumper.common.model.MultiTuple;
import com.sonsure.dumper.common.utils.StrUtils;
import com.sonsure.dumper.common.utils.UUIDUtils;
import com.sonsure.dumper.core.command.lambda.Function;
import com.sonsure.dumper.core.command.lambda.LambdaField;
import com.sonsure.dumper.core.command.lambda.LambdaHelper;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import com.sonsure.dumper.core.command.build.SimpleSQL;
import com.sonsure.dumper.core.command.build.SqlStatementType;
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
 * @since 17 /4/11
 */
@Getter
public abstract class AbstractDynamicCommandDetailsBuilder<T extends DynamicCommandDetailsBuilder<T>> extends AbstractCommandDetailsBuilder<T> implements DynamicCommandDetailsBuilder<T> {

    protected final SimpleSQL simpleSQL;
    protected final CommandParameters commandParameters;
    protected boolean ignoreNull = true;
    protected Map<String, String> tableAliasMapping;
    protected String latestTable;
    protected String latestTableAlias;
    protected SqlStatementType latestStatement;

    public AbstractDynamicCommandDetailsBuilder() {
        this.simpleSQL = new SimpleSQL();
        this.commandParameters = new CommandParameters();
        this.tableAliasMapping = new LinkedHashMap<>(8);
    }

    @Override
    public T from(String entity) {
        this.getSimpleSQL().from(entity);
        this.latestTable = entity;
        latestStatement = SqlStatementType.TABLE;
        return this.getSelf();
    }

    @Override
    public T insertInto(String entity) {
        this.getSimpleSQL().insertInto(entity);
        this.latestTable = entity;
        latestStatement = SqlStatementType.TABLE;
        return this.getSelf();
    }

    @Override
    public T update(String entity) {
        this.getSimpleSQL().update(entity);
        this.latestTable = entity;
        latestStatement = SqlStatementType.TABLE;
        return this.getSelf();
    }

    @Override
    public T deleteFrom(String entity) {
        this.getSimpleSQL().deleteFrom(entity);
        this.latestTable = entity;
        latestStatement = SqlStatementType.TABLE;
        return this.getSelf();
    }


    @Override
    public T as(String aliasName) {
        this.getSimpleSQL().as(aliasName, this.getLatestStatement());
        this.latestTableAlias = aliasName;
        this.tableAliasMapping.put(this.latestTable, aliasName);
        return this.getSelf();
    }

    @Override
    public T innerJoin(String table) {
        this.getSimpleSQL().innerJoin(table);
        this.latestTable = table;
        latestStatement = SqlStatementType.INNER_JOIN;
        return this.getSelf();
    }

    @Override
    public T on(String on) {
        this.getSimpleSQL().joinStepOn(on, getLatestStatement());
        return this.getSelf();
    }

    @Override
    public <E1, R1, E2, R2> T on(Function<E1, R1> table1Field, SqlOperator sqlOperator, Function<E2, R2> table2Field) {
        LambdaField lambdaField1 = LambdaHelper.getLambdaClass(table1Field);
        String tableAlias1 = this.getTableAliasMapping().get(lambdaField1.getSimpleClassName());
        String field1 = CommandBuildHelper.getTableAliasFieldName(tableAlias1, lambdaField1.getFieldName());

        LambdaField lambdaField2 = LambdaHelper.getLambdaClass(table2Field);
        String tableAlias2 = this.getTableAliasMapping().get(lambdaField2.getSimpleClassName());
        String field2 = CommandBuildHelper.getTableAliasFieldName(tableAlias2, lambdaField2.getFieldName());
        this.getSimpleSQL().joinStepOn(String.format("%s %s %s", field1, sqlOperator.getCode(), field2), getLatestStatement());
        return this.getSelf();
    }

    @Override
    public T on(SqlPart sqlPart) {
        List<SqlPart.PartStatement> partStatements = sqlPart.getPartStatements();
        StringBuilder partSql = new StringBuilder();
        CommandParameters partParameters = new CommandParameters();
        for (SqlPart.PartStatement partStatement : partStatements) {
            if (StrUtils.isNotBlank(partStatement.getLogical())) {
                partSql.append(partStatement.getLogical());
            }
            MultiTuple<String, CommandParameters> pair = this.buildPartStatement(partStatement);
            partSql.append(pair.getLeft());
            partParameters.addParameters(pair.getRight().getParameterObjects());
        }
        this.getSimpleSQL().joinStepOn(partSql.toString(), getLatestStatement());
        this.commandParameters.addParameters(partParameters.getParameterObjects());
        return this.getSelf();
    }

    @Override
    public T addSelectFields(String... fields) {
        this.getSimpleSQL().select(fields);
        return this.getSelf();
    }

    @Override
    public T addAliasSelectFields(String tableAlias, String... fields) {
        for (String field : fields) {
            String aliasField = CommandBuildHelper.getTableAliasFieldName(tableAlias, field);
            this.addSelectFields(aliasField);
        }
        return this.getSelf();
    }

    @Override
    public <E, R> T addSelectFields(Function<E, R> function) {
        LambdaField lambdaField = LambdaHelper.getLambdaClass(function);
        String tableAlias = this.getTableAliasMapping().get(lambdaField.getSimpleClassName());
        String field = CommandBuildHelper.getTableAliasFieldName(tableAlias, lambdaField.getFieldName());
        return this.addSelectFields(field);
    }

    @Override
    public <E, R> T dropSelectFields(Function<E, R> function) {
        LambdaField lambdaField = LambdaHelper.getLambdaClass(function);
        String tableAlias = this.getTableAliasMapping().get(lambdaField.getSimpleClassName());
        String field = CommandBuildHelper.getTableAliasFieldName(tableAlias, lambdaField.getFieldName());
        return this.dropSelectFields(field);
    }

    @Override
    public T dropSelectFields(String... fields) {
        this.getSimpleSQL().dropSelectColumns(fields);
        return this.getSelf();
    }

    @Override
    public T intoField(String field, Object value) {
        NativeContentWrapper nativeContentWrapper = new NativeContentWrapper(field);
        if (nativeContentWrapper.isNatives()) {
            this.getSimpleSQL().intoColumns(nativeContentWrapper.getActualContent()).intoValues(String.valueOf(value));
        } else {
            this.getSimpleSQL().intoColumns(field).intoValues(PARAM_PLACEHOLDER);
            this.getCommandParameters().addParameter(field, value);
        }
        return this.getSelf();
    }

    @Override
    public <E, R> T intoField(Function<E, R> function, Object value) {
        String field = LambdaHelper.getFieldName(function);
        this.intoField(field, value);
        return this.getSelf();
    }

    @Override
    public T intoFieldForObject(Object object) {
        Map<String, Object> propMap = CommandBuildHelper.obj2PropMap(object, this.isIgnoreNull());
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
            this.getSimpleSQL().set(String.format("%s %s %s", nativeContentWrapper.getActualContent(), SqlOperator.EQ.getCode(), value));
        } else {
            this.getSimpleSQL().set(String.format("%s %s %s", field, SqlOperator.EQ.getCode(), PARAM_PLACEHOLDER));
            this.getCommandParameters().addParameter(field, value);
        }
        return this.getSelf();
    }

    @Override
    public <E, R> T setField(Function<E, R> function, Object value) {
        String field = LambdaHelper.getFieldName(function);
        return this.setField(field, value);
    }

    @Override
    public T setFieldForObject(Object object) {
        Map<String, Object> propMap = CommandBuildHelper.obj2PropMap(object, this.isIgnoreNull());
        for (Map.Entry<String, Object> entry : propMap.entrySet()) {
            //不忽略null，最后构建时根据updateNull设置处理null值
            this.setField(entry.getKey(), entry.getValue());
        }
        return this.getSelf();
    }

    @Override
    public T where() {
        this.getSimpleSQL().where();
        return this.getSelf();
    }

    @Override
    public T where(String field, SqlOperator sqlOperator, Object value) {
        MultiTuple<String, CommandParameters> pair = this.buildPartStatement(field, sqlOperator, value);
        this.getSimpleSQL().where(pair.getLeft());
        this.getCommandParameters().addParameters(pair.getRight().getParameterObjects());
        return this.getSelf();
    }

    @Override
    public T where(String field, Object value) {
        return this.where(field, SqlOperator.EQ, value);
    }

    @Override
    public <E, R> T where(Function<E, R> function, SqlOperator sqlOperator, Object value) {
        LambdaField lambdaField = LambdaHelper.getLambdaClass(function);
        String tableAlias = this.getTableAliasMapping().get(lambdaField.getSimpleClassName());
        String field = CommandBuildHelper.getTableAliasFieldName(tableAlias, lambdaField.getFieldName());
        return this.where(field, sqlOperator, value);
    }

    @Override
    public T where(SqlPart sqlPart) {
        List<SqlPart.PartStatement> partStatements = sqlPart.getPartStatements();
        StringBuilder partSql = new StringBuilder("(");
        CommandParameters partParameters = new CommandParameters();
        for (SqlPart.PartStatement partStatement : partStatements) {
            if (StrUtils.isNotBlank(partStatement.getLogical())) {
                partSql.append(partStatement.getLogical());
            }
            MultiTuple<String, CommandParameters> pair = this.buildPartStatement(partStatement);
            partSql.append(pair.getLeft());
            partParameters.addParameters(pair.getRight().getParameterObjects());
        }
        partSql.append(")");
        this.getSimpleSQL().where(partSql.toString());
        this.commandParameters.addParameters(partParameters.getParameterObjects());
        return this.getSelf();
    }

    protected MultiTuple<String, CommandParameters> buildPartStatement(String field, SqlOperator sqlOperator, Object value) {
        StringBuilder conditionSql = new StringBuilder();
        CommandParameters conditionParameters = new CommandParameters();
        NativeContentWrapper nativeContentWrapper = new NativeContentWrapper(field);

        if (value == null) {
            conditionSql.append(field).append(SPACE)
                    .append(sqlOperator.getCode()).append(SPACE).append(NULL);
        } else if (nativeContentWrapper.isNatives()) {
            conditionSql.append(nativeContentWrapper.getActualContent()).append(SPACE)
                    .append(sqlOperator.getCode()).append(SPACE)
                    .append(value);
        } else {
            if (this.isNamedParameter()) {
                conditionSql.append(field).append(SPACE)
                        .append(sqlOperator.getCode()).append(SPACE)
                        .append(COLON).append(field);
                conditionParameters.addParameter(field, value);
            } else {
                if (value.getClass().isArray()) {
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
                    conditionSql.append(field).append(SPACE)
                            .append(sqlOperator.getCode()).append(SPACE)
                            .append(paramPlaceholder);
                    conditionParameters.addParameters(params);
                } else {
                    conditionSql.append(field).append(SPACE)
                            .append(sqlOperator.getCode()).append(SPACE)
                            .append(PARAM_PLACEHOLDER);
                    conditionParameters.addParameter(field, value);
                }
            }
        }
        return new MultiTuple<>(conditionSql.toString(), conditionParameters);
    }

    protected MultiTuple<String, CommandParameters> buildPartStatement(SqlPart.PartStatement partStatement) {
        String field;
        SqlOperator sqlOperator = partStatement.getSqlOperator();
        Object value;
        if (partStatement.getSource() instanceof LambdaField) {
            LambdaField lambdaField = (LambdaField) partStatement.getSource();
            String tableAlias = this.getTableAliasMapping().get(lambdaField.getSimpleClassName());
            field = CommandBuildHelper.getTableAliasFieldName(tableAlias, lambdaField.getFieldName());
        } else {
            field = ((String) partStatement.getSource());
        }
        if (partStatement.getTarget() instanceof LambdaField) {
            LambdaField lambdaField = (LambdaField) partStatement.getTarget();
            String tableAlias = this.getTableAliasMapping().get(lambdaField.getSimpleClassName());
            value = CommandBuildHelper.getTableAliasFieldName(tableAlias, lambdaField.getFieldName());
        } else {
            value = partStatement.getTarget();
        }
        if (partStatement.isRaw()) {
            field = CommandBuildHelper.wrapperToNative(field);
        }
        return this.buildPartStatement(field, sqlOperator, value);
    }

    @Override
    public T whereForObject(Object object) {
        Map<String, Object> propMap = CommandBuildHelper.obj2PropMap(object, this.isIgnoreNull());
        for (Map.Entry<String, Object> entry : propMap.entrySet()) {
            this.where(entry.getKey(), entry.getValue());
        }
        return this.getSelf();
    }

    @Override
    public T whereAppend(String segment) {
        this.getSimpleSQL().where(segment);
        return this.getSelf();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T whereAppend(String segment, Object value) {
        this.getSimpleSQL().where(segment);
        if (this.isNamedParameter()) {
            if (!(value instanceof Map)) {
                throw new SonsureJdbcException("namedParameter模式参数必须为Map类型,key与name对应");
            }
            //noinspection unchecked
            this.getCommandParameters().addParameters((Map<String, Object>) value);
        } else {
            if (value.getClass().isArray()) {
                Object[] valArray = (Object[]) value;
                for (Object val : valArray) {
                    //这里的参数名用不到，随机生成
                    this.getCommandParameters().addParameter(UUIDUtils.getUUID8(), val);
                }
            } else {
                this.getCommandParameters().addParameter(UUIDUtils.getUUID8(), value);
            }
        }

        return this.getSelf();
    }

    @Override
    public T openParen() {
        this.getSimpleSQL().openParen();
        return this.getSelf();
    }

    @Override
    public T closeParen() {
        this.getSimpleSQL().closeParen();
        return this.getSelf();
    }

    @Override
    public T and() {
        this.getSimpleSQL().and();
        return this.getSelf();
    }

    @Override
    public T or() {
        this.getSimpleSQL().or();
        return this.getSelf();
    }

    @Override
    public T orderBy(String field, OrderBy orderBy) {
        this.getSimpleSQL().orderBy(String.format("%s %s", field, orderBy.getCode()));
        return this.getSelf();
    }

    @Override
    public <E, R> T orderBy(Function<E, R> function, OrderBy orderBy) {
        LambdaField lambdaField = LambdaHelper.getLambdaClass(function);
        String tableAlias = this.getTableAliasMapping().get(lambdaField.getSimpleClassName());
        String field = CommandBuildHelper.getTableAliasFieldName(tableAlias, lambdaField.getFieldName());
        return this.orderBy(field, orderBy);
    }

    @Override
    public T groupBy(String... fields) {
        this.getSimpleSQL().groupBy(fields);
        return this.getSelf();
    }

    @Override
    public <E, R> T groupBy(Function<E, R> function) {
        String field = LambdaHelper.getFieldName(function);
        return this.groupBy(field);
    }

    @Override
    public T ignoreNull(boolean ignoreNull) {
        this.ignoreNull = ignoreNull;
        return this.getSelf();
    }

}

/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command;

import com.sonsure.dumper.core.command.build.ExecutableCmdBuilder;
import com.sonsure.dumper.core.command.build.ExecutableCmdBuilderImpl;
import com.sonsure.dumper.core.command.lambda.Function;
import com.sonsure.dumper.core.command.lambda.LambdaHelper;
import lombok.Getter;

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

    protected final ExecutableCmdBuilder executableCmdBuilder;
    protected boolean ignoreNull = true;

    public AbstractDynamicCommandDetailsBuilder() {
        this.executableCmdBuilder = new ExecutableCmdBuilderImpl();
    }

    @Override
    public T from(String entity) {
        this.executableCmdBuilder.from(entity);
        return this.getSelf();
    }

    @Override
    public T insertInto(String entity) {
        this.executableCmdBuilder.insertInto(entity);
        return this.getSelf();
    }

    @Override
    public T update(String entity) {
        this.executableCmdBuilder.update(entity);
        return this.getSelf();
    }

    @Override
    public T deleteFrom(String entity) {
        this.executableCmdBuilder.deleteFrom(entity);
        return this.getSelf();
    }


    @Override
    public T as(String aliasName) {
        this.executableCmdBuilder.as(aliasName);
        return this.getSelf();
    }

    @Override
    public T innerJoin(String table) {
        this.executableCmdBuilder.innerJoin(table);
        return this.getSelf();
    }

    @Override
    public T on(String on) {
        this.executableCmdBuilder.joinStepOn(on);
        return this.getSelf();
    }

    @Override
    public <E1, R1, E2, R2> T on(Function<E1, R1> table1Field, Function<E2, R2> table2Field) {
        this.executableCmdBuilder.joinStepOn(table1Field, table2Field);
        return this.getSelf();
    }

//    @Override
//    public T on(SqlPart sqlPart) {
//        List<SqlPart.PartStatement> partStatements = sqlPart.getPartStatements();
//        StringBuilder partSql = new StringBuilder();
//        CommandParameters partParameters = new CommandParameters();
//        for (SqlPart.PartStatement partStatement : partStatements) {
//            if (StrUtils.isNotBlank(partStatement.getLogical())) {
//                partSql.append(partStatement.getLogical());
//            }
//            MultiTuple<String, CommandParameters> pair = this.buildPartStatement(partStatement);
//            partSql.append(pair.getLeft());
//            partParameters.addParameters(pair.getRight().getParameterObjects());
//        }
//        this.getSimpleSQL().joinStepOn(partSql.toString());
//        this.commandParameters.addParameters(partParameters.getParameterObjects());
//        return this.getSelf();
//    }

    @Override
    public T addSelectFields(String... fields) {
        this.executableCmdBuilder.select(fields);
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
        this.executableCmdBuilder.select(function);
        return this.getSelf();
    }

    @Override
    public <E, R> T dropSelectFields(Function<E, R> function) {
        this.executableCmdBuilder.dropSelectColumn(function);
        return this.getSelf();
    }

    @Override
    public T dropSelectFields(String... fields) {
        this.executableCmdBuilder.dropSelectColumn(fields);
        return this.getSelf();
    }

    @Override
    public T intoField(String field, Object value) {
        NativeContentWrapper nativeContentWrapper = new NativeContentWrapper(field);
        if (nativeContentWrapper.isNatives()) {
            this.executableCmdBuilder.intoColumns(nativeContentWrapper.getActualContent()).intoValues(String.valueOf(value));
        } else {
            this.executableCmdBuilder.intoColumns(field).intoValues(PARAM_PLACEHOLDER);
            this.executableCmdBuilder.addParameter(field, value);
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
            this.executableCmdBuilder.set(String.format("%s %s %s", nativeContentWrapper.getActualContent(), SqlOperator.EQ.getCode(), value));
        } else {
            this.executableCmdBuilder.set(String.format("%s %s %s", field, SqlOperator.EQ.getCode(), PARAM_PLACEHOLDER));
            this.executableCmdBuilder.addParameter(field, value);
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
        this.executableCmdBuilder.where();
        return this.getSelf();
    }

    @Override
    public T where(String field, SqlOperator sqlOperator, Object value) {
        this.executableCmdBuilder.where(field, sqlOperator, value);
        return this.getSelf();
    }

    @Override
    public T where(String field, Object value) {
        return this.where(field, SqlOperator.EQ, value);
    }

    @Override
    public <E, R> T where(Function<E, R> function, SqlOperator sqlOperator, Object value) {
        this.executableCmdBuilder.where(function, sqlOperator, value);
        return this.getSelf();
    }

//    @Override
//    public T where(SqlPart sqlPart) {
//        List<SqlPart.PartStatement> partStatements = sqlPart.getPartStatements();
//        StringBuilder partSql = new StringBuilder("(");
//        CommandParameters partParameters = new CommandParameters();
//        for (SqlPart.PartStatement partStatement : partStatements) {
//            if (StrUtils.isNotBlank(partStatement.getLogical())) {
//                partSql.append(partStatement.getLogical());
//            }
//            MultiTuple<String, CommandParameters> pair = this.buildPartStatement(partStatement);
//            partSql.append(pair.getLeft());
//            partParameters.addParameters(pair.getRight().getParameterObjects());
//        }
//        partSql.append(")");
//        this.getSimpleSQL().where(partSql.toString());
//        this.commandParameters.addParameters(partParameters.getParameterObjects());
//        return this.getSelf();
//    }

//    protected MultiTuple<String, List<SqlParameter>> buildPartStatement(String field, SqlOperator sqlOperator, Object value) {
//        StringBuilder conditionSql = new StringBuilder();
//        List<SqlParameter> conditionParameters = new ArrayList<>(16);
//        NativeContentWrapper nativeContentWrapper = new NativeContentWrapper(field);
//
//        if (value == null) {
//            conditionSql.append(field).append(SPACE)
//                    .append(sqlOperator.getCode()).append(SPACE).append(NULL);
//        } else if (nativeContentWrapper.isNatives()) {
//            conditionSql.append(nativeContentWrapper.getActualContent()).append(SPACE)
//                    .append(sqlOperator.getCode()).append(SPACE)
//                    .append(value);
//        } else {
//            if (this.isNamedParameter()) {
//                conditionSql.append(field).append(SPACE)
//                        .append(sqlOperator.getCode()).append(SPACE)
//                        .append(COLON).append(field);
//                conditionParameters.add(new SqlParameter(field, value));
//            } else {
//                if (value.getClass().isArray()) {
//                    Object[] valArray = (Object[]) value;
//                    StringBuilder paramPlaceholder = new StringBuilder("(");
//                    List<SqlParameter> params = new ArrayList<>(valArray.length);
//                    int count = 1;
//                    for (Object val : valArray) {
//                        paramPlaceholder.append(PARAM_PLACEHOLDER).append(",");
//                        params.add(new SqlParameter(field + (count++), val));
//                    }
//                    paramPlaceholder.deleteCharAt(paramPlaceholder.length() - 1);
//                    paramPlaceholder.append(")");
//                    conditionSql.append(field).append(SPACE)
//                            .append(sqlOperator.getCode()).append(SPACE)
//                            .append(paramPlaceholder);
//                    conditionParameters.addAll(params);
//                } else {
//                    conditionSql.append(field).append(SPACE)
//                            .append(sqlOperator.getCode()).append(SPACE)
//                            .append(PARAM_PLACEHOLDER);
//                    conditionParameters.add(new SqlParameter(field, value));
//                }
//            }
//        }
//        return new MultiTuple<>(conditionSql.toString(), conditionParameters);
//    }

//    protected MultiTuple<String, CommandParameters> buildPartStatement(SqlPart.PartStatement partStatement) {
//        String field;
//        SqlOperator sqlOperator = partStatement.getSqlOperator();
//        Object value;
//        if (partStatement.getSource() instanceof LambdaField) {
//            LambdaField lambdaField = (LambdaField) partStatement.getSource();
//            String tableAlias = this.getTableAliasMapping().get(lambdaField.getSimpleClassName());
//            field = CommandBuildHelper.getTableAliasFieldName(tableAlias, lambdaField.getFieldName());
//        } else {
//            field = ((String) partStatement.getSource());
//        }
//        if (partStatement.getTarget() instanceof LambdaField) {
//            LambdaField lambdaField = (LambdaField) partStatement.getTarget();
//            String tableAlias = this.getTableAliasMapping().get(lambdaField.getSimpleClassName());
//            value = CommandBuildHelper.getTableAliasFieldName(tableAlias, lambdaField.getFieldName());
//        } else {
//            value = partStatement.getTarget();
//        }
//        if (partStatement.isRaw()) {
//            field = CommandBuildHelper.wrapperToNative(field);
//        }
//        return this.buildPartStatement(field, sqlOperator, value);
//    }

    @Override
    public T whereForObject(Object object) {
        //todo alias处理
        Map<String, Object> propMap = CommandBuildHelper.obj2PropMap(object, this.isIgnoreNull());
        for (Map.Entry<String, Object> entry : propMap.entrySet()) {
            this.where(entry.getKey(), entry.getValue());
        }
        return this.getSelf();
    }

    @Override
    public T whereAppend(String segment) {
        this.executableCmdBuilder.where(segment);
        return this.getSelf();
    }

    @Override
    public T whereAppend(String segment, Object params) {
        this.executableCmdBuilder.where(segment, params);
        return this.getSelf();
    }

    @Override
    public T openParen() {
        this.executableCmdBuilder.openParen();
        return this.getSelf();
    }

    @Override
    public T closeParen() {
        this.executableCmdBuilder.closeParen();
        return this.getSelf();
    }

    @Override
    public T and() {
        this.executableCmdBuilder.and();
        return this.getSelf();
    }

    @Override
    public T or() {
        this.executableCmdBuilder.or();
        return this.getSelf();
    }

    @Override
    public T orderBy(String field, OrderBy orderBy) {
        this.executableCmdBuilder.orderBy(field, orderBy);
        return this.getSelf();
    }

    @Override
    public <E, R> T orderBy(Function<E, R> function, OrderBy orderBy) {
        this.executableCmdBuilder.orderBy(function, orderBy);
        return this.getSelf();
    }

    @Override
    public T groupBy(String... fields) {
        this.executableCmdBuilder.groupBy(fields);
        return this.getSelf();
    }

    @Override
    public <E, R> T groupBy(Function<E, R> function) {
        this.executableCmdBuilder.groupBy(function);
        return this.getSelf();
    }

    @Override
    public T ignoreNull(boolean ignoreNull) {
        this.ignoreNull = ignoreNull;
        return this.getSelf();
    }

}

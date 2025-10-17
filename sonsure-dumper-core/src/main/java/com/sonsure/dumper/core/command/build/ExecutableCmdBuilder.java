package com.sonsure.dumper.core.command.build;

import com.sonsure.dumper.core.command.ExecutionType;
import com.sonsure.dumper.core.command.GenerateKey;
import com.sonsure.dumper.core.command.OrderBy;
import com.sonsure.dumper.core.command.SqlOperator;
import com.sonsure.dumper.core.command.lambda.Function;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

import java.util.List;
import java.util.Map;

/**
 * @author liyd
 */
public interface ExecutableCmdBuilder {

    ExecutableCmdBuilder jdbcEngineConfig(JdbcEngineConfig jdbcEngineConfig);

    ExecutableCmdBuilder addCustomizer(ExecutableCustomizer customizer);

    ExecutableCmdBuilder command(String command);

    ExecutableCmdBuilder executionType(ExecutionType executionType);

    ExecutableCmdBuilder resultType(Class<?> resultType);

    ExecutableCmdBuilder insertInto(String table);

    ExecutableCmdBuilder intoColumns(String... columns);

    ExecutableCmdBuilder intoValues(Object... values);

    ExecutableCmdBuilder select(String... columns);

    <E, R> ExecutableCmdBuilder select(Function<E, R> function);

    ExecutableCmdBuilder dropSelectColumn(String... columns);

    <E, R> ExecutableCmdBuilder dropSelectColumn(Function<E, R> function);

    ExecutableCmdBuilder selectDistinct(String... columns);

    ExecutableCmdBuilder from(String table);

    ExecutableCmdBuilder join(String table);

    ExecutableCmdBuilder innerJoin(String table);

    ExecutableCmdBuilder outerJoin(String table);

    ExecutableCmdBuilder leftOuterJoin(String table);

    ExecutableCmdBuilder rightOuterJoin(String table);

    <E1, R1, E2, R2> ExecutableCmdBuilder joinStepOn(Function<E1, R1> table1Field, Function<E2, R2> table2Field);

    ExecutableCmdBuilder joinStepOn(String on);

    ExecutableCmdBuilder update(String table);

    ExecutableCmdBuilder set(String field, Object value);

    <E, R> ExecutableCmdBuilder set(Function<E, R> function, Object value);

    ExecutableCmdBuilder deleteFrom(String table);

    ExecutableCmdBuilder as(String alias);

    ExecutableCmdBuilder namedParameter();

    ExecutableCmdBuilder namedParameter(boolean namedParameter);

    ExecutableCmdBuilder addParameter(String name, Object value);

    ExecutableCmdBuilder addParameters(List<SqlParameter> parameters);

    ExecutableCmdBuilder addParameters(Map<String, ?> parameters);

    ExecutableCmdBuilder addParameters(Object... values);

    ExecutableCmdBuilder where();

    ExecutableCmdBuilder where(String column, SqlOperator sqlOperator, Object value);

    <E, R> ExecutableCmdBuilder where(Function<E, R> function, SqlOperator sqlOperator, Object value);

//    ExecutableCmdBuilder condition(String condition);
//
//    ExecutableCmdBuilder condition(String condition, Object params);

    ExecutableCmdBuilder condition(String column, SqlOperator sqlOperator, Object value);

    <E, R> ExecutableCmdBuilder condition(Function<E, R> function, SqlOperator sqlOperator, Object value);

    ExecutableCmdBuilder or();

//    ExecutableCmdBuilder or(String condition);
//
//    ExecutableCmdBuilder or(String condition, Object params);

    ExecutableCmdBuilder or(String column, SqlOperator sqlOperator, Object value);

    <E, R> ExecutableCmdBuilder or(Function<E, R> function, SqlOperator sqlOperator, Object value);

    ExecutableCmdBuilder and();

//    ExecutableCmdBuilder and(String condition);
//
//    ExecutableCmdBuilder and(String condition, Object params);

    ExecutableCmdBuilder and(String column, SqlOperator sqlOperator, Object value);

    <E, R> ExecutableCmdBuilder and(Function<E, R> function, SqlOperator sqlOperator, Object value);

    ExecutableCmdBuilder appendSegment(String segment);

    ExecutableCmdBuilder appendSegment(String segment, Object params);

    ExecutableCmdBuilder openParen();

    ExecutableCmdBuilder closeParen();

    ExecutableCmdBuilder orderBy(String column, OrderBy orderBy);

    <E, R> ExecutableCmdBuilder orderBy(Function<E, R> function, OrderBy orderBy);

    ExecutableCmdBuilder groupBy(String... columns);

    <E, R> ExecutableCmdBuilder groupBy(Function<E, R> function);

    ExecutableCmdBuilder having(String... conditions);

    ExecutableCmdBuilder forceNative();

    ExecutableCmdBuilder updateNull();

    ExecutableCmdBuilder paginate(int pageNum, int pageSize);

    ExecutableCmdBuilder limit(int offset, int size);

    ExecutableCmdBuilder disableCountQuery();

    ExecutableCmdBuilder generateKey(GenerateKey generateKey);

//    JdbcEngineConfig getJdbcEngineConfig();

    boolean isEmptySelectColumns();

    boolean isUpdateNull();

    Class<?> getResultType();

    String resolveTableAlias(String table);

    Map<String, Object> getParameterMap();

    ExecutableCmd build();


//    ExecutableCmdBuilder addColumn(String... columns);
//    <E, R> ExecutableCmdBuilder addSelectFields(Function<E, R> function);
//    ExecutableCmdBuilder addAliasSelectFields(String tableAlias, String... fields);
//    ExecutableCmdBuilder dropSelectFields(String... fields);
//    <E, R> ExecutableCmdBuilder dropSelectFields(Function<E, R> function);
//    ExecutableCmdBuilder as(String aliasName);
//    ExecutableCmdBuilder on(String on);
//    <E1, R1, E2, R2> ExecutableCmdBuilder on(Function<E1, R1> table1Field, SqlOperator sqlOperator, Function<E2, R2> table2Field);
//    ExecutableCmdBuilder where(String field, Object value);
//    ExecutableCmdBuilder where(String field, SqlOperator sqlOperator, Object value);
//    <E, R> ExecutableCmdBuilder where(Function<E, R> function, SqlOperator sqlOperator, Object value);
//    ExecutableCmdBuilder where(SqlPart sqlPart);
//    ExecutableCmdBuilder whereForObject(Object object);
//    ExecutableCmdBuilder whereAppend(String segment);
//    ExecutableCmdBuilder whereAppend(String segment, Object value);
//    ExecutableCmdBuilder orderBy(String fields, OrderBy orderBy);
//    <E, R> ExecutableCmdBuilder orderBy(Function<E, R> function, OrderBy orderBy);
//    ExecutableCmdBuilder groupBy(String... fields);
//    <E, R> ExecutableCmdBuilder groupBy(Function<E, R> function);
//    Select<M> as(String alias);
//    Select<M> addColumn(String... fields);
//    Select<M> addAliasColumn(String tableAlias, String... fields);
//    <E, R> Select<M> addColumn(Function<E, R> function);
//    Select<M> dropColumn(String... fields);
//    <E, R> Select<M> dropColumn(Function<E, R> function);
//    Select<M> innerJoin(Class<?> cls);
//    Select<M> on(String on);
//    <E1, R1, E2, R2> Select<M> on(Function<E1, R1> table1Field, Function<E2, R2> table2Field);
//    <E1, R1, E2, R2> Select<M> on(Function<E1, R1> table1Field, SqlOperator sqlOperator, Function<E2, R2> table2Field);
//    <E, R> Select<M> groupBy(Function<E, R> function);
//    Select<M> orderBy(String field, OrderBy orderBy);
//    <E, R> Select<M> orderBy(Function<E, R> function, OrderBy orderBy);

}

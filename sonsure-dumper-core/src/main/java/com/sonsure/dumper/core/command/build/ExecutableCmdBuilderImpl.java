package com.sonsure.dumper.core.command.build;

import com.sonsure.dumper.common.model.MultiTuple;
import com.sonsure.dumper.common.model.Pagination;
import com.sonsure.dumper.common.utils.UUIDUtils;
import com.sonsure.dumper.core.command.*;
import com.sonsure.dumper.core.command.lambda.Function;
import com.sonsure.dumper.core.command.lambda.LambdaField;
import com.sonsure.dumper.core.command.lambda.LambdaHelper;
import com.sonsure.dumper.core.exception.SonsureJdbcException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author liyd
 */
public class ExecutableCmdBuilderImpl implements ExecutableCmdBuilder {

    protected static final String SPACE = " ";
    protected static final String NULL = "null";
    protected static final String COLON = ":";
    protected static final String PARAM_PLACEHOLDER = " ? ";

    private final SimpleSQL simpleSQL;
    private final List<SqlParameter> sqlParameters;
    protected boolean namedParameter = false;
    protected Map<String, String> tableAliasMapping;
    protected String latestTable;
    protected String latestTableAlias;
    protected SqlStatementType latestStatement;

    protected Pagination pagination;
    protected boolean disableCountQuery = false;
    protected boolean forceNative = false;

    public ExecutableCmdBuilderImpl() {
        simpleSQL = new SimpleSQL();
        sqlParameters = new ArrayList<>(32);
        tableAliasMapping = new HashMap<>(8);
    }

    @Override
    public ExecutableCmdBuilder insertInto(String table) {
        simpleSQL.insertInto(table);
        this.latestTable = table;
        latestStatement = SqlStatementType.TABLE;
        return this;
    }

    @Override
    public ExecutableCmdBuilder intoColumns(String... columns) {
        return this;
    }

    @Override
    public ExecutableCmdBuilder intoValues(String... values) {
        return null;
    }

    @Override
    public ExecutableCmdBuilder select(String... columns) {
        simpleSQL.select(columns);
        return this;
    }

    @Override
    public <E, R> ExecutableCmdBuilder select(Function<E, R> function) {
        LambdaField lambdaField = LambdaHelper.getLambdaClass(function);
        String tableAlias = this.tableAliasMapping.get(lambdaField.getSimpleClassName());
        String field = CommandBuildHelper.getTableAliasFieldName(tableAlias, lambdaField.getFieldName());
        return this.select(field);
    }

    @Override
    public ExecutableCmdBuilder dropSelectColumn(String... columns) {
        simpleSQL.dropSelectColumns(columns);
        return this;
    }

    @Override
    public <E, R> ExecutableCmdBuilder dropSelectColumn(Function<E, R> function) {
        LambdaField lambdaField = LambdaHelper.getLambdaClass(function);
        String tableAlias = this.tableAliasMapping.get(lambdaField.getSimpleClassName());
        String field = CommandBuildHelper.getTableAliasFieldName(tableAlias, lambdaField.getFieldName());
        return this.dropSelectColumn(field);
    }

    @Override
    public ExecutableCmdBuilder selectDistinct(String... columns) {
        return null;
    }

    @Override
    public ExecutableCmdBuilder from(String table) {
        simpleSQL.from(table);
        this.latestTable = table;
        latestStatement = SqlStatementType.TABLE;
        return this;
    }

    @Override
    public ExecutableCmdBuilder join(String... tables) {
        return null;
    }

    @Override
    public ExecutableCmdBuilder innerJoin(String table) {
        simpleSQL.innerJoin(table);
        this.latestTable = table;
        latestStatement = SqlStatementType.INNER_JOIN;
        return this;
    }

    @Override
    public ExecutableCmdBuilder outerJoin(String... tables) {
        return null;
    }

    @Override
    public ExecutableCmdBuilder leftOuterJoin(String... tables) {
        return null;
    }

    @Override
    public ExecutableCmdBuilder rightOuterJoin(String... tables) {
        return null;
    }

    @Override
    public <E1, R1, E2, R2> ExecutableCmdBuilder joinStepOn(Function<E1, R1> table1Field, Function<E2, R2> table2Field) {
        LambdaField lambdaField1 = LambdaHelper.getLambdaClass(table1Field);
        String tableAlias1 = this.tableAliasMapping.get(lambdaField1.getSimpleClassName());
        String field1 = CommandBuildHelper.getTableAliasFieldName(tableAlias1, lambdaField1.getFieldName());

        LambdaField lambdaField2 = LambdaHelper.getLambdaClass(table2Field);
        String tableAlias2 = this.tableAliasMapping.get(lambdaField2.getSimpleClassName());
        String field2 = CommandBuildHelper.getTableAliasFieldName(tableAlias2, lambdaField2.getFieldName());
        this.simpleSQL.joinStepOn(String.format("%s %s %s", field1, SqlOperator.EQ.getCode(), field2), this.latestStatement);
        return this;
    }

    @Override
    public ExecutableCmdBuilder joinStepOn(String on) {
        simpleSQL.joinStepOn(on, this.latestStatement);
        return this;
    }

    @Override
    public ExecutableCmdBuilder update(String table) {
        simpleSQL.update(table);
        this.latestTable = table;
        latestStatement = SqlStatementType.TABLE;
        return this;
    }

    @Override
    public ExecutableCmdBuilder set(String... sets) {
        return null;
    }

    @Override
    public ExecutableCmdBuilder deleteFrom(String table) {
        simpleSQL.deleteFrom(table);
        this.latestTable = table;
        latestStatement = SqlStatementType.TABLE;
        return this;
    }

    @Override
    public ExecutableCmdBuilder as(String alias) {
        this.simpleSQL.as(alias, this.latestStatement);
        this.latestTableAlias = alias;
        this.tableAliasMapping.put(this.latestTable, alias);
        return this;
    }

    @Override
    public ExecutableCmdBuilder namedParameter() {
        return this.namedParameter(true);
    }

    @Override
    public ExecutableCmdBuilder namedParameter(boolean namedParameter) {
        this.namedParameter = namedParameter;
        return this;
    }

    @Override
    public ExecutableCmdBuilder addParameter(String name, Object value) {
        this.sqlParameters.add(new SqlParameter(name, value));
        return this;
    }

    @Override
    public ExecutableCmdBuilder addParameters(List<SqlParameter> parameters) {
        this.sqlParameters.addAll(parameters);
        return this;
    }

    @Override
    public ExecutableCmdBuilder addParameters(Map<String, ?> parameters) {
        for (Map.Entry<String, ?> entry : parameters.entrySet()) {
            this.addParameter(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public ExecutableCmdBuilder where() {
        this.simpleSQL.where();
        return this;
    }

    @Override
    public ExecutableCmdBuilder where(String condition) {
        this.simpleSQL.where(condition);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ExecutableCmdBuilder where(String condition, Object params) {
        this.where(condition);
        if (this.namedParameter) {
            if (!(params instanceof Map)) {
                throw new SonsureJdbcException("namedParameter模式参数必须为Map类型,key与name对应");
            }
            this.addParameters((Map<String, ?>) params);
        } else {
            if (params.getClass().isArray()) {
                Object[] valArray = (Object[]) params;
                for (Object val : valArray) {
                    //这里的参数名用不到，随机生成
                    this.addParameter(UUIDUtils.getUUID8(), val);
                }
            } else {
                this.addParameter(UUIDUtils.getUUID8(), params);
            }
        }

        return this;
    }

    @Override
    public ExecutableCmdBuilder where(String column, SqlOperator sqlOperator, Object value) {
        MultiTuple<String, List<SqlParameter>> pair = this.buildPartStatement(column, sqlOperator, value);
        this.simpleSQL.where(pair.getLeft());
        this.addParameters(pair.getRight());
        return this;
    }

    @Override
    public <E, R> ExecutableCmdBuilder where(Function<E, R> function, SqlOperator sqlOperator, Object value) {
        LambdaField lambdaField = LambdaHelper.getLambdaClass(function);
        String tableAlias = this.tableAliasMapping.get(lambdaField.getSimpleClassName());
        String field = CommandBuildHelper.getTableAliasFieldName(tableAlias, lambdaField.getFieldName());
        return this.where(field, sqlOperator, value);
    }

    @Override
    public ExecutableCmdBuilder condition(String... conditions) {
        return null;
    }

    @Override
    public ExecutableCmdBuilder or() {
        return null;
    }

    @Override
    public ExecutableCmdBuilder or(String... conditions) {
        return null;
    }

    @Override
    public ExecutableCmdBuilder and() {
        return null;
    }

    @Override
    public ExecutableCmdBuilder and(String... conditions) {
        return null;
    }

    @Override
    public ExecutableCmdBuilder openParen() {
        return null;
    }

    @Override
    public ExecutableCmdBuilder closeParen() {
        return null;
    }

    @Override
    public ExecutableCmdBuilder orderBy(String column, OrderBy orderBy) {
        this.simpleSQL.orderBy(String.format("%s %s", column, orderBy.getCode()));
        return this;
    }

    @Override
    public <E, R> ExecutableCmdBuilder orderBy(Function<E, R> function, OrderBy orderBy) {
        LambdaField lambdaField = LambdaHelper.getLambdaClass(function);
        String tableAlias = this.tableAliasMapping.get(lambdaField.getSimpleClassName());
        String field = CommandBuildHelper.getTableAliasFieldName(tableAlias, lambdaField.getFieldName());
        return this.orderBy(field, orderBy);
    }

    @Override
    public ExecutableCmdBuilder groupBy(String... columns) {
        this.simpleSQL.groupBy(columns);
        return this;
    }

    @Override
    public <E, R> ExecutableCmdBuilder groupBy(Function<E, R> function) {
        String field = LambdaHelper.getFieldName(function);
        return this.groupBy(field);
    }

    @Override
    public ExecutableCmdBuilder having(String... conditions) {
        return null;
    }

    @Override
    public ExecutableCmdBuilder forceNative() {
        this.forceNative = true;
        return this;
    }

    @Override
    public ExecutableCmdBuilder paginate(int pageNum, int pageSize) {
        this.pagination = new Pagination();
        pagination.setPageSize(pageSize);
        pagination.setPageNum(pageNum);
        return this;
    }

    @Override
    public ExecutableCmdBuilder limit(int offset, int size) {
        this.pagination = new Pagination();
        pagination.setPageSize(size);
        pagination.setOffset(offset);
        return this;
    }

    @Override
    public ExecutableCmdBuilder disableCountQuery() {
        this.disableCountQuery = true;
        return this;
    }

    @Override
    public boolean isEmptySelectColumns() {
        return this.simpleSQL.isEmptySelectColumns();
    }

    @Override
    public Map<String, Object> getParameterMap() {
        return this.sqlParameters.stream()
                .collect(Collectors.toMap(SqlParameter::getName, SqlParameter::getValue));
    }

    protected MultiTuple<String, List<SqlParameter>> buildPartStatement(String field, SqlOperator sqlOperator, Object value) {
        StringBuilder conditionSql = new StringBuilder();
        List<SqlParameter> conditionParameters = new ArrayList<>(16);
        NativeContentWrapper nativeContentWrapper = new NativeContentWrapper(field);

        if (value == null) {
            conditionSql.append(field).append(SPACE)
                    .append(sqlOperator.getCode()).append(SPACE).append(NULL);
        } else if (nativeContentWrapper.isNatives()) {
            conditionSql.append(nativeContentWrapper.getActualContent()).append(SPACE)
                    .append(sqlOperator.getCode()).append(SPACE)
                    .append(value);
        } else {
            if (this.namedParameter) {
                conditionSql.append(field).append(SPACE)
                        .append(sqlOperator.getCode()).append(SPACE)
                        .append(COLON).append(field);
                conditionParameters.add(new SqlParameter(field, value));
            } else {
                if (value.getClass().isArray()) {
                    Object[] valArray = (Object[]) value;
                    StringBuilder paramPlaceholder = new StringBuilder("(");
                    List<SqlParameter> params = new ArrayList<>(valArray.length);
                    int count = 1;
                    for (Object val : valArray) {
                        paramPlaceholder.append(PARAM_PLACEHOLDER).append(",");
                        params.add(new SqlParameter(field + (count++), val));
                    }
                    paramPlaceholder.deleteCharAt(paramPlaceholder.length() - 1);
                    paramPlaceholder.append(")");
                    conditionSql.append(field).append(SPACE)
                            .append(sqlOperator.getCode()).append(SPACE)
                            .append(paramPlaceholder);
                    conditionParameters.addAll(params);
                } else {
                    conditionSql.append(field).append(SPACE)
                            .append(sqlOperator.getCode()).append(SPACE)
                            .append(PARAM_PLACEHOLDER);
                    conditionParameters.add(new SqlParameter(field, value));
                }
            }
        }
        return new MultiTuple<>(conditionSql.toString(), conditionParameters);
    }

    @Override
    public ExecutableCmd build() {
        //return new ParamSql(this.simpleSQL.toString(), this.sqlParameters);
        return null;
    }
}

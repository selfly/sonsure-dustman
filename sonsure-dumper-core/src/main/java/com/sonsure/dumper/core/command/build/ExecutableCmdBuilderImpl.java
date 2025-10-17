package com.sonsure.dumper.core.command.build;

import com.sonsure.dumper.common.model.MultiTuple;
import com.sonsure.dumper.common.model.Pagination;
import com.sonsure.dumper.common.utils.StrUtils;
import com.sonsure.dumper.common.utils.UUIDUtils;
import com.sonsure.dumper.common.validation.Verifier;
import com.sonsure.dumper.core.command.*;
import com.sonsure.dumper.core.command.lambda.Function;
import com.sonsure.dumper.core.command.lambda.LambdaField;
import com.sonsure.dumper.core.command.lambda.LambdaHelper;
import com.sonsure.dumper.core.command.named.NamedParameterUtils;
import com.sonsure.dumper.core.command.named.ParsedSql;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
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
    public static final String PARAM_PLACEHOLDER = " ? ";

    protected final SimpleSQL simpleSQL;
    protected final List<SqlParameter> sqlParameters;
    protected String command;
    protected JdbcEngineConfig jdbcEngineConfig;
    protected ExecutionType executionType;
    protected boolean namedParameter = false;
    protected Map<String, String> tableAliasMapping;
    protected String latestTable;
    protected String latestTableAlias;
    protected SqlStatementType latestStatement;

    protected Pagination pagination;
    protected boolean disableCountQuery = false;
    protected boolean forceNative = false;
    protected boolean updateNull = false;
    protected ToggleCase toggleCase;
    protected Class<?> resultType;
    protected GenerateKey generateKey;
    protected List<ExecutableCustomizer> customizers;


    public ExecutableCmdBuilderImpl() {
        simpleSQL = new SimpleSQL();
        sqlParameters = new ArrayList<>(32);
        tableAliasMapping = new HashMap<>(8);
    }

    @Override
    public ExecutableCmdBuilder jdbcEngineConfig(JdbcEngineConfig jdbcEngineConfig) {
        this.jdbcEngineConfig = jdbcEngineConfig;
        return this;
    }

    @Override
    public ExecutableCmdBuilder addCustomizer(ExecutableCustomizer customizer) {
        if (this.customizers == null) {
            this.customizers = new ArrayList<>();
        }
        this.customizers.add(customizer);
        return this;
    }

    @Override
    public ExecutableCmdBuilder command(String command) {
        this.command = command;
        return this;
    }

    @Override
    public ExecutableCmdBuilder executionType(ExecutionType executionType) {
        this.executionType = executionType;
        return this;
    }

    @Override
    public ExecutableCmdBuilder resultType(Class<?> resultType) {
        this.resultType = resultType;
        return this;
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
        simpleSQL.intoColumns(columns);
        return this;
    }

    @Override
    public ExecutableCmdBuilder intoValues(Object... values) {
        simpleSQL.intoValues(PARAM_PLACEHOLDER);
        this.processStatementParam(values);
        return null;
    }

    @Override
    public ExecutableCmdBuilder select(String... columns) {
        simpleSQL.select(columns);
        return this;
    }

    @Override
    public <E, R> ExecutableCmdBuilder select(Function<E, R> function) {
        return this.select(lambda2Field(function));
    }

    @Override
    public ExecutableCmdBuilder dropSelectColumn(String... columns) {
        simpleSQL.dropSelectColumns(columns);
        return this;
    }

    @Override
    public <E, R> ExecutableCmdBuilder dropSelectColumn(Function<E, R> function) {
        return this.dropSelectColumn(lambda2Field(function));
    }

    @Override
    public ExecutableCmdBuilder selectDistinct(String... columns) {
        simpleSQL.selectDistinct(columns);
        return this;
    }

    @Override
    public ExecutableCmdBuilder from(String table) {
        simpleSQL.from(table);
        this.latestTable = table;
        latestStatement = SqlStatementType.TABLE;
        return this;
    }

    @Override
    public ExecutableCmdBuilder join(String table) {
        simpleSQL.join(table);
        this.latestTable = table;
        latestStatement = SqlStatementType.JOIN;
        return this;
    }

    @Override
    public ExecutableCmdBuilder innerJoin(String table) {
        simpleSQL.innerJoin(table);
        this.latestTable = table;
        latestStatement = SqlStatementType.INNER_JOIN;
        return this;
    }

    @Override
    public ExecutableCmdBuilder outerJoin(String table) {
        simpleSQL.outerJoin(table);
        this.latestTable = table;
        latestStatement = SqlStatementType.OUTER_JOIN;
        return this;
    }

    @Override
    public ExecutableCmdBuilder leftOuterJoin(String table) {
        simpleSQL.leftOuterJoin(table);
        this.latestTable = table;
        latestStatement = SqlStatementType.LEFT_OUTER_JOIN;
        return this;
    }

    @Override
    public ExecutableCmdBuilder rightOuterJoin(String table) {
        simpleSQL.rightOuterJoin(table);
        this.latestTable = table;
        latestStatement = SqlStatementType.RIGHT_OUTER_JOIN;
        return this;
    }

    @Override
    public <E1, R1, E2, R2> ExecutableCmdBuilder joinStepOn(Function<E1, R1> table1Field, Function<E2, R2> table2Field) {
        String field1 = lambda2Field(table1Field);
        String field2 = lambda2Field(table2Field);
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
    public ExecutableCmdBuilder set(String field, Object value) {
        NativeContentWrapper nativeContentWrapper = new NativeContentWrapper(field);
        if (nativeContentWrapper.isNatives()) {
            this.simpleSQL.set(String.format("%s %s %s", nativeContentWrapper.getActualContent(), SqlOperator.EQ.getCode(), value));
        } else {
            this.simpleSQL.set(String.format("%s %s %s", field, SqlOperator.EQ.getCode(), PARAM_PLACEHOLDER));
            this.addParameter(field, value);
        }
        return this;
    }

    @Override
    public <E, R> ExecutableCmdBuilder set(Function<E, R> function, Object value) {
        return this.set(lambda2Field(function), value);
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
    public ExecutableCmdBuilder addParameters(Object... values) {
        if (this.namedParameter) {
            throw new SonsureJdbcException("namedParameter模式参数必须为Map类型,key与name对应");
        }
        for (Object value : values) {
            this.processStatementParam(value);
        }
        return this;
    }

    @Override
    public ExecutableCmdBuilder where() {
        this.simpleSQL.where();
        return this;
    }

//    @Override
//    public ExecutableCmdBuilder where(String condition) {
//        this.simpleSQL.where(condition);
//        return this;
//    }
//
//    @Override
//    public ExecutableCmdBuilder where(String condition, Object params) {
//        this.where(condition);
//        this.processStatementParam(params);
//        return this;
//    }

    @Override
    public ExecutableCmdBuilder where(String column, SqlOperator sqlOperator, Object value) {
        MultiTuple<String, List<SqlParameter>> pair = this.buildColumnStatement(column, sqlOperator, value);
        this.simpleSQL.where(pair.getLeft());
        this.addParameters(pair.getRight());
        return this;
    }

    @Override
    public <E, R> ExecutableCmdBuilder where(Function<E, R> function, SqlOperator sqlOperator, Object value) {
        return this.where(lambda2Field(function), sqlOperator, value);
    }

    @Override
    public ExecutableCmdBuilder condition(String column, SqlOperator sqlOperator, Object value) {
        return this.where(column, sqlOperator, value);
    }

    @Override
    public <E, R> ExecutableCmdBuilder condition(Function<E, R> function, SqlOperator sqlOperator, Object value) {
        return this.where(function, sqlOperator, value);
    }

    @Override
    public ExecutableCmdBuilder or() {
        this.simpleSQL.or();
        return this;
    }

//    @Override
//    public ExecutableCmdBuilder or(String condition) {
//        this.simpleSQL.or(condition);
//        return this;
//    }
//
//    @Override
//    public ExecutableCmdBuilder or(String condition, Object params) {
//        this.simpleSQL.or(condition);
//        this.processStatementParam(params);
//        return this;
//    }

    @Override
    public ExecutableCmdBuilder or(String column, SqlOperator sqlOperator, Object value) {
        MultiTuple<String, List<SqlParameter>> pair = this.buildColumnStatement(column, sqlOperator, value);
        this.simpleSQL.or(pair.getLeft());
        this.addParameters(pair.getRight());
        return this;
    }

    @Override
    public <E, R> ExecutableCmdBuilder or(Function<E, R> function, SqlOperator sqlOperator, Object value) {
        return this.or(lambda2Field(function), sqlOperator, value);
    }

    @Override
    public ExecutableCmdBuilder and() {
        this.simpleSQL.and();
        return this;
    }

    @Override
    public ExecutableCmdBuilder and(String column, SqlOperator sqlOperator, Object value) {
        MultiTuple<String, List<SqlParameter>> pair = this.buildColumnStatement(column, sqlOperator, value);
        this.simpleSQL.and(pair.getLeft());
        this.addParameters(pair.getRight());
        return this;
    }

    @Override
    public <E, R> ExecutableCmdBuilder and(Function<E, R> function, SqlOperator sqlOperator, Object value) {
        return this.and(lambda2Field(function), sqlOperator, value);
    }

    @Override
    public ExecutableCmdBuilder appendSegment(String segment) {
        this.simpleSQL.appendSegment(segment);
        return this;
    }

    @Override
    public ExecutableCmdBuilder appendSegment(String segment, Object params) {
        this.simpleSQL.appendSegment(segment);
        this.processStatementParam(params);
        return this;
    }

    @Override
    public ExecutableCmdBuilder openParen() {
        this.simpleSQL.openParen();
        return this;
    }

    @Override
    public ExecutableCmdBuilder closeParen() {
        this.simpleSQL.closeParen();
        return this;
    }

    @Override
    public ExecutableCmdBuilder orderBy(String column, OrderBy orderBy) {
        this.simpleSQL.orderBy(String.format("%s %s", column, orderBy.getCode()));
        return this;
    }

    @Override
    public <E, R> ExecutableCmdBuilder orderBy(Function<E, R> function, OrderBy orderBy) {
        return this.orderBy(lambda2Field(function), orderBy);
    }

    @Override
    public ExecutableCmdBuilder groupBy(String... columns) {
        this.simpleSQL.groupBy(columns);
        return this;
    }

    @Override
    public <E, R> ExecutableCmdBuilder groupBy(Function<E, R> function) {
        return this.groupBy(lambda2Field(function));
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
    public ExecutableCmdBuilder updateNull() {
        this.updateNull = true;
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
    public ExecutableCmdBuilder generateKey(GenerateKey generateKey) {
        this.generateKey = generateKey;
        return this;
    }

    @Override
    public boolean isEmptySelectColumns() {
        return this.simpleSQL.isEmptySelectColumns();
    }

    @Override
    public boolean isUpdateNull() {
        return this.updateNull;
    }

    @Override
    public Class<?> getResultType() {
        return this.resultType;
    }

    @Override
    public String resolveTableAlias(String table) {
        return Optional.ofNullable(this.tableAliasMapping.get(table))
                .orElse("");
    }

    @Override
    public Map<String, Object> getParameterMap() {
        return this.sqlParameters.stream()
                .collect(Collectors.toMap(SqlParameter::getName, SqlParameter::getValue));
    }

    @SuppressWarnings("unchecked")
    protected void processStatementParam(Object params) {
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
    }

    protected <E, R> String lambda2Field(Function<E, R> function) {
        LambdaField lambdaField = LambdaHelper.getLambdaClass(function);
        String tableAlias = this.tableAliasMapping.get(lambdaField.getSimpleClassName());
        return CommandBuildHelper.getTableAliasFieldName(tableAlias, lambdaField.getFieldName());
    }

    protected MultiTuple<String, List<SqlParameter>> buildColumnStatement(String column, SqlOperator sqlOperator, Object value) {
        StringBuilder conditionSql = new StringBuilder();
        List<SqlParameter> conditionParameters = new ArrayList<>(16);
        NativeContentWrapper nativeContentWrapper = new NativeContentWrapper(column);

        if (value == null) {
            conditionSql.append(column).append(SPACE)
                    .append(sqlOperator.getCode()).append(SPACE).append(NULL);
        } else if (nativeContentWrapper.isNatives()) {
            conditionSql.append(nativeContentWrapper.getActualContent()).append(SPACE)
                    .append(sqlOperator.getCode()).append(SPACE)
                    .append(value);
        } else {
            if (this.namedParameter) {
                conditionSql.append(column).append(SPACE)
                        .append(sqlOperator.getCode()).append(SPACE)
                        .append(COLON).append(column);
                conditionParameters.add(new SqlParameter(column, value));
            } else {
                if (value.getClass().isArray()) {
                    Object[] valArray = (Object[]) value;
                    StringBuilder paramPlaceholder = new StringBuilder("(");
                    List<SqlParameter> params = new ArrayList<>(valArray.length);
                    int count = 1;
                    for (Object val : valArray) {
                        paramPlaceholder.append(PARAM_PLACEHOLDER).append(",");
                        params.add(new SqlParameter(column + (count++), val));
                    }
                    paramPlaceholder.deleteCharAt(paramPlaceholder.length() - 1);
                    paramPlaceholder.append(")");
                    conditionSql.append(column).append(SPACE)
                            .append(sqlOperator.getCode()).append(SPACE)
                            .append(paramPlaceholder);
                    conditionParameters.addAll(params);
                } else {
                    conditionSql.append(column).append(SPACE)
                            .append(sqlOperator.getCode()).append(SPACE)
                            .append(PARAM_PLACEHOLDER);
                    conditionParameters.add(new SqlParameter(column, value));
                }
            }
        }
        return new MultiTuple<>(conditionSql.toString(), conditionParameters);
    }

    @Override
    public ExecutableCmd build() {
        Verifier.init().notNull(jdbcEngineConfig, "jdbc配置不能为空")
                .notNull(executionType, "执行类型不能为空")
                .notNull(resultType, "结果类型不能为空")
                .validate();
        if (this.customizers != null) {
            for (ExecutableCustomizer customizer : customizers) {
                customizer.customizeBuilder(this);
            }
        }
        if (StrUtils.isBlank(this.command)) {
            this.command = simpleSQL.toString();
        }
        ExecutableCmd executableCmd = new ExecutableCmd();
        executableCmd.setJdbcEngineConfig(jdbcEngineConfig);
        executableCmd.setExecutionType(executionType);
        executableCmd.setResultType(this.resultType);
        executableCmd.setCommand(command);
        executableCmd.setParameters(this.sqlParameters);
        executableCmd.setToggleCase(toggleCase);
        executableCmd.setForceNative(this.forceNative);
        executableCmd.setNamedParameter(this.namedParameter);
        executableCmd.setGenerateKey(this.generateKey);
        executableCmd.setPagination(this.pagination);
        executableCmd.setDisableCountQuery(this.disableCountQuery);


        if (!this.forceNative) {
            // todo 需要收集参数信息，待完成
            Map<String, Object> params = Collections.emptyMap();
            final String resolvedCommand = jdbcEngineConfig.getCommandConversionHandler().convert(executableCmd.getCommand(), params);
            executableCmd.setCommand(resolvedCommand);
        }

        if (this.namedParameter) {
            final ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(executableCmd.getCommand());
            Map<String, Object> paramMap = executableCmd.getParameters()
                    .stream()
                    .collect(Collectors.toMap(SqlParameter::getName, SqlParameter::getValue));
            final String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, paramMap);
            final Object[] objects = NamedParameterUtils.buildValueArray(parsedSql, paramMap);
            executableCmd.setCommand(sqlToUse);
            executableCmd.setParsedParameterNames(parsedSql.getParameterNames());
            executableCmd.setParsedParameterValues(Arrays.asList(objects));
        } else {
            List<Object> params = executableCmd.getParameters().stream()
                    .map(SqlParameter::getValue)
                    .collect(Collectors.toList());
            executableCmd.setParsedParameterValues(params);
        }
        if (this.toggleCase != null) {
            String caseCommand = this.convertCase(executableCmd.getCommand(), this.toggleCase);
            executableCmd.setCommand(caseCommand);
        }

        if (this.customizers != null) {
            for (ExecutableCustomizer customizer : customizers) {
                executableCmd = customizer.customizeCmd(executableCmd);
            }
        }
        return executableCmd;
    }

    protected String convertCase(String content, ToggleCase toggleCase) {
        if (ToggleCase.UPPER == toggleCase) {
            content = content.toUpperCase();
        } else if (ToggleCase.LOWER == toggleCase) {
            content = content.toLowerCase();
        }
        return content;
    }
}

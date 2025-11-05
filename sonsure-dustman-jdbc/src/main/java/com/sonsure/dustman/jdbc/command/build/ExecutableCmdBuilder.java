package com.sonsure.dustman.jdbc.command.build;

import com.sonsure.dustman.common.model.MultiTuple;
import com.sonsure.dustman.common.model.Pagination;
import com.sonsure.dustman.common.utils.StrUtils;
import com.sonsure.dustman.common.utils.UUIDUtils;
import com.sonsure.dustman.common.validation.Verifier;
import com.sonsure.dustman.jdbc.command.named.NamedParameterUtils;
import com.sonsure.dustman.jdbc.command.named.ParsedSql;
import com.sonsure.dustman.jdbc.config.JdbcContext;
import com.sonsure.dustman.jdbc.exception.SonsureJdbcException;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author liyd
 */
@Getter
public class ExecutableCmdBuilder {

    protected static final String SPACE = " ";
    protected static final String NULL = "null";
    protected static final String COLON = ":";
    public static final String PARAM_PLACEHOLDER = " ? ";

    protected final SimpleSQL simpleSQL;
    protected final List<CmdParameter> cmdParameters;
    protected String command;
    protected JdbcContext jdbcContext;
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
    protected Class<?> resultType;
    protected GenerateKey generateKey;
    protected List<ExecutableCustomizer> customizers;

    public ExecutableCmdBuilder() {
        simpleSQL = new SimpleSQL();
        cmdParameters = new ArrayList<>(32);
        tableAliasMapping = new HashMap<>(8);
    }

    public ExecutableCmdBuilder jdbcContext(JdbcContext jdbcContext) {
        this.jdbcContext = jdbcContext;
        return this;
    }

    public ExecutableCmdBuilder addCustomizer(ExecutableCustomizer customizer) {
        if (this.customizers == null) {
            this.customizers = new ArrayList<>();
        }
        this.customizers.add(customizer);
        return this;
    }

    public ExecutableCmdBuilder command(String command) {
        this.command = command;
        return this;
    }

    public ExecutableCmdBuilder executionType(ExecutionType executionType) {
        this.executionType = executionType;
        return this;
    }

    public ExecutableCmdBuilder resultType(Class<?> resultType) {
        this.resultType = resultType;
        return this;
    }

    public ExecutableCmdBuilder insertInto(String table) {
        simpleSQL.insertInto(table);
        this.latestTable = table;
        latestStatement = SqlStatementType.TABLE;
        return this;
    }

    public ExecutableCmdBuilder intoColumns(String... columns) {
        simpleSQL.intoColumns(columns);
        return this;
    }

    public ExecutableCmdBuilder intoValues(Object... values) {
        simpleSQL.intoValues(PARAM_PLACEHOLDER);
        this.processStatementParam(values);
        return null;
    }

    public ExecutableCmdBuilder select(String... columns) {
        simpleSQL.select(columns);
        return this;
    }

    public ExecutableCmdBuilder dropSelectColumn(String... columns) {
        simpleSQL.dropSelectColumns(columns);
        return this;
    }

    public ExecutableCmdBuilder selectDistinct(String... columns) {
        simpleSQL.selectDistinct(columns);
        return this;
    }

    public ExecutableCmdBuilder from(String table) {
        simpleSQL.from(table);
        this.latestTable = table;
        latestStatement = SqlStatementType.TABLE;
        return this;
    }

    public ExecutableCmdBuilder join(String table) {
        simpleSQL.join(table);
        this.latestTable = table;
        latestStatement = SqlStatementType.JOIN;
        return this;
    }

    public ExecutableCmdBuilder innerJoin(String table) {
        simpleSQL.innerJoin(table);
        this.latestTable = table;
        latestStatement = SqlStatementType.INNER_JOIN;
        return this;
    }

    public ExecutableCmdBuilder outerJoin(String table) {
        simpleSQL.outerJoin(table);
        this.latestTable = table;
        latestStatement = SqlStatementType.OUTER_JOIN;
        return this;
    }

    public ExecutableCmdBuilder leftOuterJoin(String table) {
        simpleSQL.leftOuterJoin(table);
        this.latestTable = table;
        latestStatement = SqlStatementType.LEFT_OUTER_JOIN;
        return this;
    }

    public ExecutableCmdBuilder rightOuterJoin(String table) {
        simpleSQL.rightOuterJoin(table);
        this.latestTable = table;
        latestStatement = SqlStatementType.RIGHT_OUTER_JOIN;
        return this;
    }

    public ExecutableCmdBuilder joinStepOn(String on) {
        simpleSQL.joinStepOn(on, this.latestStatement);
        return this;
    }

    public ExecutableCmdBuilder update(String table) {
        simpleSQL.update(table);
        this.latestTable = table;
        latestStatement = SqlStatementType.TABLE;
        return this;
    }

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

    public ExecutableCmdBuilder deleteFrom(String table) {
        simpleSQL.deleteFrom(table);
        this.latestTable = table;
        latestStatement = SqlStatementType.TABLE;
        return this;
    }

    public ExecutableCmdBuilder as(String alias) {
        this.simpleSQL.as(alias, this.latestStatement);
        this.latestTableAlias = alias;
        this.tableAliasMapping.put(this.latestTable, alias);
        return this;
    }

    public ExecutableCmdBuilder namedParameter() {
        return this.namedParameter(true);
    }

    public ExecutableCmdBuilder namedParameter(boolean namedParameter) {
        this.namedParameter = namedParameter;
        return this;
    }

    public ExecutableCmdBuilder addParameter(String name, Object value) {
        this.cmdParameters.add(new CmdParameter(name, value));
        return this;
    }

    public ExecutableCmdBuilder addParameters(List<CmdParameter> parameters) {
        this.cmdParameters.addAll(parameters);
        return this;
    }

    public ExecutableCmdBuilder addParameters(Map<String, ?> parameters) {
        for (Map.Entry<String, ?> entry : parameters.entrySet()) {
            this.addParameter(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public ExecutableCmdBuilder addParameters(Object... values) {
        if (this.namedParameter) {
            throw new SonsureJdbcException("namedParameter模式参数必须为Map类型,key与name对应");
        }
        for (Object value : values) {
            this.processStatementParam(value);
        }
        return this;
    }

    public ExecutableCmdBuilder where() {
        this.simpleSQL.where();
        return this;
    }

    public ExecutableCmdBuilder where(String column, SqlOperator sqlOperator, Object value) {
        MultiTuple<String, List<CmdParameter>> pair = this.buildColumnStatement(column, sqlOperator, value);
        this.simpleSQL.where(pair.getLeft());
        this.addParameters(pair.getRight());
        return this;
    }

    public ExecutableCmdBuilder condition(String column, SqlOperator sqlOperator, Object value) {
        return this.where(column, sqlOperator, value);
    }

    public ExecutableCmdBuilder or() {
        this.simpleSQL.or();
        return this;
    }

    public ExecutableCmdBuilder or(String column, SqlOperator sqlOperator, Object value) {
        MultiTuple<String, List<CmdParameter>> pair = this.buildColumnStatement(column, sqlOperator, value);
        this.simpleSQL.or(pair.getLeft());
        this.addParameters(pair.getRight());
        return this;
    }

    public ExecutableCmdBuilder and() {
        this.simpleSQL.and();
        return this;
    }

    public ExecutableCmdBuilder and(String column, SqlOperator sqlOperator, Object value) {
        MultiTuple<String, List<CmdParameter>> pair = this.buildColumnStatement(column, sqlOperator, value);
        this.simpleSQL.and(pair.getLeft());
        this.addParameters(pair.getRight());
        return this;
    }

    public ExecutableCmdBuilder appendSegment(String segment) {
        this.simpleSQL.appendSegment(segment);
        return this;
    }

    public ExecutableCmdBuilder appendSegment(String segment, Object params) {
        this.simpleSQL.appendSegment(segment);
        this.processStatementParam(params);
        return this;
    }

    public ExecutableCmdBuilder openParen() {
        this.simpleSQL.openParen();
        return this;
    }

    public ExecutableCmdBuilder closeParen() {
        this.simpleSQL.closeParen();
        return this;
    }

    public ExecutableCmdBuilder orderBy(String column, OrderBy orderBy) {
        this.simpleSQL.orderBy(String.format("%s %s", column, orderBy.getCode()));
        return this;
    }

    public ExecutableCmdBuilder groupBy(String... columns) {
        this.simpleSQL.groupBy(columns);
        return this;
    }

    public ExecutableCmdBuilder having(String column, SqlOperator sqlOperator, Object value) {
        MultiTuple<String, List<CmdParameter>> pair = this.buildColumnStatement(column, sqlOperator, value);
        this.simpleSQL.having(pair.getLeft());
        this.addParameters(pair.getRight());
        return this;
    }

    public ExecutableCmdBuilder forceNative() {
        this.forceNative = true;
        return this;
    }

    public ExecutableCmdBuilder updateNull() {
        this.updateNull = true;
        return this;
    }

    public ExecutableCmdBuilder paginate(int pageNum, int pageSize) {
        this.pagination = new Pagination();
        pagination.setPageSize(pageSize);
        pagination.setPageNum(pageNum);
        return this;
    }

    public ExecutableCmdBuilder limit(int offset, int size) {
        this.pagination = new Pagination();
        pagination.setPageSize(size);
        pagination.setOffset(offset);
        return this;
    }

    public ExecutableCmdBuilder disableCountQuery() {
        this.disableCountQuery = true;
        return this;
    }

    public ExecutableCmdBuilder generateKey(GenerateKey generateKey) {
        this.generateKey = generateKey;
        return this;
    }

    public String resolveTableAlias(String table) {
        return Optional.ofNullable(this.tableAliasMapping.get(table))
                .orElse("");
    }

    public boolean isEmptySelectColumns() {
        return this.simpleSQL.isEmptySelectColumns();
    }

    public Map<String, Object> getParameterMap() {
        return this.cmdParameters.stream()
                .collect(Collectors.toMap(CmdParameter::getName, CmdParameter::getValue));
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

    protected MultiTuple<String, List<CmdParameter>> buildColumnStatement(String column, SqlOperator sqlOperator, Object value) {
        StringBuilder conditionSql = new StringBuilder();
        List<CmdParameter> conditionParameters = new ArrayList<>(16);
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
                conditionParameters.add(new CmdParameter(column, value));
            } else {
                if (value.getClass().isArray()) {
                    Object[] valArray = (Object[]) value;
                    StringBuilder paramPlaceholder = new StringBuilder("(");
                    List<CmdParameter> params = new ArrayList<>(valArray.length);
                    int count = 1;
                    for (Object val : valArray) {
                        paramPlaceholder.append(PARAM_PLACEHOLDER).append(",");
                        params.add(new CmdParameter(column + (count++), val));
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
                    conditionParameters.add(new CmdParameter(column, value));
                }
            }
        }
        return new MultiTuple<>(conditionSql.toString(), conditionParameters);
    }


    public ExecutableCmd build() {
        Verifier.init().notNull(jdbcContext, "jdbc配置不能为空")
                .notNull(executionType, "执行类型不能为空")
                .notNull(resultType, "结果类型不能为空")
                .validate();
        if (this.customizers != null) {
            for (ExecutableCustomizer customizer : customizers) {
                customizer.customize(this);
            }
        }
        if (StrUtils.isBlank(this.command)) {
            this.command = simpleSQL.toString();
        }
        ExecutableCmd executableCmd = new ExecutableCmd();
        executableCmd.setJdbcContext(jdbcContext);
        executableCmd.setExecutionType(executionType);
        executableCmd.setResultType(this.resultType);
        executableCmd.setCommand(command);
        executableCmd.setParameters(this.cmdParameters);
        executableCmd.setCaseStyle(jdbcContext.getCaseStyle());
        executableCmd.setForceNative(this.forceNative);
        executableCmd.setNamedParameter(this.namedParameter);
        executableCmd.setGenerateKey(this.generateKey);
        executableCmd.setPagination(this.pagination);
        executableCmd.setDisableCountQuery(this.disableCountQuery);


        if (!this.forceNative) {
            final String resolvedCommand = jdbcContext.getCommandConversionHandler().convert(executableCmd.getCommand(), this.cmdParameters, this.jdbcContext);
            executableCmd.setCommand(resolvedCommand);
        }

        if (this.namedParameter) {
            final ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(executableCmd.getCommand());
            Map<String, Object> paramMap = executableCmd.getParameters()
                    .stream()
                    .collect(Collectors.toMap(CmdParameter::getName, CmdParameter::getValue));
            final String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, paramMap);
            final Object[] objects = NamedParameterUtils.buildValueArray(parsedSql, paramMap);
            executableCmd.setCommand(sqlToUse);
            executableCmd.setParsedParameterNames(parsedSql.getParameterNames());
            executableCmd.setParsedParameterValues(Arrays.asList(objects));
        } else {
            List<Object> params = executableCmd.getParameters().stream()
                    .map(CmdParameter::getValue)
                    .collect(Collectors.toList());
            executableCmd.setParsedParameterValues(params);
        }
        if (executableCmd.getCaseStyle() != null) {
            String caseCommand = this.convertCase(executableCmd.getCommand(), executableCmd.getCaseStyle());
            executableCmd.setCommand(caseCommand);
        }
        return executableCmd;
    }

    protected String convertCase(String content, CaseStyle caseStyle) {
        if (CaseStyle.UPPERCASE == caseStyle) {
            content = content.toUpperCase();
        } else if (CaseStyle.LOWERCASE == caseStyle) {
            content = content.toLowerCase();
        }
        return content;
    }
}

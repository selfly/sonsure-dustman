package com.sonsure.dumper.core.command.build;

import com.sonsure.dumper.common.model.Pagination;
import com.sonsure.dumper.core.config.JdbcContext;
import com.sonsure.dumper.core.persist.ExecutionFunction;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author selfly
 */
@Getter
@Setter
public class ExecutableCmd {

    /**
     * The Jdbc engine config.
     */
    private JdbcContext jdbcContext;

    /**
     * The Command type.
     */
    private ExecutionType executionType;

    /**
     * 命令，一般指sql
     */
    private String command;

    /**
     * The Parameters.
     */
    private List<CmdParameter> parameters;

    private List<Object> parsedParameterValues;
    private List<String> parsedParameterNames;

    /**
     * The Command case.
     */
    private CaseStyle caseStyle;

    /**
     * The Is native command.
     */
    private boolean forceNative;

    /**
     * The Is named parameter.
     */
    private boolean isNamedParameter;

    /**
     * 返回值类型，如果是native操作又不指定，可能为null
     */
    private Class<?> resultType;

    /**
     * 主键值，pkValueByDb=false才有
     */
    private GenerateKey generateKey;

    /**
     * The Pagination.
     */
    private Pagination pagination;

    /**
     * The Disable count query.
     */
    private boolean disableCountQuery;

    /**
     * The Execution function.
     */
    private ExecutionFunction<Object, Object> executionFunction;

    public ExecutableCmd() {
        forceNative = false;
        isNamedParameter = false;
    }
}

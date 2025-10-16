package com.sonsure.dumper.core.command.build;

import com.sonsure.dumper.common.model.Pagination;
import com.sonsure.dumper.core.command.CommandCase;
import com.sonsure.dumper.core.command.CommandType;
import com.sonsure.dumper.core.command.GenerateKey;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
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
    private JdbcEngineConfig jdbcEngineConfig;

    /**
     * The Command type.
     */
    private CommandType commandType;

    /**
     * The Command case.
     */
    private CommandCase commandCase;

    /**
     * 命令，一般指sql
     */
    private String command;

    /**
     * The Parameters.
     */
    private List<SqlParameter> parameters;

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

    public ExecutableCmd() {
        forceNative = false;
        isNamedParameter = false;
    }
}

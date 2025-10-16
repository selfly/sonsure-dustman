package com.sonsure.dumper.core.command;

import com.sonsure.dumper.common.model.Pageable;
import com.sonsure.dumper.common.model.Pagination;
import com.sonsure.dumper.core.command.build.ExecutableCmd;
import com.sonsure.dumper.core.command.named.NamedParameterUtils;
import com.sonsure.dumper.core.command.named.ParsedSql;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author selfly
 */
@Getter
public abstract class AbstractCommandDetailsBuilder<T extends CommandDetailsBuilder<T>> implements CommandDetailsBuilder<T> {

    protected static final String SPACE = " ";
    protected static final String NULL = "null";
    protected static final String COLON = ":";
    protected static final String PARAM_PLACEHOLDER = " ? ";

    protected boolean forceNative = false;
    protected Pagination pagination;
    protected boolean disableCountQuery = false;

    @Override
    public T forceNative() {
        this.forceNative = true;
        return this.getSelf();
    }

    @Override
    public T namedParameter() {
        this.namedParameter = true;
        return this.getSelf();
    }

    @Override
    public T paginate(int pageNum, int pageSize) {
        this.pagination = new Pagination();
        pagination.setPageSize(pageSize);
        pagination.setPageNum(pageNum);
        return this.getSelf();
    }

    @Override
    public T paginate(Pageable pageable) {
        this.paginate(pageable.getPageNum(), pageable.getPageSize());
        return this.getSelf();
    }

    @Override
    public T limit(int offset, int size) {
        this.pagination = new Pagination();
        pagination.setPageSize(size);
        pagination.setOffset(offset);
        return this.getSelf();
    }

    @Override
    public T disableCountQuery() {
        this.disableCountQuery = true;
        return this.getSelf();
    }

    @Override
    public CommandDetails build(JdbcEngineConfig jdbcEngineConfig, CommandType commandType) {
        ExecutableCmd executableCmd = new ExecutableCmd();
        executableCmd.setJdbcEngineConfig(jdbcEngineConfig);
        executableCmd.setCommandType(commandType);
        executableCmd.setCommandCase(jdbcEngineConfig.getCommandCase());
        executableCmd.setPagination(this.getPagination());
        executableCmd.setForceNative(this.isForceNative());
        executableCmd.setNamedParameter(this.isNamedParameter());
        executableCmd.setDisableCountQuery(this.isDisableCountQuery());

        this.doCustomize(executableCmd);



        if (!commandDetails.isForceNative()) {
            // todo 需要收集参数信息，待完成
            Map<String, Object> params = Collections.emptyMap();
            final String resolvedCommand = jdbcEngineConfig.getCommandConversionHandler().convert(commandDetails.getCommand(), params);
            commandDetails.setCommand(resolvedCommand);
        }

        if (commandDetails.isNamedParameter()) {
            final ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(commandDetails.getCommand());
            CommandParameters commandParameters = commandDetails.getCommandParameters();
            final Map<String, Object> paramMap = commandParameters.getParameterMap();
            final String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, paramMap);
            final Object[] objects = NamedParameterUtils.buildValueArray(parsedSql, paramMap);
            commandDetails.setCommand(sqlToUse);
            commandParameters.setParsedParameterNames(parsedSql.getParameterNames());
            commandParameters.setParsedParameterValues(Arrays.asList(objects));
        } else {
            CommandParameters commandParameters = commandDetails.getCommandParameters();
            final List<Object> objects = commandParameters.getParameterValues();
            commandParameters.setParsedParameterValues(objects);
        }
        if (commandDetails.getCommandCase() != null) {
            String caseCommand = this.convertCase(commandDetails.getCommand(), commandDetails.getCommandCase());
            commandDetails.setCommand(caseCommand);
        }
        return commandDetails;
    }

    /**
     * 转换大小写
     *
     * @param content     the content
     * @param commandCase the command case
     * @return string
     */
    protected String convertCase(String content, CommandCase commandCase) {
        if (CommandCase.UPPER == commandCase) {
            content = content.toUpperCase();
        } else if (CommandCase.LOWER == commandCase) {
            content = content.toLowerCase();
        }
        return content;
    }

    /**
     * 构建
     *
     * @param executableCmd the executable cmd
     */
    protected abstract void doCustomize(ExecutableCmd executableCmd);

    @SuppressWarnings("unchecked")
    protected T getSelf() {
        return (T) this;
    }
}

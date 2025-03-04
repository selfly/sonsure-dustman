package com.sonsure.dumper.core.command;

import com.sonsure.dumper.common.model.Pageable;
import com.sonsure.dumper.common.model.Pagination;
import com.sonsure.dumper.core.command.named.NamedParameterUtils;
import com.sonsure.dumper.core.command.named.ParsedSql;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.third.mybatis.CommandSql;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author selfly
 */
@Getter
public abstract class AbstractCommandDetailsBuilder<T extends CommandDetailsBuilder<T>> implements CommandDetailsBuilder<T> {

    protected static final String PARAM_PLACEHOLDER = " ? ";

    protected final JdbcEngineConfig jdbcEngineConfig;
    protected final CommandSql commandSql;
    protected final List<Object> parameters;
    protected boolean forceNative = false;
    protected Pagination pagination;
    protected boolean disableCountQuery = false;

    public AbstractCommandDetailsBuilder(JdbcEngineConfig jdbcEngineConfig) {
        this.jdbcEngineConfig = jdbcEngineConfig;
        this.commandSql = new CommandSql();
        this.parameters = new ArrayList<>(16);
    }

    @Override
    public T forceNative() {
        this.forceNative = true;
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
    public CommandDetails build(JdbcEngineConfig jdbcEngineConfig) {
        CommandDetails commandDetails = this.doBuild(jdbcEngineConfig);
        if (!commandDetails.isForceNative()) {
            // todo 需要收集参数信息，待完成
            Map<String, Object> params = Collections.emptyMap();
            final String resolvedCommand = jdbcEngineConfig.getCommandConversionHandler().convert(commandDetails.getCommand(), params);
            commandDetails.setCommand(resolvedCommand);
        }

        if (commandDetails.isNamedParameter()) {
            final ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(commandDetails.getCommand());
            final Map<String, Object> paramMap = commandDetails.getCommandParameters().stream()
                    .collect(Collectors.toMap(CommandParameter::getName, CommandParameter::getValue));
            final String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, paramMap);
            final Object[] objects = NamedParameterUtils.buildValueArray(parsedSql, paramMap);
            commandDetails.setCommand(sqlToUse);
            commandDetails.setNamedParamNames(parsedSql.getParameterNames());
            commandDetails.setParameters(Arrays.asList(objects));
        } else {
            final List<Object> objects = commandDetails.getCommandParameters().stream()
                    .map(CommandParameter::getValue)
                    .collect(Collectors.toList());
            commandDetails.setParameters(objects);
        }
        if (StringUtils.isNotBlank(jdbcEngineConfig.getCommandCase())) {
            String resolvedCommand = this.convertCase(commandDetails.getCommand(), jdbcEngineConfig.getCommandCase());
            commandDetails.setCommand(resolvedCommand);
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
    protected String convertCase(String content, String commandCase) {
        if (StringUtils.equalsIgnoreCase(commandCase, "upper")) {
            content = content.toUpperCase();
        } else if (StringUtils.equalsIgnoreCase(commandCase, "lower")) {
            content = content.toLowerCase();
        }
        return content;
    }

    /**
     * 构建
     *
     * @param jdbcEngineConfig the jdbc engine config
     * @return command context
     */
    public abstract CommandDetails doBuild(JdbcEngineConfig jdbcEngineConfig);

    protected T getSelf() {
        //noinspection unchecked
        return (T) this;
    }
}

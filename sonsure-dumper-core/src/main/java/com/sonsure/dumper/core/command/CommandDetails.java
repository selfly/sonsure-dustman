/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command;

import com.sonsure.dumper.common.model.Pagination;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 执行的命令内容
 * <p>
 *
 * @author liyd
 * @date 17 /4/12
 */
@Getter
@Setter
public class CommandDetails {

    /**
     * 命令，一般指sql
     */
    private String command;

    /**
     * The Is native command.
     */
    private boolean forceNative;

    /**
     * The Is named parameter.
     */
    private boolean isNamedParameter;

    /**
     * The Named param names.
     */
    private List<String> namedParamNames;

    /**
     * The Command parameters.
     */
    private final List<CommandParameter> commandParameters;

    /**
     * The Parameters.
     */
    private List<Object> parameters;

    /**
     * 返回值类型，如果是native操作又不指定，可能为null
     */
    private Class<?> resultType;

    /**
     * 主键值，pkValueByDb=false才有
     */
    private GenerateKey generateKey;

    private Pagination pagination;

    private boolean disableCountQuery;

    public CommandDetails() {
        forceNative = false;
        isNamedParameter = false;
        commandParameters = new ArrayList<>();
        parameters = new ArrayList<>();
    }

    public void addCommandParameters(List<CommandParameter> commandParameters) {
        this.commandParameters.addAll(commandParameters);
    }

    public void addCommandParameter(CommandParameter commandParameter) {
        this.commandParameters.add(commandParameter);
    }

    public void addCommandParameter(String name, Object value) {
        this.addCommandParameter(new CommandParameter(name, value));
    }

}

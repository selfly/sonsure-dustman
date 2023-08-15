/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command;


import com.sonsure.commons.model.Pagination;

import java.util.ArrayList;
import java.util.List;

/**
 * 执行的命令内容
 * <p>
 *
 * @author liyd
 * @date 17 /4/12
 */
public class CommandContext {

    /**
     * 命令，一般指sql
     */
    private String command;

    /**
     * The Is native command.
     */
    private boolean isNativeCommand;

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

    private boolean count;

    public CommandContext() {
        isNativeCommand = false;
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

    public List<Object> getParameters() {
        return parameters;
    }

    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Class<?> getResultType() {
        return resultType;
    }

    public void setResultType(Class<?> resultType) {
        this.resultType = resultType;
    }

    public GenerateKey getGenerateKey() {
        return generateKey;
    }

    public void setGenerateKey(GenerateKey generateKey) {
        this.generateKey = generateKey;
    }

    public boolean isNativeCommand() {
        return isNativeCommand;
    }

    public void setNativeCommand(boolean nativeCommand) {
        isNativeCommand = nativeCommand;
    }

    public boolean isNamedParameter() {
        return isNamedParameter;
    }

    public void setNamedParameter(boolean namedParameter) {
        isNamedParameter = namedParameter;
    }

    public List<CommandParameter> getCommandParameters() {
        return commandParameters;
    }

    public List<String> getNamedParamNames() {
        return namedParamNames;
    }

    public void setNamedParamNames(List<String> namedParamNames) {
        this.namedParamNames = namedParamNames;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public boolean isCount() {
        return count;
    }

    public void setCount(boolean count) {
        this.count = count;
    }
}

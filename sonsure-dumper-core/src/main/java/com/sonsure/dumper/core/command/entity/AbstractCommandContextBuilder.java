/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;

import com.sonsure.dumper.core.command.CommandContext;
import com.sonsure.dumper.core.command.CommandContextBuilder;
import com.sonsure.dumper.core.command.CommandContextBuilderContext;
import com.sonsure.dumper.core.command.CommandParameter;
import com.sonsure.dumper.core.command.named.NamedParameterUtils;
import com.sonsure.dumper.core.command.named.ParsedSql;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import com.sonsure.dumper.core.management.CommandField;
import com.sonsure.dumper.core.management.ModelClassCache;
import com.sonsure.dumper.core.management.ModelFieldMeta;
import com.sonsure.dumper.core.mapping.AbstractMappingHandler;
import com.sonsure.dumper.core.mapping.MappingHandler;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Abstract command context builder.
 *
 * @author liyd
 * @date 17 /4/12
 */
public abstract class AbstractCommandContextBuilder implements CommandContextBuilder {

    private final CommandContextBuilderContext commandContextBuilderContext;

    public AbstractCommandContextBuilder(CommandContextBuilderContext commandContextBuilderContext) {
        this.commandContextBuilderContext = commandContextBuilderContext;
    }

    public void addModelClass(Class<?> cls) {
        this.commandContextBuilderContext.addModelClass(cls);
    }

    /**
     * Native command.
     */
    public void forceNative() {
        commandContextBuilderContext.setForceNative(true);
    }

    public void namedParameter() {
        this.commandContextBuilderContext.setNamedParameter(true);
    }

    public CommandContextBuilderContext getCommandContextBuilderContext() {
        return commandContextBuilderContext;
    }

    @Override
    public CommandContext build(JdbcEngineConfig jdbcEngineConfig) {

        CommandContext commandContext = this.doBuild(jdbcEngineConfig);
        //如果是where、group等子构建，直接返回
        if (this.commandContextBuilderContext.isSubBuilderContext()) {
            return commandContext;
        }
        MappingHandler mappingHandler = jdbcEngineConfig.getMappingHandler();
        if (mappingHandler instanceof AbstractMappingHandler) {
            ((AbstractMappingHandler) mappingHandler).addClassMapping(this.commandContextBuilderContext.getModelClasses());
        }

        if (!this.commandContextBuilderContext.isForceNative()) {
            // todo 需要收集参数信息，待完成
            Map<String, Object> params = Collections.emptyMap();
            final String resolvedCommand = jdbcEngineConfig.getCommandConversionHandler().convert(commandContext.getCommand(), params);
            commandContext.setCommand(resolvedCommand);
        }

        if (this.commandContextBuilderContext.isNamedParameter()) {
            final ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(commandContext.getCommand());
            final Map<String, Object> paramMap = commandContext.getCommandParameters().stream()
                    .collect(Collectors.toMap(CommandParameter::getName, CommandParameter::getValue));
            final String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, paramMap);
            final Object[] objects = NamedParameterUtils.buildValueArray(parsedSql, paramMap);
            commandContext.setCommand(sqlToUse);
            commandContext.setNamedParamNames(parsedSql.getParameterNames());
            commandContext.setParameters(Arrays.asList(objects));
        } else {
            final List<Object> objects = commandContext.getCommandParameters().stream()
                    .map(CommandParameter::getValue)
                    .collect(Collectors.toList());
            commandContext.setParameters(objects);
        }
        if (StringUtils.isNotBlank(jdbcEngineConfig.getCommandCase())) {
            String resolvedCommand = this.convertCase(commandContext.getCommand(), jdbcEngineConfig.getCommandCase());
            commandContext.setCommand(resolvedCommand);
        }
        return commandContext;
    }

    /**
     * 构建执行内容
     *
     * @param jdbcEngineConfig the jdbc engine config
     * @return command context
     */
    public abstract CommandContext doBuild(JdbcEngineConfig jdbcEngineConfig);

    /**
     * Gets unique model class.
     *
     * @return the unique model class
     */
    protected Class<?> getUniqueModelClass() {
        final Set<Class<?>> modelClasses = this.commandContextBuilderContext.getModelClasses();
        if (modelClasses == null || modelClasses.size() != 1) {
            throw new SonsureJdbcException("当前执行业务不止一个Model Class");
        }
        return modelClasses.iterator().next();
    }


    /**
     * Create class field.
     *
     * @param name              the name
     * @param analyseTableAlias the analyse table alias
     * @return the class field
     */
    protected CommandField createCommandClassField(String name, boolean analyseTableAlias, CommandField.Type type, Class<?> cls) {
        return new CommandField(name, analyseTableAlias, type, cls);
    }

    /**
     * Create class field .
     *
     * @param name              the name
     * @param analyseTableAlias the analyse table alias
     * @param type              the type
     * @return the class field
     */
    public CommandField createCommandClassField(String name, boolean analyseTableAlias, CommandField.Type type) {
        return this.createCommandClassField(name, analyseTableAlias, type, null);
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
     * 获取带别名的field
     *
     * @param tableAlias the table alias
     * @param field      the field
     * @return table alias field
     */
    protected String getTableAliasField(String tableAlias, String field) {
        if (StringUtils.isNotBlank(tableAlias)) {
            return new StringBuilder(tableAlias).append(".").append(field).toString();
        }
        return field;
    }

    /**
     * 获取带别名的model名
     *
     * @param modelClass the model class
     * @param tableAlias the table alias
     * @return column table alias name
     */
    protected String getModelAliasName(Class<?> modelClass, String tableAlias) {
        StringBuilder sb = new StringBuilder(modelClass.getSimpleName());
        if (StringUtils.isNotBlank(tableAlias)) {
            sb.append(" ").append(tableAlias);
        }
        return sb.toString();
    }

    protected String getModelName(Class<?> modelClass) {
        return modelClass.getSimpleName();
    }

    protected String getPkField(Class<?> modelClass, MappingHandler mappingHandler) {
        return mappingHandler.getPkField(modelClass);
    }

    /**
     * 获取class的属性
     *
     * @param clazz the clazz
     * @return class fields
     */
    protected Collection<ModelFieldMeta> getClassFields(Class<?> clazz) {
        return ModelClassCache.getClassFieldMetas(clazz);
    }

    /**
     * 获取设置了通用参数的CommandContext
     *
     * @return generic command context
     */
    protected CommandContext createCommandContext() {
        return new CommandContext();
    }


    protected String getFiledCommandName(CommandField commandField, JdbcEngineConfig jdbcEngineConfig) {
        if (this.commandContextBuilderContext.isForceNative() && commandField.getType() == CommandField.Type.ENTITY_FIELD) {
            final MappingHandler mappingHandler = jdbcEngineConfig.getMappingHandler();
            return mappingHandler.getColumn(commandField.getCls(), commandField.getFieldName());
        }
        return commandField.getFieldName();
    }

    protected String createParameterPlaceholder(String fieldName, boolean isNamedParameter) {
        return isNamedParameter ? ":" + fieldName : "?";
    }

}

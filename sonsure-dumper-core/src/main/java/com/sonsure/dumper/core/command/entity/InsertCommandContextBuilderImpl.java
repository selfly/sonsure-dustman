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
import com.sonsure.dumper.core.command.CommandContextBuilderContext;
import com.sonsure.dumper.core.command.CommandParameter;
import com.sonsure.dumper.core.command.GenerateKey;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.management.CommandField;
import com.sonsure.dumper.core.mapping.MappingHandler;
import com.sonsure.dumper.core.persist.KeyGenerator;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liyd
 * @date 17/4/14
 */
public class InsertCommandContextBuilderImpl extends AbstractCommandContextBuilder {

    private static final String COMMAND_OPEN = "insert into ";

    private final Context insertContext;

    public InsertCommandContextBuilderImpl(Context insertContext) {
        super(insertContext);
        this.insertContext = insertContext;
    }

    /**
     * Add insert field.
     *
     * @param field the field
     * @param value the value
     */
    public void addInsertField(String field, Object value) {
        this.addInsertField(field, value, null);
    }

    /**
     * Add insert field.
     *
     * @param field the field
     * @param value the value
     * @param cls   the cls
     */
    public void addInsertField(String field, Object value, Class<?> cls) {
        CommandField.Type type = cls == null ? CommandField.Type.MANUAL_FIELD : CommandField.Type.ENTITY_FIELD;
        CommandField commandField = this.createCommandClassField(field, false, type, cls);
        commandField.setValue(value);
        this.insertContext.addInsertFields(commandField);
    }

    @Override
    public CommandContext doBuild(JdbcEngineConfig jdbcEngineConfig) {

        CommandContext commandContext = createCommandContext();
        MappingHandler mappingHandler = jdbcEngineConfig.getMappingHandler();
        final Class<?> modelClass = this.getUniqueModelClass();
        String pkField = this.getPkField(modelClass, mappingHandler);
        String pkColumn = mappingHandler.getColumn(modelClass, pkField);
        pkColumn = this.convertCase(pkColumn, jdbcEngineConfig.getCommandCase());
        GenerateKey generateKey = new GenerateKey();
        generateKey.setClazz(modelClass);
        generateKey.setColumn(pkColumn);

        commandContext.setGenerateKey(generateKey);

        StringBuilder command = new StringBuilder(COMMAND_OPEN);
        StringBuilder argsCommand = new StringBuilder("(");

        command.append(this.getModelName(modelClass)).append(" (");

        boolean hasPkParam = false;
        for (CommandField commandField : this.insertContext.getInsertFields()) {
            if (StringUtils.equalsIgnoreCase(pkField, commandField.getFieldName())) {
                hasPkParam = true;
            }
            final String placeholder = this.createParameterPlaceholder(commandField.getFieldName(), this.insertContext.isNamedParameter());
            final String filedCommandName = this.getFiledCommandName(commandField, jdbcEngineConfig);
            command.append(filedCommandName).append(",");
            argsCommand.append(placeholder).append(",");
            commandContext.addCommandParameter(new CommandParameter(commandField.getFieldName(), commandField.getValue()));
        }
        if (!hasPkParam) {
            KeyGenerator keyGenerator = jdbcEngineConfig.getKeyGenerator();
            if (keyGenerator != null) {
                Object generateKeyValue = keyGenerator.generateKeyValue(modelClass);
                generateKey.setValue(generateKeyValue);
                boolean isParam = true;
                if (generateKeyValue instanceof String) {
                    isParam = !(StringUtils.startsWith((String) generateKeyValue, KeyGenerator.NATIVE_OPEN_TOKEN) && StringUtils.endsWith(((String) generateKeyValue), KeyGenerator.NATIVE_CLOSE_TOKEN));
                }
                generateKey.setParameter(isParam);
                //设置主键值，insert之后返回用
                commandContext.setGenerateKey(generateKey);
                //传参
                command.append(pkField).append(",");
                if (isParam) {
                    final String placeholder = this.createParameterPlaceholder(pkField, this.insertContext.isNamedParameter());
                    argsCommand.append(placeholder).append(",");
                    commandContext.addCommandParameter(pkField, generateKeyValue);
                } else {
                    //不传参方式，例如是oracle的序列名
                    argsCommand.append(generateKeyValue).append(",");
                }
            }
        }
        command.deleteCharAt(command.length() - 1);
        argsCommand.deleteCharAt(argsCommand.length() - 1);
        argsCommand.append(")");
        command.append(")").append(" values ").append(argsCommand);
        commandContext.setCommand(command.toString());
        return commandContext;
    }

    public static class Context extends CommandContextBuilderContext {

        private final List<CommandField> insertFields;

        public Context() {
            this.insertFields = new ArrayList<>();
        }

        public void addInsertFields(CommandField commandField) {
            this.insertFields.add(commandField);
        }

        public List<CommandField> getInsertFields() {
            return insertFields;
        }
    }
}

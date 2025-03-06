/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;

import com.sonsure.dumper.core.command.AbstractDynamicCommandDetailsBuilder;
import com.sonsure.dumper.core.command.CommandDetails;
import com.sonsure.dumper.core.command.CommandType;
import com.sonsure.dumper.core.command.GenerateKey;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import com.sonsure.dumper.core.management.ModelClassDetailsHelper;
import com.sonsure.dumper.core.management.ModelClassFieldDetails;
import com.sonsure.dumper.core.management.ModelClassWrapper;
import com.sonsure.dumper.core.management.NativeContentWrapper;
import com.sonsure.dumper.core.mapping.AbstractMappingHandler;
import com.sonsure.dumper.core.mapping.MappingHandler;
import com.sonsure.dumper.core.persist.KeyGenerator;

import java.util.Map;

/**
 * CommandContext构建
 * <p>
 *
 * @author liyd
 * @date 17/4/11
 */
public class EntityCommandDetailsBuilderImpl extends AbstractDynamicCommandDetailsBuilder<EntityCommandDetailsBuilder> implements EntityCommandDetailsBuilder {

    protected ModelClassWrapper latestModelClass;

    public EntityCommandDetailsBuilderImpl(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
    }

    @Override
    public EntityCommandDetailsBuilder from(Class<?> cls) {
        this.addClassMapping(cls);
        ModelClassWrapper modelClassWrapper = this.createModelClassWrapper(cls);
        this.commandSql.FROM(modelClassWrapper.getModelName());
        return this;
    }

    @Override
    public EntityCommandDetailsBuilder addAllColumns() {
        String[] fields = this.getLatestModelClass().getModelFields().stream()
                .map(v -> ModelClassDetailsHelper.getTableAliasFileName(this.latestTableAlias, v.getFieldName())).toArray(String[]::new);
        this.addSelectFields(fields);
        return this;
    }

    @Override
    public EntityCommandDetailsBuilder insertInto(Class<?> cls) {
        this.addClassMapping(cls);
        ModelClassWrapper modelClassWrapper = this.createModelClassWrapper(cls);
        this.commandSql.INSERT_INTO(modelClassWrapper.getModelName());
        return this;
    }

    @Override
    public EntityCommandDetailsBuilder update(Class<?> cls) {
        this.addClassMapping(cls);
        ModelClassWrapper modelClassWrapper = this.createModelClassWrapper(cls);
        this.commandSql.UPDATE(modelClassWrapper.getModelName());
        return this;
    }

    @Override
    public EntityCommandDetailsBuilder deleteFrom(Class<?> cls) {
        this.addClassMapping(cls);
        ModelClassWrapper modelClassWrapper = this.createModelClassWrapper(cls);
        this.commandSql.DELETE_FROM(modelClassWrapper.getModelName());
        return this.getSelf();
    }

    @Override
    public EntityCommandDetailsBuilder setFieldForObjectWherePk(Object object) {
        Map<String, Object> propMap = this.obj2PropMap(object, this.isIgnoreNull());
        ModelClassWrapper uniqueModelClass = this.getLatestModelClass();
        ModelClassFieldDetails pkField = uniqueModelClass.getPrimaryKeyField();
        //处理主键成where条件
        Object pkValue = propMap.get(pkField.getFieldName());
        if (pkValue == null) {
            throw new SonsureJdbcException("主键属性值不能为空:" + pkField.getFieldName());
        }
        propMap.remove(pkField.getFieldName());
        for (Map.Entry<String, Object> entry : propMap.entrySet()) {
            //不忽略null，最后构建时根据updateNull设置处理null值
            this.setField(entry.getKey(), entry.getValue());
        }
        this.where(pkField.getFieldName(), pkValue);
        return this.getSelf();
    }

    @Override
    public CommandDetails doBuild(JdbcEngineConfig jdbcEngineConfig, CommandType commandType) {
        if (CommandType.isSelectCommandType(commandType) && this.getCommandSql().isEmptySelectColumns()) {
            this.addAllColumns();
        }
        CommandDetails commandDetails = new CommandDetails();

        if (CommandType.INSERT == commandType) {
            ModelClassWrapper modelClass = this.getLatestModelClass();
            ModelClassFieldDetails primaryKeyField = modelClass.getPrimaryKeyField();
            Object primaryKeyValue = this.getCommandParameters().getParameterMap().get(primaryKeyField.getFieldName());
            GenerateKey generateKey = new GenerateKey();
            if (primaryKeyValue == null) {
                KeyGenerator keyGenerator = jdbcEngineConfig.getKeyGenerator();
                if (keyGenerator != null) {
                    Object generateKeyValue = keyGenerator.generateKeyValue(modelClass.getModelClass());
                    generateKey.setValue(generateKeyValue);
                    boolean primaryKeyParameter = true;
                    if (generateKeyValue instanceof String) {
                        NativeContentWrapper nativeContentWrapper = new NativeContentWrapper((String) generateKeyValue);
                        primaryKeyParameter = !nativeContentWrapper.isNatives();
                    }
                    generateKey.setPrimaryKeyParameter(primaryKeyParameter);
                    //主键列
                    this.getCommandSql().INTO_COLUMNS(primaryKeyField.getFieldName());
                    if (primaryKeyParameter) {
                        this.getCommandSql().INTO_VALUES(PARAM_PLACEHOLDER);
                        this.getCommandParameters().addParameter(primaryKeyField.getFieldName(), generateKeyValue);
                    } else {
                        //不传参方式，例如是oracle的序列名
                        this.getCommandSql().INTO_VALUES(generateKeyValue.toString());
                    }
                }
            } else {
                generateKey.setValue(primaryKeyValue);
                generateKey.setPrimaryKeyParameter(true);
            }
            commandDetails.setGenerateKey(generateKey);
        }

        String command = this.getCommandSql().toString();
        commandDetails.setCommand(command);
        commandDetails.setCommandParameters(this.getCommandParameters());
        commandDetails.setForceNative(this.isForceNative());
        commandDetails.setNamedParameter(false);
        commandDetails.setPagination(this.getPagination());
        commandDetails.setDisableCountQuery(this.isDisableCountQuery());
        return commandDetails;
    }

    protected ModelClassWrapper createModelClassWrapper(Class<?> cls) {
        ModelClassWrapper modelClassWrapper = new ModelClassWrapper(cls);
        this.latestModelClass = modelClassWrapper;
        return modelClassWrapper;
    }

    protected ModelClassWrapper getLatestModelClass() {
        if (this.latestModelClass == null) {
            throw new SonsureJdbcException("当前执行业务必须先指定ModelClass");
        }
        return this.latestModelClass;
    }

    protected void addClassMapping(Class<?> cls) {
        MappingHandler mappingHandler = this.getJdbcEngineConfig().getMappingHandler();
        if (mappingHandler instanceof AbstractMappingHandler) {
            ((AbstractMappingHandler) mappingHandler).addClassMapping(cls);
        }
    }
}

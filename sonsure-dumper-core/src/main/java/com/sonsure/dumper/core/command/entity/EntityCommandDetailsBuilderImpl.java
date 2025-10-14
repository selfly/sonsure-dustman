/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;

import com.sonsure.dumper.core.command.*;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import com.sonsure.dumper.core.persist.KeyGenerator;

import java.util.Map;

/**
 * CommandContext构建
 * <p>
 *
 * @author liyd
 */
public class EntityCommandDetailsBuilderImpl extends AbstractDynamicCommandDetailsBuilder<EntityCommandDetailsBuilder> implements EntityCommandDetailsBuilder {

    protected ModelClassWrapper latestModelClass;

    @Override
    public EntityCommandDetailsBuilder from(Class<?> cls) {
        ModelClassWrapper modelClassWrapper = this.mppingAndCreateModelClassWrapper(cls);
        return this.from(modelClassWrapper.getModelName());
    }

    @Override
    public EntityCommandDetailsBuilder addAllColumns() {
        String[] fields = this.getLatestModelClass().getModelFields().stream()
                .map(v -> CommandBuildHelper.getTableAliasFieldName(this.latestTableAlias, v.getFieldName())).toArray(String[]::new);
        return this.addSelectFields(fields);
    }

    @Override
    public EntityCommandDetailsBuilder insertInto(Class<?> cls) {
        ModelClassWrapper modelClassWrapper = this.mppingAndCreateModelClassWrapper(cls);
        return this.insertInto(modelClassWrapper.getModelName());
    }

    @Override
    public EntityCommandDetailsBuilder update(Class<?> cls) {
        ModelClassWrapper modelClassWrapper = this.mppingAndCreateModelClassWrapper(cls);
        return this.update(modelClassWrapper.getModelName());
    }

    @Override
    public EntityCommandDetailsBuilder deleteFrom(Class<?> cls) {
        ModelClassWrapper modelClassWrapper = this.mppingAndCreateModelClassWrapper(cls);
        return this.deleteFrom(modelClassWrapper.getModelName());
    }

    @Override
    public EntityCommandDetailsBuilder innerJoin(Class<?> cls) {
        ModelClassWrapper modelClassWrapper = this.mppingAndCreateModelClassWrapper(cls);
        return this.innerJoin(modelClassWrapper.getModelName());
    }

    @Override
    public EntityCommandDetailsBuilder setFieldForObjectWherePk(Object object) {
        Map<String, Object> propMap = CommandBuildHelper.obj2PropMap(object, this.isIgnoreNull());
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
    protected CommandDetails doBuild(JdbcEngineConfig jdbcEngineConfig, CommandType commandType) {
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
                    this.getCommandSql().intoColumns(primaryKeyField.getFieldName());
                    if (primaryKeyParameter) {
                        this.getCommandSql().intoValues(PARAM_PLACEHOLDER);
                        this.getCommandParameters().addParameter(primaryKeyField.getFieldName(), generateKeyValue);
                    } else {
                        //不传参方式，例如是oracle的序列名
                        this.getCommandSql().intoValues(generateKeyValue.toString());
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

    protected ModelClassWrapper mppingAndCreateModelClassWrapper(Class<?> cls) {
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
}

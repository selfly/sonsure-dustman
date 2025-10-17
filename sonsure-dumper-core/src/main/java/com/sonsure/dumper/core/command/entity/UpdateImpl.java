/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;

import com.sonsure.dumper.core.command.CommandBuildHelper;
import com.sonsure.dumper.core.command.ExecutionType;
import com.sonsure.dumper.core.command.ModelClassFieldDetails;
import com.sonsure.dumper.core.command.ModelClassWrapper;
import com.sonsure.dumper.core.command.build.ExecutableCmd;
import com.sonsure.dumper.core.command.lambda.Function;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.exception.SonsureJdbcException;

import java.util.Map;

/**
 * The type Update.
 *
 * @author liyd
 * @since 17 /4/14
 */
public class UpdateImpl extends AbstractConditionCommandExecutor<Update> implements Update {

    public UpdateImpl(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
    }

    @Override
    public Update table(Class<?> cls) {
        this.registerClassToMappingHandler(cls);
        this.getExecutableCmdBuilder().update(cls.getSimpleName());
        return this;
    }

    @Override
    public Update set(String field, Object value) {
        this.getExecutableCmdBuilder().set(field, value);
        return this;
    }

    @Override
    public <E, R> Update set(Function<E, R> function, Object value) {
        this.getExecutableCmdBuilder().set(function, value);
        return this;
    }

    @Override
    public Update setForBeanWherePk(Object bean) {
        Map<String, Object> propMap = CommandBuildHelper.obj2PropMap(bean, !this.getExecutableCmdBuilder().isUpdateNull());
        ModelClassWrapper uniqueModelClass = new ModelClassWrapper(bean.getClass());
        ModelClassFieldDetails pkField = uniqueModelClass.getPrimaryKeyField();
        //处理主键成where条件
        Object pkValue = propMap.get(pkField.getFieldName());
        if (pkValue == null) {
            throw new SonsureJdbcException("主键属性值不能为空:" + pkField.getFieldName());
        }
        propMap.remove(pkField.getFieldName());
        for (Map.Entry<String, Object> entry : propMap.entrySet()) {
            this.set(entry.getKey(), entry.getValue());
        }
        this.where(pkField.getFieldName(), pkValue);
        return this;
    }

    @Override
    public Update setForBean(Object bean) {
        Map<String, Object> propMap = CommandBuildHelper.obj2PropMap(bean, !this.getExecutableCmdBuilder().isUpdateNull());
        for (Map.Entry<String, Object> entry : propMap.entrySet()) {
            this.set(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public Update updateNull() {
        this.getExecutableCmdBuilder().updateNull();
        return this;
    }

    @Override
    public int execute() {
        this.getExecutableCmdBuilder().executionType(ExecutionType.UPDATE);
        this.getExecutableCmdBuilder().resultType(Integer.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return (Integer) this.getJdbcEngineConfig().getPersistExecutor().execute(executableCmd);
    }

}

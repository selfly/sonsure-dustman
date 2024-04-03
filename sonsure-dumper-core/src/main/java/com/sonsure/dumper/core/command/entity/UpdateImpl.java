/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;

import com.sonsure.dumper.common.utils.ClassUtils;
import com.sonsure.dumper.core.annotation.Transient;
import com.sonsure.dumper.core.command.AbstractCommonCommandContextBuilder;
import com.sonsure.dumper.core.command.CommandContext;
import com.sonsure.dumper.core.command.CommandType;
import com.sonsure.dumper.core.command.lambda.Function;
import com.sonsure.dumper.core.command.lambda.LambdaMethod;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.exception.SonsureJdbcException;

import java.util.Map;

/**
 * The type Update.
 *
 * @author liyd
 * @date 17 /4/14
 */
public class UpdateImpl extends AbstractConditionCommandExecutor<Update> implements Update {

    private final UpdateCommandContextBuilderImpl updateCommandContextBuilder;

    public UpdateImpl(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
        this.updateCommandContextBuilder = new UpdateCommandContextBuilderImpl(new UpdateCommandContextBuilderImpl.Context());
    }

    @Override
    protected ConditionCommandBuilderImpl getConditionCommandBuilder() {
        return this.updateCommandContextBuilder.getConditionCommandBuilder();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends AbstractCommonCommandContextBuilder> T getCommandContextBuilder() {
        return (T) updateCommandContextBuilder;
    }

    @Override
    public Update table(Class<?> cls) {
        this.updateCommandContextBuilder.addModelClass(cls);
        return this;
    }

    @Override
    public Update set(String field, Object value) {
        this.updateCommandContextBuilder.addSetField(field, value);
        return this;
    }

    @Override
    public <E, R> Update set(Function<E, R> function, Object value) {
        String field = LambdaMethod.getField(function);
        updateCommandContextBuilder.addSetField(field, value);
        return this;
    }

    @Override
    public Update setForEntityWhereId(Object entity) {
        updateCommandContextBuilder.addModelClass(entity.getClass());
        String pkField = getJdbcEngineConfig().getMappingHandler().getPkField(entity.getClass());
        Map<String, Object> beanPropMap = ClassUtils.getSelfBeanPropMap(entity, Transient.class);
        //处理主键成where条件
        Object pkValue = beanPropMap.get(pkField);
        if (pkValue == null) {
            throw new SonsureJdbcException("主键属性值不能为空:" + pkField);
        }
        this.where(pkField, pkValue);
        //移除
        beanPropMap.remove(pkField);

        for (Map.Entry<String, Object> entry : beanPropMap.entrySet()) {
            //不忽略null，最后构建时根据updateNull设置处理null值
            updateCommandContextBuilder.addSetField(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public Update setForEntity(Object entity) {
        Map<String, Object> beanPropMap = ClassUtils.getSelfBeanPropMap(entity, Transient.class);
        for (Map.Entry<String, Object> entry : beanPropMap.entrySet()) {
            //不忽略null，最后构建时根据updateNull设置处理null值
            updateCommandContextBuilder.addSetField(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public Update updateNull() {
        updateCommandContextBuilder.setIgnoreNull(false);
        return this;
    }

    @Override
    public int execute() {
        CommandContext commandContext = this.updateCommandContextBuilder.build(getJdbcEngineConfig());
        return (Integer) this.getJdbcEngineConfig().getPersistExecutor().execute(commandContext, CommandType.UPDATE);
    }

}

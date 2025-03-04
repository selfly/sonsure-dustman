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
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import com.sonsure.dumper.core.management.ModelClassFieldDetails;
import com.sonsure.dumper.core.management.ModelClassWrapper;
import com.sonsure.dumper.core.mapping.AbstractMappingHandler;
import com.sonsure.dumper.core.mapping.MappingHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CommandContext构建
 * <p>
 *
 * @author liyd
 * @date 17/4/11
 */
public class EntityCommandDetailsBuilderImpl extends AbstractDynamicCommandDetailsBuilder<EntityCommandDetailsBuilder> implements EntityCommandDetailsBuilder {

    protected final List<ModelClassWrapper> modelClassList;

    public EntityCommandDetailsBuilderImpl(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
        this.modelClassList = new ArrayList<>(8);
    }

    @Override
    public EntityCommandDetailsBuilder from(Class<?> cls) {
        this.addClassMapping(cls);
        ModelClassWrapper modelClassWrapper = this.createModelClassWrapper(cls);
        String[] fields = modelClassWrapper.getModelFields().stream()
                .map(ModelClassFieldDetails::getFieldName).toArray(String[]::new);
        this.commandSql.SELECT(fields);
        this.commandSql.FROM(modelClassWrapper.getModelName());
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
        Map<String, Object> propMap = this.obj2PropMap(object);
        ModelClassWrapper uniqueModelClass = this.getUniqueModelClass();
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


    protected ModelClassWrapper createModelClassWrapper(Class<?> cls) {
        ModelClassWrapper modelClassWrapper = new ModelClassWrapper(cls);
        this.modelClassList.add(modelClassWrapper);
        return modelClassWrapper;
    }

    protected ModelClassWrapper getUniqueModelClass() {
        if (this.modelClassList.size() != 1) {
            throw new SonsureJdbcException("当前执行业务必须且只能有一个Model Class");
        }
        return this.modelClassList.iterator().next();
    }

    protected void addClassMapping(Class<?> cls) {
        MappingHandler mappingHandler = this.getJdbcEngineConfig().getMappingHandler();
        if (mappingHandler instanceof AbstractMappingHandler) {
            ((AbstractMappingHandler) mappingHandler).addClassMapping(cls);
        }
    }
}

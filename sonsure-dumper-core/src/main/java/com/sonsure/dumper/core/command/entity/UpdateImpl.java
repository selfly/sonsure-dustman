/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;

import com.sonsure.dumper.core.command.CommandDetails;
import com.sonsure.dumper.core.command.CommandType;
import com.sonsure.dumper.core.command.lambda.Function;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

/**
 * The type Update.
 *
 * @author liyd
 * @date 17 /4/14
 */
public class UpdateImpl extends AbstractConditionCommandExecutor<Update> implements Update {

    public UpdateImpl(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
    }

    @Override
    public Update table(Class<?> cls) {
        this.getEntityCommandDetailsBuilder().update(cls);
        return this;
    }

    @Override
    public Update set(String field, Object value) {
        this.getEntityCommandDetailsBuilder().setField(field, value);
        return this;
    }

    @Override
    public <E, R> Update set(Function<E, R> function, Object value) {
        this.getEntityCommandDetailsBuilder().setField(function, value);
        return this;
    }

    @Override
    public Update setForObjectWherePk(Object object) {
        this.getEntityCommandDetailsBuilder().setFieldForObjectWherePk(object);
        return this;
    }

    @Override
    public Update setForObject(Object object) {
        this.getEntityCommandDetailsBuilder().setFieldForObject(object);
        return this;
    }

    @Override
    public Update updateNull(boolean updateNull) {
        this.getEntityCommandDetailsBuilder().updateNull(updateNull);
        return this;
    }

    @Override
    public int execute() {
        CommandDetails commandDetails = this.getCommandDetailsBuilder().build(getJdbcEngineConfig(), CommandType.UPDATE);
        commandDetails.setResultType(Integer.class);
        return (Integer) this.getJdbcEngineConfig().getPersistExecutor().execute(commandDetails);
    }

}

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
        this.getCommandDetailsBuilder().update(cls);
        return this;
    }

    @Override
    public Update set(String field, Object value) {
        this.getCommandDetailsBuilder().setField(field, value);
        return this;
    }

    @Override
    public <E, R> Update set(Function<E, R> function, Object value) {
        this.getCommandDetailsBuilder().setField(function, value);
        return this;
    }

    @Override
    public Update setForObjectWherePk(Object object) {
        this.getCommandDetailsBuilder().setFieldForObjectWherePk(object);
        return this;
    }

    @Override
    public Update setForObject(Object object) {
        this.getCommandDetailsBuilder().setFieldForObject(object);
        return this;
    }

    @Override
    public Update updateNull() {
        this.getCommandDetailsBuilder().updateNull();
        return this;
    }

    @Override
    public int execute() {
        CommandDetails commandDetails = this.getCommandDetailsBuilder().build(getJdbcEngineConfig());
        return (Integer) this.getJdbcEngineConfig().getPersistExecutor().execute(commandDetails, CommandType.UPDATE);
    }

}

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
 * @author liyd
 * @date 17/4/14
 */
public class InsertImpl extends AbstractEntityCommandExecutor<Insert> implements Insert {

    public InsertImpl(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
    }

    @Override
    public Insert into(Class<?> cls) {
        this.getEntityCommandDetailsBuilder().insertInto(cls);
        return this;
    }

    @Override
    public Insert set(String field, Object value) {
        this.getEntityCommandDetailsBuilder().intoField(field, value);
        return this;
    }

    @Override
    public <E, R> Insert set(Function<E, R> function, Object value) {
        this.getEntityCommandDetailsBuilder().intoField(function, value);
        return this;
    }

    @Override
    public Insert setForObject(Object obj) {
        this.getEntityCommandDetailsBuilder().intoFieldForObject(obj);
        return this;
    }

    @Override
    public Object execute() {
        CommandDetails commandDetails = this.getEntityCommandDetailsBuilder().build(getJdbcEngineConfig(), CommandType.INSERT);
        commandDetails.setResultType(Object.class);
        return getJdbcEngineConfig().getPersistExecutor().execute(commandDetails);
    }

}

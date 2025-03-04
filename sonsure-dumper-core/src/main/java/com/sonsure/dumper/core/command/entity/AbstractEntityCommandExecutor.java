/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   https://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;

import com.sonsure.dumper.core.command.AbstractCommonCommandExecutor;
import com.sonsure.dumper.core.command.CommandDetailsBuilder;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import lombok.Getter;

/**
 * @author liyd
 */
@Getter
public abstract class AbstractEntityCommandExecutor<T extends EntityCommandExecutor<T>> extends AbstractCommonCommandExecutor<T> implements EntityCommandExecutor<T> {

    private final EntityCommandDetailsBuilder entityCommandDetailsBuilder;

    public AbstractEntityCommandExecutor(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
        this.entityCommandDetailsBuilder = new EntityCommandDetailsBuilderImpl(jdbcEngineConfig);
    }

    @Override
    protected <B extends CommandDetailsBuilder<B>> B getCommandDetailsBuilder() {
        //noinspection unchecked
        return (B) entityCommandDetailsBuilder;
    }
}

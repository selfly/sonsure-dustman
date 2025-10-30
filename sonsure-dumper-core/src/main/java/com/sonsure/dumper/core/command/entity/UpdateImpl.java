/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;

import com.sonsure.dumper.core.command.build.CommandBuildHelper;
import com.sonsure.dumper.core.command.build.ExecutionType;
import com.sonsure.dumper.core.command.build.CacheEntityClassWrapper;
import com.sonsure.dumper.core.command.build.ExecutableCmd;
import com.sonsure.dumper.core.command.build.GetterFunction;
import com.sonsure.dumper.core.config.JdbcExecutorConfig;

import java.util.Map;

/**
 * The type Update.
 *
 * @author liyd
 * @since 17 /4/14
 */
public class UpdateImpl extends AbstractConditionCommandExecutor<Update> implements Update {

    public UpdateImpl(JdbcExecutorConfig jdbcExecutorConfig) {
        super(jdbcExecutorConfig);
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
    public <T> Update set(GetterFunction<T> getter, Object value) {
        return this.set(lambda2Field(getter), value);
    }

    @Override
    public Update setForBean(Object bean) {
        CacheEntityClassWrapper cacheEntityClassWrapper = new CacheEntityClassWrapper(bean.getClass());
        Map<String, Object> propMap = CommandBuildHelper.obj2PropMap(bean, this.getExecutableCmdBuilder().isUpdateNull());
        for (Map.Entry<String, Object> entry : propMap.entrySet()) {
            // 主键不更新
            if (entry.getKey().equals(cacheEntityClassWrapper.getPrimaryKeyField().getFieldName())) {
                continue;
            }
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
        return (Integer) this.getJdbcExecutorConfig().getPersistExecutor().execute(executableCmd);
    }

}

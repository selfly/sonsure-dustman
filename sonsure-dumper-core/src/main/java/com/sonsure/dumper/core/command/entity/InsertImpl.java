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
import com.sonsure.dumper.core.command.build.ExecutableCmd;
import com.sonsure.dumper.core.command.build.ExecutableCmdBuilder;
import com.sonsure.dumper.core.command.build.ExecutableCustomizer;
import com.sonsure.dumper.core.command.lambda.Function;
import com.sonsure.dumper.core.command.lambda.LambdaHelper;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.persist.KeyGenerator;

import java.util.Map;

/**
 * @author liyd
 * @since 17/4/14
 */
public class InsertImpl extends AbstractCommandExecutor<Insert> implements Insert {

    public InsertImpl(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
    }

    @Override
    public Insert into(Class<?> cls) {
        this.registerClassToMappingHandler(cls);
        this.getExecutableCmdBuilder().insertInto(cls.getSimpleName())
                .addCustomizer(new InsertExecutableCustomizer(getJdbcEngineConfig(), cls));
        return this;
    }

    @Override
    public Insert intoField(String field, Object value) {
        this.getExecutableCmdBuilder().intoColumns(field);
        this.getExecutableCmdBuilder().intoValues(value);
        return this;
    }

    @Override
    public <E, R> Insert intoField(Function<E, R> function, Object value) {
        String fieldName = LambdaHelper.getFieldName(function);
        return this.intoField(fieldName, value);
    }

    @Override
    public Insert intoForObject(Object obj) {
        Map<String, Object> propMap = CommandBuildHelper.obj2PropMap(obj, !this.getExecutableCmdBuilder().isUpdateNull());
        for (Map.Entry<String, Object> entry : propMap.entrySet()) {
            //忽略掉null
            if (entry.getValue() == null) {
                continue;
            }
            this.intoField(entry.getKey(), entry.getValue());
        }
        return this.getSelf();
    }

    @Override
    public Object execute() {
        this.getExecutableCmdBuilder().executionType(ExecutionType.INSERT);
        this.getExecutableCmdBuilder().resultType(Object.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return getJdbcEngineConfig().getPersistExecutor().execute(executableCmd);
    }

    private static class InsertExecutableCustomizer implements ExecutableCustomizer {

        private final JdbcEngineConfig jdbcEngineConfig;
        private final Class<?> cls;

        public InsertExecutableCustomizer(JdbcEngineConfig jdbcEngineConfig, Class<?> cls) {
            this.jdbcEngineConfig = jdbcEngineConfig;
            this.cls = cls;
        }

        @Override
        public void customize(ExecutableCmdBuilder executableCmdBuilder) {
            if (this.cls == null) {
                return;
            }
            ModelClassWrapper modelClass = new ModelClassWrapper(this.cls);
            ModelClassFieldDetails primaryKeyField = modelClass.getPrimaryKeyField();
            Object primaryKeyValue = executableCmdBuilder.getParameterMap().get(primaryKeyField.getFieldName());
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
                    executableCmdBuilder.intoColumns(primaryKeyField.getFieldName());
                    if (primaryKeyParameter) {
                        executableCmdBuilder.intoValues(ExecutableCmdBuilder.PARAM_PLACEHOLDER);
                        executableCmdBuilder.addParameter(primaryKeyField.getFieldName(), generateKeyValue);
                    } else {
                        //不传参方式，例如是oracle的序列名
                        executableCmdBuilder.intoValues(generateKeyValue.toString());
                    }
                }
            } else {
                generateKey.setValue(primaryKeyValue);
                generateKey.setPrimaryKeyParameter(true);
            }
            executableCmdBuilder.generateKey(generateKey);
        }
    }
}

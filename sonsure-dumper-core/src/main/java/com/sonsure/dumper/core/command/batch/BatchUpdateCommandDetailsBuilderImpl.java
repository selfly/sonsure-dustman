/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.batch;

import com.sonsure.dumper.core.command.AbstractCommonCommandDetailsBuilder;
import com.sonsure.dumper.core.command.CommandContextBuilderContext;
import com.sonsure.dumper.core.command.CommandDetails;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

import java.util.Collection;

/**
 * The type Batch update command context builder.
 *
 * @author liyd
 */
public class BatchUpdateCommandDetailsBuilderImpl extends AbstractCommonCommandDetailsBuilder {

    private final Context batchBuilderContext;

    public BatchUpdateCommandDetailsBuilderImpl(Context batchBuilderContext) {
        super(batchBuilderContext);
        this.batchBuilderContext = batchBuilderContext;
    }

    public void command(String command) {
        this.batchBuilderContext.setCommand(command);
    }

    public void batchSize(int batchSize) {
        this.batchBuilderContext.setBatchSize(batchSize);
    }

    public <T> void batchData(Collection<T> batchData) {
        this.batchBuilderContext.setBatchData(batchData);
    }

    public <T> void parameterizedSetter(ParameterizedSetter<T> parameterizedSetter) {
        this.batchBuilderContext.setParameterizedSetter(parameterizedSetter);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public CommandDetails doBuild(JdbcEngineConfig jdbcEngineConfig) {
        BatchCommandDetails batchCommandContext = new BatchCommandDetails();
        batchCommandContext.setCommand(this.batchBuilderContext.getCommand());
        batchCommandContext.setBatchData(this.batchBuilderContext.getBatchData());
        batchCommandContext.setBatchSize(this.batchBuilderContext.getBatchSize());
        batchCommandContext.setParameterizedSetter(this.batchBuilderContext.getParameterizedSetter());
        return batchCommandContext;
    }

    public static class Context extends CommandContextBuilderContext {

        private String command;


        /**
         * The Batch data.
         */
        private Collection<?> batchData;

        /**
         * The Batch size.
         */
        private int batchSize;

        /**
         * The Parameterized setter.
         */
        private ParameterizedSetter<?> parameterizedSetter;

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public void setBatchData(Collection<?> batchData) {
            this.batchData = batchData;
        }

        public void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }

        public void setParameterizedSetter(ParameterizedSetter<?> parameterizedSetter) {
            this.parameterizedSetter = parameterizedSetter;
        }

        public Collection<?> getBatchData() {
            return batchData;
        }

        public int getBatchSize() {
            return batchSize;
        }

        public ParameterizedSetter<?> getParameterizedSetter() {
            return parameterizedSetter;
        }
    }

}

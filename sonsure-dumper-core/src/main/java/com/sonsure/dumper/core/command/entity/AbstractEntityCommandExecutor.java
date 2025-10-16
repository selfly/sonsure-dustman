///*
// * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
// * You may obtain more information at
// *
// *   https://www.sonsure.com
// *
// * Designed By Selfly Lee (selfly@live.com)
// */
//
//package com.sonsure.dumper.core.command.entity;
//
//import com.sonsure.dumper.core.command.AbstractCommandExecutor;
//import com.sonsure.dumper.core.command.CommandDetailsBuilder;
//import com.sonsure.dumper.core.config.JdbcEngineConfig;
//import com.sonsure.dumper.core.mapping.AbstractMappingHandler;
//import com.sonsure.dumper.core.mapping.MappingHandler;
//import lombok.Getter;
//
///**
// * @author liyd
// */
//@Getter
//public abstract class AbstractEntityCommandExecutor<T extends EntityCommandExecutor<T>> extends AbstractCommandExecutor<T> implements EntityCommandExecutor<T> {
//
//    private final EntityCommandDetailsBuilder entityCommandDetailsBuilder;
//
//    public AbstractEntityCommandExecutor(JdbcEngineConfig jdbcEngineConfig) {
//        super(jdbcEngineConfig);
//        this.entityCommandDetailsBuilder = new EntityCommandDetailsBuilderImpl();
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    protected <B extends CommandDetailsBuilder<B>> B getCommandDetailsBuilder() {
//        //noinspection unchecked
//        return (B) entityCommandDetailsBuilder;
//    }
//
//    protected void registerClassToMappingHandler(Class<?> cls) {
//        MappingHandler mappingHandler = this.getJdbcEngineConfig().getMappingHandler();
//        if (mappingHandler instanceof AbstractMappingHandler) {
//            ((AbstractMappingHandler) mappingHandler).addClassMapping(cls);
//        }
//    }
//}

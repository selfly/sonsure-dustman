///*
// * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
// * You may obtain more information at
// *
// *   http://www.sonsure.com
// *
// * Designed By Selfly Lee (selfly@live.com)
// */
//
//package com.sonsure.dumper.springjdbc.config;
//
//import com.sonsure.dumper.core.config.JdbcContext;
//import com.sonsure.dumper.core.config.JdbcContextImpl;
//import org.springframework.beans.factory.FactoryBean;
//
///**
// * @author liyd
// */
//public class JdbcTemplateExecutorFactoryBean extends JdbcTemplateExecutorConfigImpl implements FactoryBean<JdbcContext> {
//
//    @Override
//    public JdbcContext getObject() throws Exception {
//        return new JdbcContextImpl(this);
//    }
//
//    @Override
//    public Class<?> getObjectType() {
//        return JdbcContext.class;
//    }
//
//}

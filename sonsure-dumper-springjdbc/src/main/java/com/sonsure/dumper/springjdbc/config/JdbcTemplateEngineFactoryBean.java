/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.springjdbc.config;

import com.sonsure.dumper.core.config.JdbcEngine;
import com.sonsure.dumper.core.config.JdbcEngineImpl;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author liyd
 */
public class JdbcTemplateEngineFactoryBean extends JdbcTemplateEngineConfigImpl implements FactoryBean<JdbcEngine> {

    @Override
    public JdbcEngine getObject() throws Exception {
        return new JdbcEngineImpl(this);
    }

    @Override
    public Class<?> getObjectType() {
        return JdbcEngine.class;
    }

}

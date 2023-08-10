/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.springjdbc.config;

import com.sonsure.dumper.core.config.AbstractJdbcEngineConfig;
import com.sonsure.dumper.core.persist.PersistExecutor;
import com.sonsure.dumper.springjdbc.persist.JdbcTemplatePersistExecutor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author liyd
 * @date 17/4/12
 */
public class JdbcTemplateEngineConfigImpl extends AbstractJdbcEngineConfig {

    private JdbcOperations jdbcOperations;

    @Override
    protected PersistExecutor initPersistExecutor() {
        if (jdbcOperations == null) {
            jdbcOperations = new JdbcTemplate(dataSource);
        }
        return new JdbcTemplatePersistExecutor(this.jdbcOperations, this);
    }

    public void setJdbcOperations(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public JdbcOperations getJdbcOperations() {
        return jdbcOperations;
    }
}

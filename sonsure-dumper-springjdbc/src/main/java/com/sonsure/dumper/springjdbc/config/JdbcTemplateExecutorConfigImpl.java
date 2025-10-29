/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.springjdbc.config;

import com.sonsure.dumper.core.config.AbstractJdbcExecutorConfig;
import com.sonsure.dumper.core.persist.PersistExecutor;
import com.sonsure.dumper.springjdbc.persist.JdbcTemplatePersistExecutor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author liyd
 * @since 17/4/12
 */
@Setter
@Getter
public class JdbcTemplateExecutorConfigImpl extends AbstractJdbcExecutorConfig {

    private JdbcOperations jdbcOperations;

    @Override
    protected PersistExecutor initPersistExecutor() {
        if (jdbcOperations == null) {
            jdbcOperations = new JdbcTemplate(dataSource);
        }
        return new JdbcTemplatePersistExecutor(this, this.jdbcOperations);
    }

}

/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.springjdbc.persist;


import com.sonsure.dumper.core.config.JdbcEngineImpl;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import com.sonsure.dumper.core.persist.AbstractJdbcDaoImpl;
import com.sonsure.dumper.springjdbc.config.JdbcTemplateEngineConfigImpl;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author liyd
 * @since 17/4/12
 */
public class SpringJdbcTemplateDaoImpl extends AbstractJdbcDaoImpl implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        if (defaultJdbcEngine == null) {
            if (dataSource == null) {
                throw new SonsureJdbcException("defaultJdbcEngine和dataSource不能同时为空");
            }
            JdbcTemplateEngineConfigImpl jdbcTemplateEngineConfig = new JdbcTemplateEngineConfigImpl();
            jdbcTemplateEngineConfig.setDataSource(getDataSource());
            defaultJdbcEngine = new JdbcEngineImpl(jdbcTemplateEngineConfig);
        }
    }
}

package com.sonsure.dumper.test.config;

import com.sonsure.dumper.core.config.JdbcEngine;
import com.sonsure.dumper.core.mapping.MappingHandler;
import com.sonsure.dumper.core.persist.JdbcDao;
import com.sonsure.dumper.core.persist.KeyGenerator;
import com.sonsure.dumper.core.persist.OracleKeyGenerator;
import com.sonsure.dumper.springjdbc.config.JdbcTemplateEngineFactoryBean;
import com.sonsure.dumper.springjdbc.persist.SpringJdbcDaoTemplateImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("oracle")
public class OracleTestConfig {

    @Bean
    public KeyGenerator oracleKeyGenerator() {
        return new OracleKeyGenerator();
    }

    @Bean
    public JdbcTemplateEngineFactoryBean oracleJdbcTemplateEngine(DataSource dataSource, MappingHandler mappingHandler) {
        JdbcTemplateEngineFactoryBean jdbcTemplateEngineFactoryBean = new JdbcTemplateEngineFactoryBean();
        jdbcTemplateEngineFactoryBean.setDataSource(dataSource);
        jdbcTemplateEngineFactoryBean.setMappingHandler(mappingHandler);
        jdbcTemplateEngineFactoryBean.setKeyGenerator(oracleKeyGenerator());
        return jdbcTemplateEngineFactoryBean;
    }

    @Bean
    public JdbcDao oracleJdbcDao(@Qualifier("oracleJdbcTemplateEngine") JdbcEngine jdbcEngine) {
        SpringJdbcDaoTemplateImpl jdbcDaoTemplate = new SpringJdbcDaoTemplateImpl();
        jdbcDaoTemplate.setDefaultJdbcEngine(jdbcEngine);
        return jdbcDaoTemplate;
    }
}

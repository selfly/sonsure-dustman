package com.sonsure.dustman.test.config;

import com.sonsure.dustman.jdbc.config.JdbcContext;
import com.sonsure.dustman.jdbc.config.JdbcContextImpl;
import com.sonsure.dustman.jdbc.mapping.MappingHandler;
import com.sonsure.dustman.jdbc.persist.JdbcDao;
import com.sonsure.dustman.jdbc.persist.JdbcDaoImpl;
import com.sonsure.dustman.jdbc.persist.KeyGenerator;
import com.sonsure.dustman.jdbc.persist.OracleKeyGenerator;
import com.sonsure.dustman.springjdbc.persist.JdbcTemplatePersistExecutor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@Profile("oracle")
public class OracleTestConfig {

    @Bean
    public KeyGenerator oracleKeyGenerator() {
        return new OracleKeyGenerator();
    }

    @Bean
    public JdbcContext oracleJdbcTemplate(DataSource dataSource, MappingHandler mappingHandler) {
        JdbcContextImpl jdbcContext = new JdbcContextImpl();
        jdbcContext.setMappingHandler(mappingHandler);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcContext.setPersistExecutor(new JdbcTemplatePersistExecutor(jdbcTemplate));
        return jdbcContext;
    }

    @Bean
    public JdbcDao oracleJdbcDao(@Qualifier("oracleJdbcTemplate") JdbcContext jdbcContext) {
        JdbcDaoImpl jdbcDao = new JdbcDaoImpl();
        jdbcDao.setJdbcContext(jdbcContext);
        return jdbcDao;
    }
}

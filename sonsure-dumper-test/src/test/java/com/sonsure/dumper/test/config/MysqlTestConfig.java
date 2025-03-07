package com.sonsure.dumper.test.config;

import com.sonsure.dumper.core.config.JdbcEngine;
import com.sonsure.dumper.core.mapping.MappingHandler;
import com.sonsure.dumper.core.persist.JdbcDao;
import com.sonsure.dumper.springjdbc.config.JdbcTemplateEngineFactoryBean;
import com.sonsure.dumper.springjdbc.persist.SpringJdbcTemplateDaoImpl;
import com.sonsure.dumper.test.flyable.FlyableInitializer;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class MysqlTestConfig {

    @Bean
    public JdbcTemplateEngineFactoryBean jdbcTemplateEngine(DataSource dataSource, MappingHandler mappingHandler, SqlSessionFactory sqlSessionFactory) {
        JdbcTemplateEngineFactoryBean jdbcTemplateEngineFactoryBean = new JdbcTemplateEngineFactoryBean();
        jdbcTemplateEngineFactoryBean.setDataSource(dataSource);
        jdbcTemplateEngineFactoryBean.setMappingHandler(mappingHandler);
        jdbcTemplateEngineFactoryBean.setMybatisSqlSessionFactory(sqlSessionFactory);
        return jdbcTemplateEngineFactoryBean;
    }

    @Bean
    public JdbcDao mysqlJdbcDao(@Qualifier("jdbcTemplateEngine") JdbcEngine jdbcEngine) {
        SpringJdbcTemplateDaoImpl jdbcDao = new SpringJdbcTemplateDaoImpl();
        jdbcDao.setDefaultJdbcEngine(jdbcEngine);
        return jdbcDao;
    }

    @Bean
    public FlyableInitializer flyableInitializer(@Qualifier("mysqlJdbcDao") JdbcDao jdbcDao) {
        return new FlyableInitializer(jdbcDao);
    }
}

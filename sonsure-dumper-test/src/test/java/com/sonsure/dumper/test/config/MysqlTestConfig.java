package com.sonsure.dumper.test.config;

import com.sonsure.dumper.core.config.JdbcExecutor;
import com.sonsure.dumper.core.mapping.MappingHandler;
import com.sonsure.dumper.core.persist.JdbcDao;
import com.sonsure.dumper.springjdbc.config.JdbcTemplateExecutorFactoryBean;
import com.sonsure.dumper.springjdbc.persist.SpringJdbcTemplateDaoImpl;
import com.sonsure.dumper.test.flyable.FlyableInitializer;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Collections;

@Configuration
public class MysqlTestConfig {


    @Bean
    public JdbcTemplateExecutorFactoryBean jdbcTemplateExecutor(DataSource dataSource, MappingHandler mappingHandler, SqlSessionFactory sqlSessionFactory) {
        JdbcTemplateExecutorFactoryBean jdbcTemplateEngineFactoryBean = new JdbcTemplateExecutorFactoryBean();
        jdbcTemplateEngineFactoryBean.setDataSource(dataSource);
        jdbcTemplateEngineFactoryBean.setMappingHandler(mappingHandler);
        jdbcTemplateEngineFactoryBean.setMybatisSqlSessionFactory(sqlSessionFactory);
        jdbcTemplateEngineFactoryBean.setPersistInterceptors(Collections.singletonList(new DumperTestConfig.TestInterceptor()));
        return jdbcTemplateEngineFactoryBean;
    }

    @Bean
    public JdbcDao mysqlJdbcDao(@Qualifier("jdbcTemplateExecutor") JdbcExecutor jdbcExecutor) {
        SpringJdbcTemplateDaoImpl jdbcDao = new SpringJdbcTemplateDaoImpl();
        jdbcDao.setDefaultJdbcExecutor(jdbcExecutor);
        return jdbcDao;
    }

    @Bean
    public FlyableInitializer flyableInitializer(@Qualifier("mysqlJdbcDao") JdbcDao jdbcDao) {
        return new FlyableInitializer(jdbcDao);
    }

}

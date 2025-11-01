package com.sonsure.dumper.test.config;

import com.sonsure.dumper.core.config.JdbcContext;
import com.sonsure.dumper.core.config.JdbcContextImpl;
import com.sonsure.dumper.core.mapping.MappingHandler;
import com.sonsure.dumper.core.persist.JdbcDao;
import com.sonsure.dumper.springjdbc.persist.JdbcTemplatePersistExecutor;
import com.sonsure.dumper.springjdbc.persist.SpringJdbcTemplateDaoImpl;
import com.sonsure.dumper.test.flyable.FlyableInitializer;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class MysqlTestConfig {


//    @Bean
//    public JdbcTemplateExecutorFactoryBean jdbcTemplateExecutor(DataSource dataSource, MappingHandler mappingHandler, SqlSessionFactory sqlSessionFactory) {
//        JdbcTemplateExecutorFactoryBean jdbcTemplateEngineFactoryBean = new JdbcTemplateExecutorFactoryBean();
//        jdbcTemplateEngineFactoryBean.setDataSource(dataSource);
//        jdbcTemplateEngineFactoryBean.setMappingHandler(mappingHandler);
//        jdbcTemplateEngineFactoryBean.setMybatisSqlSessionFactory(sqlSessionFactory);
//        jdbcTemplateEngineFactoryBean.setPersistInterceptors(Collections.singletonList(new DumperTestConfig.TestInterceptor()));
//        return jdbcTemplateEngineFactoryBean;
//    }

    @Bean
    public JdbcContext jdbcTemplateContext(DataSource dataSource, MappingHandler mappingHandler, SqlSessionFactory sqlSessionFactory) {
        JdbcContextImpl jdbcContext = new JdbcContextImpl();
        jdbcContext.setDataSource(dataSource);
        jdbcContext.setMappingHandler(mappingHandler);
        jdbcContext.setMybatisSqlSessionFactory(sqlSessionFactory);
        jdbcContext.setPersistExecutor(new JdbcTemplatePersistExecutor(jdbcContext, new JdbcTemplate(dataSource)));
        return jdbcContext;
    }

    @Bean
    public JdbcDao mysqlJdbcDao(@Qualifier("jdbcTemplateContext") JdbcContext jdbcContext) {
        SpringJdbcTemplateDaoImpl jdbcDao = new SpringJdbcTemplateDaoImpl();
        jdbcDao.setJdbcContext(jdbcContext);
        return jdbcDao;
    }

    @Bean
    public FlyableInitializer flyableInitializer(@Qualifier("mysqlJdbcDao") JdbcDao jdbcDao) {
        return new FlyableInitializer(jdbcDao);
    }

}

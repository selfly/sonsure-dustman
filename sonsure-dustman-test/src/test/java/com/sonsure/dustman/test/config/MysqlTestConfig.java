package com.sonsure.dustman.test.config;

import com.sonsure.dustman.jdbc.config.JdbcContext;
import com.sonsure.dustman.jdbc.config.JdbcContextImpl;
import com.sonsure.dustman.jdbc.mapping.MappingHandler;
import com.sonsure.dustman.jdbc.persist.JdbcDao;
import com.sonsure.dustman.jdbc.persist.JdbcDaoImpl;
import com.sonsure.dustman.springjdbc.persist.JdbcTemplatePersistExecutor;
import com.sonsure.dustman.test.flyable.FlyableInitializer;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
public class MysqlTestConfig {

    @Bean
    public JdbcContext jdbcTemplateContext(DataSource dataSource, MappingHandler mappingHandler, SqlSessionFactory sqlSessionFactory) {
        JdbcContextImpl jdbcContext = new JdbcContextImpl();
        jdbcContext.setMappingHandler(mappingHandler);
        jdbcContext.setMybatisSqlSessionFactory(sqlSessionFactory);
        jdbcContext.setPersistInterceptors(Arrays.asList(new DustmanTestConfig.TestBeforeInterceptor(), new DustmanTestConfig.TestAfterInterceptor()));
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcContext.setPersistExecutor(new JdbcTemplatePersistExecutor(jdbcTemplate));
        return jdbcContext;
    }

    @Bean
    public JdbcDao mysqlJdbcDao(@Qualifier("jdbcTemplateContext") JdbcContext jdbcContext) {
        JdbcDaoImpl jdbcDao = new JdbcDaoImpl();
        jdbcDao.setJdbcContext(jdbcContext);
        return jdbcDao;
    }

    @Bean
    public FlyableInitializer flyableInitializer(@Qualifier("mysqlJdbcDao") JdbcDao jdbcDao) {
        return new FlyableInitializer(jdbcDao);
    }

}

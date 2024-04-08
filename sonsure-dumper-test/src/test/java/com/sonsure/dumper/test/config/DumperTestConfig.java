package com.sonsure.dumper.test.config;

import com.sonsure.dumper.core.mapping.DefaultMappingHandler;
import com.sonsure.dumper.core.mapping.MappingHandler;
import com.sonsure.dumper.flyable.FlyableHistory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
public class DumperTestConfig {

    @Bean
    public MappingHandler defaultMappingHandler() {
        DefaultMappingHandler defaultMappingHandler = new DefaultMappingHandler("com.sonsure.dumper.test.model.**");
        defaultMappingHandler.addTablePrefix("sd_", FlyableHistory.class.getPackage().getName(), "com.sonsure.dumper.test.model");
        return defaultMappingHandler;
    }

    @Bean
    public SqlSessionFactoryBean mybatisSqlSessionFactoryBean(DataSource dataSource) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setConfigLocation(new ClassPathResource("mybatis/mybatis-config.xml"));
        sqlSessionFactoryBean.setDataSource(dataSource);
        return sqlSessionFactoryBean;
    }

}

package com.sonsure.dumper.test.config;

import com.sonsure.dumper.core.interceptor.InterceptorChain;
import com.sonsure.dumper.core.interceptor.PersistContext;
import com.sonsure.dumper.core.interceptor.PersistInterceptor;
import com.sonsure.dumper.core.mapping.DefaultMappingHandler;
import com.sonsure.dumper.core.mapping.MappingHandler;
import com.sonsure.dumper.flyable.FlyableHistory;
import com.sonsure.dumper.test.model.UserInfo;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.util.Collections;

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

    public static class TestInterceptor implements PersistInterceptor {

        public static final String SQL = "select loginName, pwd from user_info";

        @Override
        public void executeBefore(PersistContext persistContext, InterceptorChain chain) {
            PersistInterceptor.super.executeBefore(persistContext, chain);
            if (SQL.equalsIgnoreCase(persistContext.getExecutableCmd().getCommand())) {
                UserInfo userInfo = new UserInfo();
                userInfo.setLoginName("interceptorUser");
                persistContext.setResult(Collections.singletonList(userInfo));
                persistContext.setSkipExecution(true);
            }
            chain.doBefore(persistContext);
        }

        @Override
        public void executeAfter(PersistContext persistContext, InterceptorChain chain) {
            PersistInterceptor.super.executeAfter(persistContext, chain);
            chain.doAfter(persistContext);
        }
    }

}

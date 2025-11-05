package com.sonsure.dustman.test.config;

import com.sonsure.dustman.jdbc.interceptor.InterceptorChain;
import com.sonsure.dustman.jdbc.interceptor.PersistContext;
import com.sonsure.dustman.jdbc.interceptor.PersistInterceptor;
import com.sonsure.dustman.jdbc.mapping.MappingHandler;
import com.sonsure.dustman.jdbc.mapping.MappingHandlerImpl;
import com.sonsure.dustman.flyable.FlyableHistory;
import com.sonsure.dustman.test.model.UserInfo;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

@Configuration
public class DustmanTestConfig {

    @Bean
    public MappingHandler defaultMappingHandler() {
        MappingHandlerImpl mappingHandler = new MappingHandlerImpl();
        mappingHandler.addScanPackages("com.sonsure.dustman.test.model");
        mappingHandler.registerTablePrefixMapping("sd_", FlyableHistory.class.getPackage().getName(), "com.sonsure.dustman.test.model");
        return mappingHandler;
    }

    @Bean
    public SqlSessionFactoryBean mybatisSqlSessionFactoryBean(DataSource dataSource) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setConfigLocation(new ClassPathResource("mybatis/mybatis-config.xml"));
        sqlSessionFactoryBean.setDataSource(dataSource);
        return sqlSessionFactoryBean;
    }

    public static class TestBeforeInterceptor implements PersistInterceptor {

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
            chain.execute(persistContext);
        }
    }

    public static class TestAfterInterceptor implements PersistInterceptor {

        @Override
        public void executeAfter(PersistContext persistContext, InterceptorChain chain) {
            Object result = persistContext.getResult();

            if (result instanceof List) {
                List<?> list = (List<?>) result;
                if (!list.isEmpty()) {
                    Object next = list.iterator().next();
                    if (next instanceof UserInfo) {
                        UserInfo userInfo = (UserInfo) next;
                        if ("interceptorUser".equals(userInfo.getLoginName())) {
                            userInfo.setLoginName("interceptorUserAfter");
                        }
                    }
                }
            }
            chain.execute(persistContext);
        }
    }

}

/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.jdbc.command.mybatis;


import com.sonsure.dustman.jdbc.command.build.ExecutableCmdBuilder;
import com.sonsure.dustman.jdbc.command.build.ExecutableCustomizer;
import com.sonsure.dustman.jdbc.command.simple.AbstractSimpleCommandExecutor;
import com.sonsure.dustman.jdbc.config.JdbcContext;
import com.sonsure.dustman.jdbc.exception.SonsureJdbcException;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.util.List;

/**
 * @author liyd
 * @since 17/4/25
 */
public class MybatisExecutorImpl extends AbstractSimpleCommandExecutor<MybatisExecutor> implements MybatisExecutor {

    public MybatisExecutorImpl(JdbcContext jdbcContext) {
        super(jdbcContext);
        this.getExecutableCmdBuilder()
                .addCustomizer(new MybatisExecutableCustomizer())
                .namedParameter();
    }

    private static class MybatisExecutableCustomizer implements ExecutableCustomizer {

        @Override
        public void customize(ExecutableCmdBuilder executableCmdBuilder) {

            SqlSessionFactory sqlSessionFactory = executableCmdBuilder.getJdbcContext().getMybatisSqlSessionFactory();
            if (sqlSessionFactory == null) {
                throw new SonsureJdbcException("使用Mybatis请先设置MybatisSqlSessionFactory");
            }
            MappedStatement statement = sqlSessionFactory.getConfiguration().getMappedStatement(executableCmdBuilder.getCommand());
            Configuration configuration = sqlSessionFactory.getConfiguration();
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            Object params = executableCmdBuilder.getParameterMap();
            BoundSql boundSql = statement.getBoundSql(params);
            Object parameterObject = boundSql.getParameterObject();
            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
            if (parameterMappings != null) {
                executableCmdBuilder.getCmdParameters().clear();
                for (ParameterMapping parameterMapping : parameterMappings) {
                    if (parameterMapping.getMode() != ParameterMode.OUT) {
                        Object value;
                        String propertyName = parameterMapping.getProperty();
                        if (boundSql.hasAdditionalParameter(propertyName)) {
                            value = boundSql.getAdditionalParameter(propertyName);
                        } else if (parameterObject == null) {
                            value = null;
                        } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                            value = parameterObject;
                        } else {
                            MetaObject metaObject = configuration.newMetaObject(parameterObject);
                            value = metaObject.getValue(propertyName);
                        }
                        executableCmdBuilder.addParameter(propertyName, value);
                    }
                }
            }
            executableCmdBuilder.command(boundSql.getSql());
            executableCmdBuilder.namedParameter(false);
        }
    }
}

/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.mybatis;

import com.sonsure.dumper.core.command.CommandDetails;
import com.sonsure.dumper.core.command.CommandParameter;
import com.sonsure.dumper.core.command.simple.AbstractSimpleCommandDetailsBuilder;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
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
 */
public class MybatisCommandDetailsBuilderImpl extends AbstractSimpleCommandDetailsBuilder<MybatisCommandDetailsBuilderImpl> {


    public MybatisCommandDetailsBuilderImpl(JdbcEngineConfig jdbcEngineConfig) {
        super(jdbcEngineConfig);
    }

    @Override
    public CommandDetails doBuild(JdbcEngineConfig jdbcEngineConfig) {

        CommandDetails commandDetails = new CommandDetails();

        SqlSessionFactory sqlSessionFactory = jdbcEngineConfig.getMybatisSqlSessionFactory();
        if (sqlSessionFactory == null) {
            throw new SonsureJdbcException("使用Mybatis请先设置MybatisSqlSessionFactory");
        }
        MappedStatement statement = sqlSessionFactory.getConfiguration().getMappedStatement(this.getCommand());
        Configuration configuration = sqlSessionFactory.getConfiguration();
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        BoundSql boundSql = statement.getBoundSql(this.getParameters());
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
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
                    commandDetails.addCommandParameter(new CommandParameter(propertyName, value));
                }
            }
        }
        commandDetails.setCommand(boundSql.getSql());
        this.namedParameter = false;
        return commandDetails;
    }
}

/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.mybatis;

import com.sonsure.dumper.core.command.CommandContext;
import com.sonsure.dumper.core.command.CommandParameter;
import com.sonsure.dumper.core.command.simple.AbstractSimpleCommandContextBuilder;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
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
public class MybatisCommandContextBuilderImpl extends AbstractSimpleCommandContextBuilder {


    public MybatisCommandContextBuilderImpl(Context simpleContext) {
        super(simpleContext);
    }

    @Override
    public CommandContext doBuild(JdbcEngineConfig jdbcEngineConfig) {

        CommandContext commandContext = new CommandContext();

        SqlSessionFactory sqlSessionFactory = jdbcEngineConfig.getMybatisSqlSessionFactory();
        MappedStatement statement = sqlSessionFactory.getConfiguration().getMappedStatement(getSimpleContext().getCommand());
        Configuration configuration = sqlSessionFactory.getConfiguration();
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        BoundSql boundSql = statement.getBoundSql(getSimpleContext().getParameters());
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
                    commandContext.addCommandParameter(new CommandParameter(propertyName, value));
                }
            }
        }
        commandContext.setCommand(boundSql.getSql());
        getSimpleContext().setNamedParameter(false);
        return commandContext;
    }
}

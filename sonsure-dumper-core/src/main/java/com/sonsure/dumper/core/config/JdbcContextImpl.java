/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.config;

import com.sonsure.dumper.core.command.build.CaseStyle;
import com.sonsure.dumper.core.command.sql.CommandConversionHandler;
import com.sonsure.dumper.core.command.sql.JSqlParserCommandConversionHandler;
import com.sonsure.dumper.core.interceptor.PersistInterceptor;
import com.sonsure.dumper.core.mapping.DefaultMappingHandler;
import com.sonsure.dumper.core.mapping.MappingHandler;
import com.sonsure.dumper.core.page.NegotiatingPageHandler;
import com.sonsure.dumper.core.page.PageHandler;
import com.sonsure.dumper.core.persist.KeyGenerator;
import com.sonsure.dumper.core.persist.PersistExecutor;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.session.SqlSessionFactory;

import javax.sql.DataSource;
import java.util.List;

/**
 * The type Jdbc engine.
 *
 * @author liyd
 * @since 17 /4/12
 */
@Getter
@Setter
public class JdbcContextImpl implements JdbcContext {

    /**
     * 数据源
     */
    protected DataSource dataSource;

    /**
     * 执行器构建factory
     */
    protected CommandExecutorFactory commandExecutorFactory;

    /**
     * 默认映射处理
     */
    protected MappingHandler mappingHandler;

    /**
     * 分页处理器
     */
    protected PageHandler pageHandler;

    /**
     * 解析器
     */
    protected CommandConversionHandler commandConversionHandler;

    /**
     * 默认主键生成器
     */
    protected KeyGenerator keyGenerator;

    /**
     * 默认持久化处理
     */
    protected PersistExecutor persistExecutor;

    /**
     * 拦截器
     */
    protected List<PersistInterceptor> persistInterceptors;

    /**
     * mybatis SqlSessionFactory
     */
    protected SqlSessionFactory mybatisSqlSessionFactory;

    /**
     * command大小写
     */
    protected CaseStyle caseStyle = CaseStyle.NONE;

    public JdbcContextImpl() {
        this.commandExecutorFactory = new CommandExecutorFactoryImpl();
        this.mappingHandler = new DefaultMappingHandler();
        this.pageHandler = new NegotiatingPageHandler();
        this.commandConversionHandler = new JSqlParserCommandConversionHandler();
    }

//    @Override
//    public PersistExecutor getPersistExecutor() {
//        if (persistExecutor == null) {
//            persistExecutor = this.initPersistExecutor();
//            if (persistExecutor == null) {
//                throw new SonsureJdbcException("persistExecutor不能为空");
//            }
//        }
//        return persistExecutor;
//    }


//    /**
//     * 初始化persistExecutor，具体由选型的子类重写
//     */
//    protected abstract PersistExecutor initPersistExecutor();

    @Override
    public String getDatabaseProduct() {
        return this.getPersistExecutor().getDatabaseProduct();
    }


//    @Override
//    public <T extends CommandExecutor<?>, M> T createExecutor(Class<T> commandExecutorClass, Class<M> modelClass) {
//        return this.jdbcExecutorConfig.getCommandExecutorFactory().createCommandExecutor(commandExecutorClass, this.jdbcExecutorConfig, modelClass);
//    }

}

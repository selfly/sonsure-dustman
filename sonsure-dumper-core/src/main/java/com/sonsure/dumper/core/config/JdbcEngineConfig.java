/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.config;


import com.sonsure.dumper.core.command.build.ToggleCase;
import com.sonsure.dumper.core.command.sql.CommandConversionHandler;
import com.sonsure.dumper.core.interceptor.PersistInterceptor;
import com.sonsure.dumper.core.mapping.MappingHandler;
import com.sonsure.dumper.core.page.PageHandler;
import com.sonsure.dumper.core.persist.KeyGenerator;
import com.sonsure.dumper.core.persist.PersistExecutor;
import org.apache.ibatis.session.SqlSessionFactory;

import javax.sql.DataSource;
import java.util.List;

/**
 * jdbc配置类
 * <p>
 *
 * @author liyd
 * @since 17/4/11
 */
public interface JdbcEngineConfig {

    /**
     * Gets data source.
     *
     * @return the data source
     */
    DataSource getDataSource();

    /**
     * 获取CommandExecutorFactory
     *
     * @return command executor factory
     */
    CommandExecutorFactory getCommandExecutorFactory();

    /**
     * 获取MappingHandler
     * 先根据type获取，如果没有再根据topInterfaceClass获取
     *
     * @return mapping handler
     */
    MappingHandler getMappingHandler();

    /**
     * 获取PageHandler
     * 先根据type获取，如果没有再根据topInterfaceClass获取
     *
     * @return page handler
     */
    PageHandler getPageHandler();

    /**
     * 获取KeyGenerator
     * 先根据type获取，如果没有再根据topInterfaceClass获取
     *
     * @return key generator
     */
    KeyGenerator getKeyGenerator();

    /**
     * 获取持久化执行器
     *
     * @return persist executor
     */
    PersistExecutor getPersistExecutor();

    /**
     * Gets persist interceptors.
     *
     * @return the persist interceptors
     */
    List<PersistInterceptor> getPersistInterceptors();

    /**
     * 获取command解析器
     *
     * @return command conversion handler
     */
    CommandConversionHandler getCommandConversionHandler();

    /**
     * 获取mybatis SqlSessionFactory
     *
     * @return mybatis sql session factory
     */
    SqlSessionFactory getMybatisSqlSessionFactory();

    /**
     * command大小写
     *
     * @return command case
     */
    ToggleCase getCommandCase();

}

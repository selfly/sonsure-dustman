/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.jdbc.config;

import com.sonsure.dustman.jdbc.command.build.CaseStyle;
import com.sonsure.dustman.jdbc.command.sql.CommandConversionHandler;
import com.sonsure.dustman.jdbc.interceptor.PersistInterceptor;
import com.sonsure.dustman.jdbc.mapping.MappingHandler;
import com.sonsure.dustman.jdbc.page.PageHandler;
import com.sonsure.dustman.jdbc.persist.KeyGenerator;
import com.sonsure.dustman.jdbc.persist.PersistExecutor;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

/**
 * The interface Jdbc engine.
 *
 * @author liyd
 * @since 17 /4/12
 */
public interface JdbcContext {

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
    CaseStyle getCaseStyle();

}

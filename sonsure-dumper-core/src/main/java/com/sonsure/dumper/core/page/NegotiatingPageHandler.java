/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.page;

import com.sonsure.commons.model.Pagination;
import com.sonsure.dumper.core.exception.SonsureJdbcException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liyd
 */
public class NegotiatingPageHandler implements PageHandler {

    protected List<PageHandler> defaultPageHandlers;

    protected List<PageHandler> pageHandlers;

    public NegotiatingPageHandler() {
        defaultPageHandlers = new ArrayList<>();
        defaultPageHandlers.add(new MysqlPageHandler());
        defaultPageHandlers.add(new OraclePageHandler());
        defaultPageHandlers.add(new PostgresqlPageHandler());
        defaultPageHandlers.add(new SqlServerPageHandler());
        defaultPageHandlers.add(new SqlitePageHandler());
        defaultPageHandlers.add(new H2PageHandler());
    }

    @Override
    public boolean support(String dialect) {
        return getPageHandler(dialect) != null;
    }

    @Override
    public String getCountCommand(String command, String dialect) {

        return getPageHandler(dialect).getCountCommand(command, dialect);
    }

    @Override
    public String getPageCommand(String command, Pagination pagination, String dialect) {

        return getPageHandler(dialect).getPageCommand(command, pagination, dialect);
    }

    /**
     * 根据dialect获取对应的pageHandler
     *
     * @param dialect
     * @return
     */
    protected PageHandler getPageHandler(String dialect) {
        if (pageHandlers != null) {
            for (PageHandler pageHandler : pageHandlers) {
                if (pageHandler.support(dialect)) {
                    return pageHandler;
                }
            }
        }
        for (PageHandler defaultPageHandler : defaultPageHandlers) {
            if (defaultPageHandler.support(dialect)) {
                return defaultPageHandler;
            }
        }
        throw new SonsureJdbcException("当前数据库dialect:" + dialect + "没有适配的PageHandler");
    }

    public void setPageHandlers(List<PageHandler> pageHandlers) {
        this.pageHandlers = pageHandlers;
    }

    public List<PageHandler> getPageHandlers() {
        return pageHandlers;
    }
}

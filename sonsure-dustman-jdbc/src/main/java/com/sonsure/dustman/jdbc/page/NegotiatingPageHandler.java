/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.jdbc.page;

import com.sonsure.dustman.common.model.Pagination;
import com.sonsure.dustman.jdbc.exception.SonsureJdbcException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liyd
 */
public class NegotiatingPageHandler implements PageHandler {

    protected List<PageHandler> pageHandlers;

    public NegotiatingPageHandler() {
        pageHandlers = new ArrayList<>();
        this.addPageHandler(new MysqlPageHandler());
        this.addPageHandler(new OraclePageHandler());
        this.addPageHandler(new PostgresqlPageHandler());
        this.addPageHandler(new SqlServerPageHandler());
        this.addPageHandler(new SqlitePageHandler());
        this.addPageHandler(new H2PageHandler());
    }

    public void addPageHandler(PageHandler pageHandler) {
        pageHandlers.add(pageHandler);
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
     * @param dialect the dialect
     * @return page handler
     */
    protected PageHandler getPageHandler(String dialect) {
        if (pageHandlers != null) {
            for (PageHandler pageHandler : pageHandlers) {
                if (pageHandler.support(dialect)) {
                    return pageHandler;
                }
            }
        }
        throw new SonsureJdbcException("当前数据库dialect:" + dialect + "没有适配的PageHandler");
    }

}

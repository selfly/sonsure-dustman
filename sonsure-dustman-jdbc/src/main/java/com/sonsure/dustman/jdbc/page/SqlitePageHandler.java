/*
 * Copyright (c) 2022. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   https://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.jdbc.page;

import com.sonsure.dustman.common.model.Pagination;
import com.sonsure.dustman.jdbc.config.DatabaseDialect;

/**
 * @author liyd
 */
public class SqlitePageHandler extends AbstractPageHandler {

    @Override
    public boolean support(String dialect) {
        return DatabaseDialect.SQLITE.belong(dialect);
    }

    @Override
    public String getPageCommand(String command, Pagination pagination, String dialect) {
        StringBuilder pageSql = new StringBuilder(200);
        pageSql.append(command);
        pageSql.append(" limit ");
        pageSql.append(pagination.getPageSize());
        pageSql.append(" offset ");
        pageSql.append(pagination.getBeginIndex());
        return pageSql.toString();
    }
}


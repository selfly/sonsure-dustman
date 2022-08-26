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
import com.sonsure.dumper.core.config.DatabaseDialect;

/**
 * @author selfly
 */
public class PostgresqlPageHandler extends AbstractPageHandler {

    @Override
    public boolean support(String dialect) {
        return DatabaseDialect.POSTGRESQL.belong(dialect);
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

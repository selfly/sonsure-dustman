/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   https://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.page;

import com.sonsure.dumper.common.model.Pagination;
import com.sonsure.dumper.core.config.DatabaseDialect;

/**
 * @author liyd
 */
public class SqlServerPageHandler extends AbstractPageHandler {

    protected SqlServerParser sqlServerParser = new SqlServerParser();

    @Override
    public boolean support(String dialect) {
        return DatabaseDialect.SQL_SERVER.belong(dialect);
    }

    @Override
    public String getPageCommand(String command, Pagination pagination, String dialect) {
        return sqlServerParser.convertToPageSql(command, pagination.getBeginIndex(), pagination.getPageSize());
    }
}

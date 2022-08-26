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
 * @author liyd
 */
public class OraclePageHandler extends AbstractPageHandler {

    @Override
    public boolean support(String dialect) {
        return DatabaseDialect.ORACLE.belong(dialect);
    }

    @Override
    public String getPageCommand(String command, Pagination pagination, String dialect) {
        StringBuilder pageSql = new StringBuilder(200);
        pageSql.append("select * from ( select rownum rownum_,temp_.* from (");
        pageSql.append(command);
        pageSql.append(") temp_ where rownum <= ").append(pagination.getEndIndex() + 1);
        pageSql.append(") where rownum_ > ").append(pagination.getBeginIndex());
        return pageSql.toString();
    }
}

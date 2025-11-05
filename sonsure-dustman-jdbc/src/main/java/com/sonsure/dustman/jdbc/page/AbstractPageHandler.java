/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.jdbc.page;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liyd
 */
public abstract class AbstractPageHandler implements PageHandler {

    /**
     * The Sql cache.
     */
    private final Map<String, String> sqlCache = new ConcurrentHashMap<>();

    protected CountSqlParser countSqlParser = new CountSqlParser();

    @Override
    public String getCountCommand(String sql, String dialect) {
        if (sqlCache.get(sql) != null) {
            return sqlCache.get(sql);
        }
        final String countCommand = countSqlParser.getSmartCountSql(sql);
        sqlCache.put(sql, countCommand);
        return countCommand;
    }
}

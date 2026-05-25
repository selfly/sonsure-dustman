/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.jdbc.page;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author liyd
 */
public abstract class AbstractPageHandler implements PageHandler {

    /**
     * The Sql cache.
     */
    private final Map<String, String> sqlCache = new LinkedHashMap<String, String>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
            return size() > 1024;
        }
    };

    protected CountSqlParser countSqlParser = new CountSqlParser();

    @Override
    public String getCountCommand(String sql, String dialect) {
        return sqlCache.computeIfAbsent(sql, k -> countSqlParser.getSmartCountSql(k));
    }
}

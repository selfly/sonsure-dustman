/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.page;

import com.sonsure.dumper.common.model.Pagination;

/**
 *
 * @author liyd
 * @since 16/6/8
 */
public interface PageHandler {

    /**
     * 是否支持当前数据库的分页
     *
     * @param dialect the dialect
     * @return boolean
     */
    boolean support(String dialect);

    /**
     * 根据查询语句获取count语句
     *
     * @param command the command
     * @param dialect the dialect
     * @return count command
     */
    String getCountCommand(String command, String dialect);

    /**
     * 根据查询语句获取分页语句
     *
     * @param command    the command
     * @param pagination the pagination
     * @param dialect    the dialect
     * @return page command
     */
    String getPageCommand(String command, Pagination pagination, String dialect);
}

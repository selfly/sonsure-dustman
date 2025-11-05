/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.jdbc.command.sql;


import com.sonsure.dustman.jdbc.command.build.CmdParameter;
import com.sonsure.dustman.jdbc.config.JdbcContext;

import java.util.List;

/**
 * @author liyd
 */
public interface CommandConversionHandler {

    /**
     * command转换
     *
     * @param command     the command
     * @param parameters  the parameters
     * @param jdbcContext the jdbc context
     * @return string string
     */
    String convert(String command, List<CmdParameter> parameters, JdbcContext jdbcContext);


}

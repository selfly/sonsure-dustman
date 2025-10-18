/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.sql;


import com.sonsure.dumper.core.command.build.CmdParameter;

import java.util.List;

/**
 * @author liyd
 */
public interface CommandConversionHandler {

    /**
     * command转换
     *
     * @param command    the command
     * @param parameters the parameters
     * @return string string
     */
    String convert(String command, List<CmdParameter> parameters);


}

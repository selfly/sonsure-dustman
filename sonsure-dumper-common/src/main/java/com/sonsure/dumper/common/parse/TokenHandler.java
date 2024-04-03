/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.common.parse;

/**
 * Created by liyd on 2015-11-24.
 */
public interface TokenHandler {

    /**
     * Handle token string.
     *
     * @param content the content
     * @return the string
     */
    String handleToken(String content);
}

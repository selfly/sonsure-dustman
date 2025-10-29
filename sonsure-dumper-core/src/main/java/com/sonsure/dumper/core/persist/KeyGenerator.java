/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.persist;

/**
 *
 * @author liyd
 * @since 17/4/12
 */
public interface KeyGenerator {

    /**
     * 包围主键值表示一整列不解析，如oracle序列名 seq_user.nextval,sql语法下会解析成table.column
     */
    String NATIVE_OPEN_TOKEN = "`{{";

    String NATIVE_CLOSE_TOKEN = "}}`";

    /**
     * 生成主键值
     *
     * @param clazz the clazz
     * @return value
     */
    Object generateKeyValue(Class<?> clazz);
}

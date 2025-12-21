/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.jdbc.persist;


import com.sonsure.dustman.common.utils.NameUtils;

/**
 *
 * @author liyd
 * @since 16/8/25
 */
public class OracleKeyGenerator implements KeyGenerator {

    @Override
    public Object generateKeyValue(Class<?> clazz) {
        //根据实体名获取主键序列名
        String tableName = NameUtils.getLowerUnderlineName(clazz.getSimpleName());
        return String.format("`{{SEQ_%s.NEXTVAL}}`", tableName);
    }
}

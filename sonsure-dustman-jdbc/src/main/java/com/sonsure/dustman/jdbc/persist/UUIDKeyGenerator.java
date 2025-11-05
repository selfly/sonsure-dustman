/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.jdbc.persist;

import com.sonsure.dustman.common.utils.UUIDUtils;

/**
 * Created by liyd on 16/8/25.
 */
public class UUIDKeyGenerator implements KeyGenerator {

    @Override
    public Object generateKeyValue(Class<?> clazz) {
        return UUIDUtils.getUUID32();
    }
}

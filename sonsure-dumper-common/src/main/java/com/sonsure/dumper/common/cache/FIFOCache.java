/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.common.cache;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * FIFO缓存实现
 *
 * Created by liyd on 9/25/14.
 */
public class FIFOCache<K, V> extends AbstractCacheMap<K, V> {

    /**
     * 构造访求
     * 
     * @param cacheSize
     * @param defaultExpire
     */
    public FIFOCache(int cacheSize, long defaultExpire) {
        super(cacheSize, defaultExpire);
        cacheMap = new LinkedHashMap<K, CacheObject<K, V>>(cacheSize + 1, 1F, false);
    }

    @Override
    protected int eliminateCache() {

        int count = 0;
        K firstKey = null;

        Iterator<CacheObject<K, V>> iterator = cacheMap.values().iterator();
        while (iterator.hasNext()) {
            CacheObject<K, V> cacheObject = iterator.next();

            if (cacheObject.isExpired()) {
                iterator.remove();
                count++;
            } else {
                if (firstKey == null) {
                    firstKey = cacheObject.key;
                }
            }
        }

        //删除过期对象还是满,继续删除链表第一个
        if (firstKey != null && isFull()) {
            cacheMap.remove(firstKey);
        }

        return count;
    }

}

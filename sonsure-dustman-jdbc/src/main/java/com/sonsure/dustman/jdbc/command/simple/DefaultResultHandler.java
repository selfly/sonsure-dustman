/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.jdbc.command.simple;


import com.sonsure.dustman.common.bean.BeanKit;
import com.sonsure.dustman.jdbc.exception.SonsureJdbcException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author selfly
 */
public class DefaultResultHandler<E> implements ResultHandler<E> {

    protected static final List<Class<?>> SINGLE_VALUE_CLASSES = new ArrayList<>();

    static {
        SINGLE_VALUE_CLASSES.add(String.class);
        SINGLE_VALUE_CLASSES.add(Short.class);
        SINGLE_VALUE_CLASSES.add(Integer.class);
        SINGLE_VALUE_CLASSES.add(Boolean.class);
        SINGLE_VALUE_CLASSES.add(Float.class);
        SINGLE_VALUE_CLASSES.add(Double.class);
        SINGLE_VALUE_CLASSES.add(Long.class);
        SINGLE_VALUE_CLASSES.add(Byte.class);
        SINGLE_VALUE_CLASSES.add(Character.class);
    }

    private final Class<E> clazz;

    private DefaultResultHandler(Class<E> mappedClass) {
        this.clazz = mappedClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E handle(Object object) {
        if (object instanceof Map) {
            Map<String, ?> map = (Map<String, ?>) object;
            if (SINGLE_VALUE_CLASSES.contains(this.clazz)) {
                this.checkSingleValueMapSize(map);
                return (E) map.values().iterator().next();
            } else {
                return BeanKit.mapToBean(map, clazz, '_');
            }
        } else {
            return (E) object;
        }
    }

    public static <E> ResultHandler<E> newInstance(Class<E> mappedClass) {
        return new DefaultResultHandler<>(mappedClass);
    }

    private void checkSingleValueMapSize(Map<String, ?> map) {
        if (map.size() > 1) {
            throw new SonsureJdbcException("返回的结果列数大于1，无法转换成单一值类型:" + this.clazz);
        }
    }
}

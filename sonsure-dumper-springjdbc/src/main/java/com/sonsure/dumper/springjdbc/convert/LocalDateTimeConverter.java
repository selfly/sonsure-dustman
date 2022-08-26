/*
 * Copyright (c) 2022. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   https://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.springjdbc.convert;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author liyd
 */
public class LocalDateTimeConverter implements TypeConverter {

    private static final String LOCAL_DATE = "LocalDate";
    private static final String LOCAL_TIME = "LocalTime";
    private static final String LOCAL_DATE_TIME = "LocalDateTime";

    private final Set<String> types = new HashSet<>();

    public LocalDateTimeConverter() {
        types.add(LOCAL_DATE);
        types.add(LOCAL_TIME);
        types.add(LOCAL_DATE_TIME);
    }

    @Override
    public boolean support(Class<?> requiredType, Object value) {
        return types.contains(requiredType.getSimpleName()) && value instanceof Timestamp;
    }

    @Override
    public Object convert(Class<?> requiredType, Object value) {
        final LocalDateTime localDateTime = ((Timestamp) value).toLocalDateTime();
        if (LOCAL_DATE.equals(requiredType.getSimpleName())) {
            return localDateTime.toLocalDate();
        } else if (LOCAL_TIME.equals(requiredType.getSimpleName())) {
            return localDateTime.toLocalTime();
        } else {
            return localDateTime;
        }
    }
}

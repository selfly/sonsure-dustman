package com.sonsure.dumper.common.bean;

import java.beans.PropertyDescriptor;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * @author selfly
 */
public class DateTimeConverter {

    public static class Date2LocalDateTimeConverter implements TypeConverter {

        @Override
        public boolean supportSourceType(Class<?> sourceType) {
            return Date.class.isAssignableFrom(sourceType);
        }

        @Override
        public boolean supportTargetType(Class<?> targetType) {
            return LocalDateTime.class.equals(targetType);
        }

        @Override
        public Object convert(PropertyDescriptor targetPd, Object value) {
            Instant instant = ((Date) value).toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            return instant.atZone(zoneId).toLocalDateTime();
        }
    }

    public static class LocalDateTime2DateConverter implements TypeConverter {

        @Override
        public boolean supportSourceType(Class<?> sourceType) {
            return LocalDateTime.class.isAssignableFrom(sourceType);
        }

        @Override
        public boolean supportTargetType(Class<?> targetType) {
            return Date.class.equals(targetType);
        }

        @Override
        public Object convert(PropertyDescriptor targetPd, Object value) {
            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zonedDateTime = ((LocalDateTime) value).atZone(zoneId);
            return Date.from(zonedDateTime.toInstant());
        }
    }

}

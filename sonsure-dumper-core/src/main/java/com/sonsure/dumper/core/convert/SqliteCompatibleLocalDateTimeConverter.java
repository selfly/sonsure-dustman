package com.sonsure.dumper.core.convert;

import com.sonsure.dumper.core.config.DatabaseDialect;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

/**
 * @author selfly
 */
public class SqliteCompatibleLocalDateTimeConverter implements JdbcTypeConverter {

    private static final String LOCAL_DATE = "LocalDate";
    private static final String LOCAL_TIME = "LocalTime";
    private static final String LOCAL_DATE_TIME = "LocalDateTime";

    private static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    private static final String PATTERN_DATE = "yyyy-MM-dd";
    private static final String PATTERN_TIME = "HH:mm:ss";

    private final Set<String> types = new HashSet<>();

    public SqliteCompatibleLocalDateTimeConverter() {
        types.add(LOCAL_DATE);
        types.add(LOCAL_TIME);
        types.add(LOCAL_DATE_TIME);
    }

    @Override
    public Object db2JavaType(String dialect, Class<?> requiredType, Object value) {
        if (!DatabaseDialect.SQLITE.belong(dialect)) {
            return value;
        }
        if (types.contains(requiredType.getSimpleName()) && value instanceof Timestamp) {
            final LocalDateTime localDateTime = ((Timestamp) value).toLocalDateTime();
            if (LOCAL_DATE.equals(requiredType.getSimpleName())) {
                return localDateTime.toLocalDate();
            } else if (LOCAL_TIME.equals(requiredType.getSimpleName())) {
                return localDateTime.toLocalTime();
            } else {
                return localDateTime;
            }
        }
        return value;
    }

    @Override
    public Object java2DbType(String dialect, Object value) {
        if (!DatabaseDialect.SQLITE.belong(dialect)) {
            return value;
        }
        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).format(DateTimeFormatter.ofPattern(PATTERN_DATETIME));
        } else if (value instanceof LocalDate) {
            return ((LocalDate) value).format(DateTimeFormatter.ofPattern(PATTERN_DATE));
        } else if (value instanceof LocalTime) {
            return ((LocalTime) value).format(DateTimeFormatter.ofPattern(PATTERN_TIME));
        } else {
            return value;
        }
    }
}

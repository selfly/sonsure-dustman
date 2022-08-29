package com.sonsure.dumper.core.convert;

/**
 * @author selfly
 */
public interface JdbcTypeConverter {

    /**
     * Support dialect boolean.
     *
     * @param dialect the dialect
     * @return the boolean
     */
    boolean support(String dialect);

    /**
     * Db 2 java type object.
     *
     * @param dialect      the dialect
     * @param requiredType the required type
     * @param value        the value
     * @return the object
     */
    default Object db2JavaType(String dialect, Class<?> requiredType, Object value) {
        return value;
    }

    /**
     * Java 2 db type object.
     *
     * @param dialect the dialect
     * @param value   the value
     * @return the object
     */
    default Object java2DbType(String dialect, Object value) {
        return value;
    }

}

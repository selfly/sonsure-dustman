package com.sonsure.dumper.core.convert;

import com.sonsure.dumper.core.command.CommandDetails;
import com.sonsure.dumper.core.command.CommandType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author selfly
 */
public class JdbcTypeConverterComposite implements JdbcTypeConverter {

    private final List<JdbcTypeConverter> jdbcTypeConverters;

    public JdbcTypeConverterComposite(List<JdbcTypeConverter> jdbcTypeConverters) {
        this.jdbcTypeConverters = jdbcTypeConverters;
    }

    @Override
    public boolean support(String dialect) {
        if (jdbcTypeConverters == null || jdbcTypeConverters.isEmpty()) {
            return false;
        }
        for (JdbcTypeConverter jdbcTypeConverter : jdbcTypeConverters) {
            if (jdbcTypeConverter.support(dialect)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object db2JavaType(String dialect, Class<?> requiredType, Object value) {
        Object result = value;
        for (JdbcTypeConverter jdbcTypeConverter : jdbcTypeConverters) {
            if (jdbcTypeConverter.support(dialect)) {
                result = jdbcTypeConverter.db2JavaType(dialect, requiredType, value);
            }
        }
        return result;
    }

    @Override
    public Object java2DbType(String dialect, Object value) {
        Object result = value;
        for (JdbcTypeConverter jdbcTypeConverter : jdbcTypeConverters) {
            if (jdbcTypeConverter.support(dialect)) {
                result = jdbcTypeConverter.java2DbType(dialect, value);
            }
        }
        return result;
    }

    /**
     * Convert.
     *
     * @param dialect        the dialect
     * @param commandDetails the command context
     * @param commandType    the command type
     */
    public void convert(String dialect, CommandDetails commandDetails, CommandType commandType) {
        final List<Object> parameters = commandDetails.getParameters();
        final List<Object> newParams = new ArrayList<>();
        for (Object parameter : parameters) {
            final Object value = this.java2DbType(dialect, parameter);
            newParams.add(value);
        }
        commandDetails.setParameters(newParams);
    }
}

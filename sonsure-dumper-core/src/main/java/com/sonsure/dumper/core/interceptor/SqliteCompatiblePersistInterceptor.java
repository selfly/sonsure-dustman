package com.sonsure.dumper.core.interceptor;

import com.sonsure.dumper.core.command.CommandContext;
import com.sonsure.dumper.core.command.CommandType;
import com.sonsure.dumper.core.config.DatabaseDialect;
import com.sonsure.dumper.core.convert.SqliteCompatibleLocalDateTimeConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author selfly
 */
public class SqliteCompatiblePersistInterceptor implements PersistInterceptor {

    private final SqliteCompatibleLocalDateTimeConverter sqliteCompatibleLocalDateTimeConverter = new SqliteCompatibleLocalDateTimeConverter();

    @Override
    public void executeBefore(String dialect, CommandContext commandContext, CommandType commandType) {
        if (!DatabaseDialect.SQLITE.belong(dialect)) {
            return;
        }
        final List<Object> parameters = commandContext.getParameters();
        final List<Object> newParams = new ArrayList<>();
        for (Object parameter : parameters) {
            final Object value = sqliteCompatibleLocalDateTimeConverter.java2DbType(dialect, parameter);
            newParams.add(value);
        }
        commandContext.setParameters(newParams);
    }
}

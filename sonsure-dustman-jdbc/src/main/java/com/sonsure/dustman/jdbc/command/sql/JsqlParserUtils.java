package com.sonsure.dustman.jdbc.command.sql;

import com.sonsure.dustman.jdbc.persist.KeyGenerator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.RowConstructor;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

import java.util.List;

/**
 * @author selfly
 */
public class JsqlParserUtils {

    /**
     * Table name mapping.
     *
     * @param table          the table
     * @param mappingHandler the mapping handler
     */
    public static void mappingTableName(Table table, CommandMappingHandler mappingHandler) {
        final String tableName = mappingHandler.getTableName(table);
        table.setName(tableName);
    }

    /**
     * Mapping column.
     *
     * @param column         the column
     * @param mappingHandler the mapping handler
     */
    public static void mappingColumn(Column column, CommandMappingHandler mappingHandler) {
        String columnName = column.getColumnName();
        if (columnName.startsWith(KeyGenerator.NATIVE_OPEN_TOKEN) && columnName.endsWith(KeyGenerator.NATIVE_CLOSE_TOKEN)) {
            columnName = columnName.substring(KeyGenerator.NATIVE_OPEN_TOKEN.length(), columnName.length() - KeyGenerator.NATIVE_CLOSE_TOKEN.length());
        } else {
            columnName = mappingHandler.getColumnName(column);
        }
        column.setColumnName(columnName);
    }

    /**
     * Mapping expression.
     *
     * @param expressionList the expression list
     * @param mappingHandler the mapping handler
     */
    public static void mappingExpression(ExpressionList expressionList, CommandMappingHandler mappingHandler) {

        final List<Expression> expressions = expressionList.getExpressions();
        for (Expression expression : expressions) {
            mappingExpression(expression, mappingHandler);
        }
    }

    public static void mappingExpression(Expression expression, CommandMappingHandler mappingHandler) {
        if (expression instanceof RowConstructor) {
            final ExpressionList exprList = ((RowConstructor) expression).getExprList();
            mappingExpression(exprList, mappingHandler);
        } else if (expression instanceof Column) {
            mappingColumn((Column) expression, mappingHandler);
        }
    }

}

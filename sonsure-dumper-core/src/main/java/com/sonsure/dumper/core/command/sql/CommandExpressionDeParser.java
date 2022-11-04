/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.sql;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;

/**
 * @author liyd
 */
public class CommandExpressionDeParser extends ExpressionDeParser {

    @Override
    public void visit(Column column) {

        CommandSelectDeParser selectVisitor = (CommandSelectDeParser) getSelectVisitor();
        CommandMappingHandler commandMappingHandler = selectVisitor.getCommandMappingHandler();

        final Table table = column.getTable();
        String tableName = null;
        if (table != null) {
            JsqlParserUtils.mappingTableName(table, commandMappingHandler);
            if (table.getAlias() != null) {
                tableName = table.getAlias().getName();
            } else {
                tableName = table.getFullyQualifiedName();
            }
        }
        if (tableName != null && !tableName.isEmpty()) {
            getBuffer().append(tableName).append(".");
        }
        JsqlParserUtils.mappingColumn(column, commandMappingHandler);
        buffer.append(column.getColumnName());
    }
}

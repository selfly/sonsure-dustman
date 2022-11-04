/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.sql;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.util.deparser.InsertDeParser;

import java.util.Iterator;

/**
 * @author liyd
 */
public class CommandInsertDeParser extends InsertDeParser {

    private final CommandMappingHandler commandMappingHandler;

    public CommandInsertDeParser(ExpressionVisitor expressionVisitor, SelectVisitor selectVisitor, StringBuilder buffer, CommandMappingHandler commandMappingHandler) {
        super(expressionVisitor, selectVisitor, buffer);
        this.commandMappingHandler = commandMappingHandler;
    }

    @Override
    public void deParse(Insert insert) {
        if (insert.getWithItemsList() != null && !insert.getWithItemsList().isEmpty()) {
            buffer.append("WITH ");
            for (Iterator<WithItem> iter = insert.getWithItemsList().iterator(); iter.hasNext(); ) {
                WithItem withItem = iter.next();
                withItem.accept(getSelectVisitor());
                if (iter.hasNext()) {
                    buffer.append(",");
                }
                buffer.append(" ");
            }
        }

        buffer.append("INSERT ");
        if (insert.getModifierPriority() != null) {
            buffer.append(insert.getModifierPriority()).append(" ");
        }
        if (insert.isModifierIgnore()) {
            buffer.append("IGNORE ");
        }
        buffer.append("INTO ");

        final Table table = insert.getTable();
        JsqlParserUtils.mappingTableName(table, commandMappingHandler);
        buffer.append(table.toString());

        if (insert.getColumns() != null) {
            buffer.append(" (");
            for (Iterator<Column> iter = insert.getColumns().iterator(); iter.hasNext(); ) {
                Column column = iter.next();
                JsqlParserUtils.mappingColumn(column, commandMappingHandler);
                buffer.append(column.getColumnName());
                if (iter.hasNext()) {
                    buffer.append(", ");
                }
            }
            buffer.append(")");
        }

        if (insert.getOutputClause() != null) {
            buffer.append(insert.getOutputClause().toString());
        }

        if (insert.getSelect() != null) {
            buffer.append(" ");
            if (insert.getSelect().isUsingWithBrackets()) {
                buffer.append("(");
            }
            if (insert.getSelect().getWithItemsList() != null) {
                buffer.append("WITH ");
                for (WithItem with : insert.getSelect().getWithItemsList()) {
                    with.accept(getSelectVisitor());
                }
                buffer.append(" ");
            }
            insert.getSelect().getSelectBody().accept(getSelectVisitor());
            if (insert.getSelect().isUsingWithBrackets()) {
                buffer.append(")");
            }
        }

        if (insert.isUseSet()) {
            buffer.append(" SET ");
            for (int i = 0; i < insert.getSetColumns().size(); i++) {
                Column column = insert.getSetColumns().get(i);
                column.accept(getExpressionVisitor());

                buffer.append(" = ");

                Expression expression = insert.getSetExpressionList().get(i);
                expression.accept(getExpressionVisitor());
                if (i < insert.getSetColumns().size() - 1) {
                    buffer.append(", ");
                }
            }
        }

        if (insert.isUseDuplicate()) {
            buffer.append(" ON DUPLICATE KEY UPDATE ");
            for (int i = 0; i < insert.getDuplicateUpdateColumns().size(); i++) {
                Column column = insert.getDuplicateUpdateColumns().get(i);
                JsqlParserUtils.mappingColumn(column, commandMappingHandler);
                buffer.append(column.getFullyQualifiedName()).append(" = ");

                Expression expression = insert.getDuplicateUpdateExpressionList().get(i);
                expression.accept(getExpressionVisitor());
                if (i < insert.getDuplicateUpdateColumns().size() - 1) {
                    buffer.append(", ");
                }
            }
        }

        //@todo: Accept some Visitors for the involved Expressions
        if (insert.getConflictAction() != null) {
            buffer.append(" ON CONFLICT");

            if (insert.getConflictTarget() != null) {
                insert.getConflictTarget().appendTo(buffer);
            }
            insert.getConflictAction().appendTo(buffer);
        }

        if (insert.getReturningExpressionList() != null) {
            buffer.append(" RETURNING ").append(PlainSelect.
                    getStringList(insert.getReturningExpressionList(), true, false));
        }
    }

}

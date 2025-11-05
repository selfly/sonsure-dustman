/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.jdbc.command.sql;

import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.update.UpdateSet;
import net.sf.jsqlparser.util.deparser.LimitDeparser;
import net.sf.jsqlparser.util.deparser.OrderByDeParser;
import net.sf.jsqlparser.util.deparser.UpdateDeParser;

import java.util.Iterator;

/**
 * @author liyd
 */
public class CommandUpdateDeParser extends UpdateDeParser {

    private final CommandMappingHandler commandMappingHandler;

    public CommandUpdateDeParser(ExpressionVisitor expressionVisitor, StringBuilder buffer, CommandMappingHandler commandMappingHandler) {
        super(expressionVisitor, buffer);
        this.commandMappingHandler = commandMappingHandler;
    }

    @Override
    public void deParse(Update update) {
        if (update.getWithItemsList() != null && !update.getWithItemsList().isEmpty()) {
            buffer.append("WITH ");
            for (Iterator<WithItem> iter = update.getWithItemsList().iterator(); iter.hasNext(); ) {
                WithItem withItem = iter.next();
                buffer.append(withItem);
                if (iter.hasNext()) {
                    buffer.append(",");
                }
                buffer.append(" ");
            }
        }
        buffer.append("UPDATE ");
        if (update.getModifierPriority() != null) {
            buffer.append(update.getModifierPriority()).append(" ");
        }
        if (update.isModifierIgnore()) {
            buffer.append("IGNORE ");
        }
        final Table table = update.getTable();
        JsqlParserUtils.mappingTableName(table, commandMappingHandler);
        buffer.append(table);
        if (update.getStartJoins() != null) {
            for (Join join : update.getStartJoins()) {
                if (join.isSimple()) {
                    buffer.append(", ").append(join);
                } else {
                    buffer.append(" ").append(join);
                }
            }
        }
        buffer.append(" SET ");

        int j = 0;
        for (UpdateSet updateSet : update.getUpdateSets()) {
            if (j > 0) {
                buffer.append(", ");
            }

            if (updateSet.isUsingBracketsForColumns()) {
                buffer.append("(");
            }
            for (int i = 0; i < updateSet.getColumns().size(); i++) {
                if (i > 0) {
                    buffer.append(", ");
                }
                updateSet.getColumns().get(i).accept(getExpressionVisitor());
            }
            if (updateSet.isUsingBracketsForColumns()) {
                buffer.append(")");
            }

            buffer.append(" = ");

            if (updateSet.isUsingBracketsForValues()) {
                buffer.append("(");
            }
            for (int i = 0; i < updateSet.getExpressions().size(); i++) {
                if (i > 0) {
                    buffer.append(", ");
                }
                updateSet.getExpressions().get(i).accept(getExpressionVisitor());
            }
            if (updateSet.isUsingBracketsForValues()) {
                buffer.append(")");
            }

            j++;
        }

        if (update.getOutputClause() != null) {
            update.getOutputClause().appendTo(buffer);
        }

        if (update.getFromItem() != null) {
            buffer.append(" FROM ").append(update.getFromItem());
            if (update.getJoins() != null) {
                for (Join join : update.getJoins()) {
                    if (join.isSimple()) {
                        buffer.append(", ").append(join);
                    } else {
                        buffer.append(" ").append(join);
                    }
                }
            }
        }

        if (update.getWhere() != null) {
            buffer.append(" WHERE ");
            update.getWhere().accept(getExpressionVisitor());
        }
        if (update.getOrderByElements() != null) {
            new OrderByDeParser(getExpressionVisitor(), buffer).deParse(update.getOrderByElements());
        }
        if (update.getLimit() != null) {
            new LimitDeparser(buffer).deParse(update.getLimit());
        }

        if (update.getReturningExpressionList() != null) {
            buffer.append(" RETURNING ").append(PlainSelect.
                    getStringList(update.getReturningExpressionList(), true, false));
        }
    }

}

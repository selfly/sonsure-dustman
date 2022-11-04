/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.sql;

import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.util.deparser.DeleteDeParser;
import net.sf.jsqlparser.util.deparser.LimitDeparser;
import net.sf.jsqlparser.util.deparser.OrderByDeParser;

import java.util.Iterator;

import static java.util.stream.Collectors.joining;

/**
 * @author liyd
 */
public class CommandDeleteDeParser extends DeleteDeParser {

    private final CommandMappingHandler commandMappingHandler;

    public CommandDeleteDeParser(ExpressionVisitor expressionVisitor, StringBuilder buffer, CommandMappingHandler commandMappingHandler) {
        super(expressionVisitor, buffer);
        this.commandMappingHandler = commandMappingHandler;
    }

    @Override
    public void deParse(Delete delete) {
        if (delete.getWithItemsList() != null && !delete.getWithItemsList().isEmpty()) {
            buffer.append("WITH ");
            for (Iterator<WithItem> iter = delete.getWithItemsList().iterator(); iter.hasNext(); ) {
                WithItem withItem = iter.next();
                buffer.append(withItem);
                if (iter.hasNext()) {
                    buffer.append(",");
                }
                buffer.append(" ");
            }
        }
        buffer.append("DELETE");
        if (delete.getModifierPriority() != null) {
            buffer.append(" ").append(delete.getModifierPriority());
        }
        if (delete.isModifierQuick()) {
            buffer.append(" QUICK");
        }
        if (delete.isModifierIgnore()) {
            buffer.append(" IGNORE");
        }
        if (delete.getTables() != null && !delete.getTables().isEmpty()) {
            buffer.append(
                    delete.getTables().stream()
                            .map(t -> {
                                JsqlParserUtils.mappingTableName(t, commandMappingHandler);
                                return t.getFullyQualifiedName();
                            })
                            .collect(joining(", ", " ", "")));
        }

        if (delete.getOutputClause() != null) {
            delete.getOutputClause().appendTo(buffer);
        }

        if (delete.isHasFrom()) {
            buffer.append(" FROM");
        }
        final Table table = delete.getTable();
        JsqlParserUtils.mappingTableName(table, commandMappingHandler);
        buffer.append(" ").append(table);

        if (delete.getUsingList() != null && !delete.getUsingList().isEmpty()) {
            buffer.append(" USING").append(
                    delete.getUsingList().stream().map(t -> {
                        JsqlParserUtils.mappingTableName(t, commandMappingHandler);
                        return t.getFullyQualifiedName();
                    }).collect(joining(", ", " ", "")));
        }
        if (delete.getJoins() != null) {
            for (Join join : delete.getJoins()) {
                if (join.isSimple()) {
                    buffer.append(", ").append(join);
                } else {
                    buffer.append(" ").append(join);
                }
            }
        }

        if (delete.getWhere() != null) {
            buffer.append(" WHERE ");
            delete.getWhere().accept(getExpressionVisitor());
        }

        if (delete.getOrderByElements() != null) {
            new OrderByDeParser(getExpressionVisitor(), buffer).deParse(delete.getOrderByElements());
        }
        if (delete.getLimit() != null) {
            new LimitDeparser(buffer).deParse(delete.getLimit());
        }

        if (delete.getReturningExpressionList() != null) {
            buffer.append(" RETURNING ").append(PlainSelect.
                    getStringList(delete.getReturningExpressionList(), true, false));
        }

    }

}

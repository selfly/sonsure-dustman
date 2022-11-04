/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.sql;

import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.MySQLIndexHint;
import net.sf.jsqlparser.expression.SQLServerHints;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Pivot;
import net.sf.jsqlparser.statement.select.UnPivot;
import net.sf.jsqlparser.util.deparser.SelectDeParser;

/**
 * @author liyd
 */
public class CommandSelectDeParser extends SelectDeParser {

    private final CommandMappingHandler commandMappingHandler;

    public CommandSelectDeParser(ExpressionVisitor expressionVisitor, StringBuilder buffer, CommandMappingHandler commandMappingHandler) {
        super(expressionVisitor, buffer);
        this.commandMappingHandler = commandMappingHandler;
    }

    @Override
    public void visit(Table tableName) {

        JsqlParserUtils.mappingTableName(tableName, commandMappingHandler);
        buffer.append(tableName.getFullyQualifiedName());
        Alias alias = tableName.getAlias();
        if (alias != null) {
            buffer.append(alias);
        }
        Pivot pivot = tableName.getPivot();
        if (pivot != null) {
            pivot.accept(this);
        }
        UnPivot unpivot = tableName.getUnPivot();
        if (unpivot != null) {
            unpivot.accept(this);
        }
        MySQLIndexHint indexHint = tableName.getIndexHint();
        if (indexHint != null) {
            buffer.append(indexHint);
        }
        SQLServerHints sqlServerHints = tableName.getSqlServerHints();
        if (sqlServerHints != null) {
            buffer.append(sqlServerHints);
        }
    }

    @Override
    public void visit(ExpressionList expressionList) {
        JsqlParserUtils.mappingExpression(expressionList, commandMappingHandler);
        buffer.append(expressionList);
    }

    public CommandMappingHandler getCommandMappingHandler() {
        return commandMappingHandler;
    }
}

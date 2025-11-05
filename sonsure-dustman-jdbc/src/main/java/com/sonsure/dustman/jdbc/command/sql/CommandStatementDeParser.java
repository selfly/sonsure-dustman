/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.jdbc.command.sql;

import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.deparser.*;

/**
 * @author liyd
 */
public class CommandStatementDeParser extends StatementDeParser {

    protected ExpressionDeParser expressionDeParser;
    protected SelectDeParser selectDeParser;
    protected CommandMappingHandler commandMappingHandler;

    public CommandStatementDeParser(ExpressionDeParser expressionDeParser, SelectDeParser selectDeParser, StringBuilder buffer, CommandMappingHandler commandMappingHandler) {
        super(expressionDeParser, selectDeParser, buffer);
        this.expressionDeParser = expressionDeParser;
        this.selectDeParser = selectDeParser;
        this.commandMappingHandler = commandMappingHandler;
    }

    @Override
    public void visit(Update update) {
        selectDeParser.setBuffer(getBuffer());
        expressionDeParser.setSelectVisitor(selectDeParser);
        expressionDeParser.setBuffer(getBuffer());
        UpdateDeParser updateDeParser = new CommandUpdateDeParser(expressionDeParser, getBuffer(), this.commandMappingHandler);
        selectDeParser.setExpressionVisitor(expressionDeParser);
        updateDeParser.deParse(update);
    }

    @Override
    public void visit(Delete delete) {
        selectDeParser.setBuffer(getBuffer());
        expressionDeParser.setSelectVisitor(selectDeParser);
        expressionDeParser.setBuffer(getBuffer());
        selectDeParser.setExpressionVisitor(expressionDeParser);
        DeleteDeParser deleteDeParser = new CommandDeleteDeParser(expressionDeParser, getBuffer(), this.commandMappingHandler);
        deleteDeParser.deParse(delete);
    }

    @Override
    public void visit(Insert insert) {
        selectDeParser.setBuffer(getBuffer());
        expressionDeParser.setSelectVisitor(selectDeParser);
        expressionDeParser.setBuffer(getBuffer());
        selectDeParser.setExpressionVisitor(expressionDeParser);
        InsertDeParser insertDeParser = new CommandInsertDeParser(expressionDeParser, selectDeParser, getBuffer(), this.commandMappingHandler);
        insertDeParser.deParse(insert);
    }
}

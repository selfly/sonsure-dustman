/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.sql;

import com.sonsure.dumper.core.command.build.CmdParameter;
import com.sonsure.dumper.core.config.JdbcContext;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;
import net.sf.jsqlparser.util.deparser.SelectDeParser;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liyd
 */
public class JSqlParserCommandConversionHandler implements CommandConversionHandler {

    protected final Map<String, String> cache = new WeakHashMap<>(new ConcurrentHashMap<>());

    @Override
    public String convert(String command, List<CmdParameter> parameters, JdbcContext jdbcContext) {

        String convertedCommand = cache.get(command);
        if (convertedCommand == null) {
            try {
                StringBuilder buffer = new StringBuilder();
                Statement statement = CCJSqlParserUtil.parse(command);
                CommandMappingHandler commandMappingHandler = new CommandMappingHandler(statement, jdbcContext.getMappingHandler(), parameters);
                ExpressionDeParser expressionDeParser = new CommandExpressionDeParser();
                SelectDeParser selectDeParser = new CommandSelectDeParser(expressionDeParser, buffer, commandMappingHandler);
                expressionDeParser.setSelectVisitor(selectDeParser);
                expressionDeParser.setBuffer(buffer);
                statement.accept(new CommandStatementDeParser(expressionDeParser, selectDeParser, buffer, commandMappingHandler));
                convertedCommand = buffer.toString();
            } catch (Exception e) {
                throw new SonsureJdbcException("Parsing sql failed:" + command, e);
            }
        }
        return convertedCommand;
    }

}

/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.sql;

import com.sonsure.dumper.common.utils.StrUtils;
import com.sonsure.dumper.core.command.build.CmdParameter;
import com.sonsure.dumper.core.mapping.MappingHandler;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.update.UpdateSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CommandMappingHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CommandMappingHandler.class);

    /**
     * 没有表别名时的默认别名 如单表查询时
     */
    private static final String DEFAULT_ALIAS = "_default_alias";

    private final MappingHandler mappingHandler;

    /**
     * 参数本身无任何作用，只在调用mappingHandler时传入，方便分表时确定表名
     */
    private final List<CmdParameter> parameters;

    private final Map<Column, ColumnMapping> mappingColumns = new HashMap<>();
    private final Map<Table, TableMapping> mappingTables = new HashMap<>();

    public CommandMappingHandler(Statement statement, MappingHandler mappingHandler, List<CmdParameter> parameters) {
        this.mappingHandler = mappingHandler;
        this.parameters = parameters;
        this.extractMappings(statement);
    }

    protected void extractMappings(Statement statement) {

        Map<String, Object> mappings = new HashMap<>();
        if (statement instanceof Select) {
            Select select = (Select) statement;
            SelectBody selectBody = select.getSelectBody();
            this.extractMappings(selectBody, mappings);
        } else if (statement instanceof Insert) {

            Insert insert = (Insert) statement;
            this.extractTableMappings(insert.getTable(), mappings);

            this.extractColumnMapping(insert.getColumns(), mappings);

            Select select = insert.getSelect();
            if (select != null) {
                this.extractMappings(select);
            }
        } else if (statement instanceof Update) {

            Update update = (Update) statement;

            this.extractTableMappings(update.getTable(), mappings);
            this.extractUpdateSet(update.getUpdateSets(), mappings);
            this.extractExpression(update.getWhere(), mappings);

        } else if (statement instanceof Delete) {

            Delete delete = (Delete) statement;
            this.extractTableMappings(delete.getTable(), mappings);
            this.extractTableMappings(delete.getTables(), mappings);
            this.extractExpression(delete.getWhere(), mappings);
        }
    }

    public String getTableName(Table table) {
        TableMapping tableMapping = mappingTables.get(table);
        if (tableMapping == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No TableMapping found, unconverted table name. table=[{}]", table.getName());
            }
            return table.getName();
        }
        return tableMapping.getMappingName();
    }

    public String getColumnName(Column column) {
        ColumnMapping columnMapping = mappingColumns.get(column);
        if (columnMapping == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No ColumnMapping found, unconverted table name. column=[{}]", column.getColumnName());
            }
            return column.getColumnName();
        }
        return columnMapping.getSmartMappingName();
    }

    private void extractMappings(PlainSelect plainSelect, Map<String, Object> mappings) {
        FromItem fromItem = plainSelect.getFromItem();
        if (fromItem instanceof Table) {
            this.extractTableMappings(((Table) fromItem), mappings);
        }
        List<Join> joins = plainSelect.getJoins();
        if (joins != null) {
            for (Join join : joins) {
                FromItem rightItem = join.getRightItem();
                if (rightItem instanceof Table) {
                    this.extractTableMappings(((Table) rightItem), mappings);
                } else if (rightItem instanceof SubSelect) {
                    this.extractSubSelect(((SubSelect) rightItem), mappings);
                }
                final Collection<Expression> onExpressions = join.getOnExpressions();
                if (onExpressions != null && !onExpressions.isEmpty()) {
                    this.extractExpression(onExpressions, mappings);
                }
            }
        }

        Expression where = plainSelect.getWhere();
        if (where != null) {
            this.extractExpression(where, mappings);
        }

        final GroupByElement groupBy = plainSelect.getGroupBy();
        if (groupBy != null && groupBy.getGroupByExpressionList() != null) {
            final List<Expression> expressions = groupBy.getGroupByExpressionList().getExpressions();
            this.extractExpression(expressions, mappings);
        }

        List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
        if (orderByElements != null) {

            for (OrderByElement orderByElement : orderByElements) {
                Expression expression = orderByElement.getExpression();
                this.extractExpression(expression, mappings);
            }
        }

        Expression having = plainSelect.getHaving();
        if (having != null) {
            this.extractExpression(having, mappings);
        }

        List<SelectItem> selectItems = plainSelect.getSelectItems();
        for (SelectItem selectItem : selectItems) {

            if (selectItem instanceof SelectExpressionItem) {
                SelectExpressionItem expressionItem = (SelectExpressionItem) selectItem;
                Expression expression = expressionItem.getExpression();
                this.extractExpression(expression, mappings);
            }
        }
    }

    protected void extractColumnMapping(List<Column> columns, Map<String, Object> mappings) {
        if (columns == null) {
            return;
        }
        for (Column column : columns) {
            this.extractColumnMapping(column, mappings);
        }
    }

    protected void extractColumnMapping(Column column, Map<String, Object> mappings) {
        Table table = column.getTable();
        String name = (table == null || StrUtils.isBlank(table.getName())) ? DEFAULT_ALIAS : table.getName();
        String mappingName = column.getColumnName();
        Object obj = mappings.get(name);
        if (obj instanceof String) {
            mappingName = mappingHandler.getColumn((String) obj, column.getColumnName());
            mappings.put(column.getColumnName(), mappingName);
        } else if (obj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> subMap = (Map<String, Object>) obj;
            mappingName = (String) subMap.get(column.getColumnName());
        }
        ColumnMapping mappingColumn = new ColumnMapping(column, mappingName);
        mappingColumns.put(column, mappingColumn);
    }

    protected void extractExpression(Collection<Expression> expressions, Map<String, Object> mappings) {
        if (expressions == null) {
            return;
        }
        for (Expression expression : expressions) {
            this.extractExpression(expression, mappings);
        }
    }

    protected void extractExpression(Expression expression, Map<String, Object> mappings) {

        if (expression == null) {
            return;
        }
        if (expression instanceof SubSelect) {
            this.extractSubSelect(((SubSelect) expression), mappings);
        } else if (expression instanceof BinaryExpression) {

            BinaryExpression binaryExpression = (BinaryExpression) expression;
            Expression leftExpression = binaryExpression.getLeftExpression();
            this.extractExpression(leftExpression, mappings);

            Expression rightExpression = binaryExpression.getRightExpression();
            this.extractExpression(rightExpression, mappings);

        } else if (expression instanceof InExpression) {

            InExpression inExpression = (InExpression) expression;
            Expression leftExpression = inExpression.getLeftExpression();
            this.extractExpression(leftExpression, mappings);

            ItemsList rightItemsList = inExpression.getRightItemsList();
            if (rightItemsList instanceof SubSelect) {
                this.extractSubSelect(((SubSelect) rightItemsList), mappings);
            }
            final Expression rightExpression = inExpression.getRightExpression();
            if (rightExpression instanceof SubSelect) {
                this.extractSubSelect(((SubSelect) rightExpression), mappings);
            }
        } else if (expression instanceof IsNullExpression) {
            IsNullExpression isNullExpression = (IsNullExpression) expression;
            Expression leftExpression = isNullExpression.getLeftExpression();
            this.extractExpression(leftExpression, mappings);
        } else if (expression instanceof Function) {
            Function function = (Function) expression;
            ExpressionList parameters = function.getParameters();
            if (parameters != null) {
                this.extractExpression(parameters.getExpressions(), mappings);
            }
        } else if (expression instanceof Parenthesis) {
            Parenthesis parenthesis = (Parenthesis) expression;
            Expression parenthesisExpression = parenthesis.getExpression();
            this.extractExpression(parenthesisExpression, mappings);
        } else if (expression instanceof Column) {
            this.extractColumnMapping(((Column) expression), mappings);
        }
    }

    private void extractMappings(SelectBody selectBody, Map<String, Object> mappings) {

        if (selectBody instanceof PlainSelect) {
            PlainSelect plainSelect = (PlainSelect) selectBody;
            this.extractMappings(plainSelect, mappings);
        }
    }

    private String getTableAliasName(Table table) {
        Alias alias = table.getAlias();
        return alias == null || StrUtils.isBlank(alias.getName()) ? DEFAULT_ALIAS : alias.getName();
    }

    protected void extractTableMappings(List<Table> tables, Map<String, Object> mappings) {
        if (tables == null) {
            return;
        }
        for (Table table : tables) {
            this.extractTableMappings(table, mappings);
        }
    }

    private void extractTableMappings(Table table, Map<String, Object> mappings) {
        String tableAliasName = this.getTableAliasName(table);
        mappings.put(tableAliasName, table.getName());

        String mappingName = mappingHandler.getTable(table.getName(), this.parameters);
        TableMapping tableMapping = new TableMapping();
        tableMapping.setTable(table);
        tableMapping.setMappingName(mappingName);

        mappingTables.put(table, tableMapping);
    }

    private void extractSubSelect(SubSelect subSelect, Map<String, Object> mappings) {

        Map<String, Object> subMappings = new HashMap<>();
        Alias alias = subSelect.getAlias();
        //where中的subSelect
        if (alias != null) {
            mappings.put(alias.getName(), subMappings);
        }
        SelectBody selectBody = subSelect.getSelectBody();
        this.extractMappings(selectBody, subMappings);
    }

    private void extractUpdateSet(ArrayList<UpdateSet> updateSets, Map<String, Object> mappings) {
        for (UpdateSet updateSet : updateSets) {
            this.extractUpdateSet(updateSet, mappings);
        }
    }

    private void extractUpdateSet(UpdateSet updateSet, Map<String, Object> mappings) {
        this.extractColumnMapping(updateSet.getColumns(), mappings);
        this.extractExpression(updateSet.getExpressions(), mappings);
    }
}

/*
 *    Copyright 2009-2024 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.sonsure.dumper.core.third.mybatis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * @author Clinton Begin
 * @author Jeff Butler
 * @author Adam Gent
 * @author Kazuki Shimizu
 */
public abstract class AbstractSQL<T> {

    private static final String AND = " AND ";
    private static final String OR = " OR ";
    private static final String OPEN_PAREN = "(";
    private static final String CLOSE_PAREN = ")";
    private static final List<String> DEFINE_KEYS = Arrays.asList(AND, OR, OPEN_PAREN, CLOSE_PAREN);

    private final SQLStatement sql = new SQLStatement();

    /**
     * Gets self.
     *
     * @return the self
     */
    public abstract T getSelf();

    public T update(String table) {
        sql().statementType = SQLStatement.StatementType.UPDATE;
        sql().tables.add(table);
        return getSelf();
    }

    /**
     * Sets the.
     *
     * @param sets the sets
     * @return the t
     * @since 3.4.2
     */
    public T set(String... sets) {
        Collections.addAll(sql().sets, sets);
        return getSelf();
    }

    public T insertInto(String tableName) {
        sql().statementType = SQLStatement.StatementType.INSERT;
        sql().tables.add(tableName);
        return getSelf();
    }

    public T values(String columns, String values) {
        intoColumns(columns);
        intoValues(values);
        return getSelf();
    }

    /**
     * Into columns.
     *
     * @param columns the columns
     * @return the t
     * @since 3.4.2
     */
    public T intoColumns(String... columns) {
        Collections.addAll(sql().columns, columns);
        return getSelf();
    }

    /**
     * Into values.
     *
     * @param values the values
     * @return the t
     * @since 3.4.2
     */
    public T intoValues(String... values) {
        List<String> list = sql().valuesList.get(sql().valuesList.size() - 1);
        Collections.addAll(list, values);
        return getSelf();
    }

    /**
     * Select.
     *
     * @param columns the columns
     * @return the t
     * @since 3.4.2
     */
    public T select(String... columns) {
        sql().statementType = SQLStatement.StatementType.SELECT;
        Collections.addAll(sql().select, columns);
        return getSelf();
    }

    /**
     * Select distinct.
     *
     * @param columns the columns
     * @return the t
     * @since 3.4.2
     */
    public T selectDistinct(String... columns) {
        sql().distinct = true;
        select(columns);
        return getSelf();
    }

    public T deleteFrom(String table) {
        sql().statementType = SQLStatement.StatementType.DELETE;
        sql().tables.add(table);
        return getSelf();
    }

    /**
     * From.
     *
     * @param tables the tables
     * @return the t
     * @since 3.4.2
     */
    public T from(String... tables) {
        Collections.addAll(sql().tables, tables);
        return getSelf();
    }

    /**
     * Join.
     *
     * @param joins the joins
     * @return the t
     * @since 3.4.2
     */
    public T join(String... joins) {
        Collections.addAll(sql().join, joins);
        return getSelf();
    }

    /**
     * Inner join.
     *
     * @param joins the joins
     * @return the t
     * @since 3.4.2
     */
    public T innerJoin(String... joins) {
        Collections.addAll(sql().innerJoin, joins);
        return getSelf();
    }

    /**
     * Left outer join.
     *
     * @param joins the joins
     * @return the t
     * @since 3.4.2
     */
    public T leftOuterJoin(String... joins) {
        Collections.addAll(sql().leftOuterJoin, joins);
        return getSelf();
    }

    /**
     * Right outer join.
     *
     * @param joins the joins
     * @return the t
     * @since 3.4.2
     */
    public T rightOuterJoin(String... joins) {
        Collections.addAll(sql().rightOuterJoin, joins);
        return getSelf();
    }

    /**
     * Outer join.
     *
     * @param joins the joins
     * @return the t
     * @since 3.4.2
     */
    public T outerJoin(String... joins) {
        Collections.addAll(sql().outerJoin, joins);
        return getSelf();
    }

    public T where() {
        sql().lastList = sql().where;
        return getSelf();
    }

    /**
     * Where.
     *
     * @param conditions the conditions
     * @return the t
     * @since 3.4.2
     */
    public T where(String... conditions) {
        Collections.addAll(sql().where, conditions);
        sql().lastList = sql().where;
        return getSelf();
    }

    public T or() {
        if (!sql().lastList.isEmpty()) {
            sql().lastList.add(OR);
        }

        return getSelf();
    }

    public T or(String... conditions) {
        this.or();
        Collections.addAll(sql().lastList, conditions);
        return getSelf();
    }

    public T and() {
        if (!sql().lastList.isEmpty()) {
            sql().lastList.add(AND);
        }
        return getSelf();
    }

    public T and(String... conditions) {
        this.and();
        Collections.addAll(sql().lastList, conditions);
        return getSelf();
    }

    public T openParen() {
        sql().lastList.add(OPEN_PAREN);
        return getSelf();
    }

    public T closeParen() {
        sql().lastList.add(CLOSE_PAREN);
        return getSelf();
    }

    /**
     * Group by.
     *
     * @param columns the columns
     * @return the t
     * @since 3.4.2
     */
    public T groupBy(String... columns) {
        Collections.addAll(sql().groupBy, columns);
        return getSelf();
    }

    /**
     * Having.
     *
     * @param conditions the conditions
     * @return the t
     * @since 3.4.2
     */
    public T having(String... conditions) {
        Collections.addAll(sql().having, conditions);
        sql().lastList = sql().having;
        return getSelf();
    }

    /**
     * Order by.
     *
     * @param columns the columns
     * @return the t
     * @since 3.4.2
     */
    public T orderBy(String... columns) {
        Collections.addAll(sql().orderBy, columns);
        return getSelf();
    }

    /**
     * Set the limit variable string(e.g. {@code "#{limit}"}).
     *
     * @param variable a limit variable string
     * @return a self instance
     * @see #offset(String)
     * @since 3.5.2
     */
    public T limit(String variable) {
        sql().limit = variable;
        sql().limitingRowsStrategy = SQLStatement.LimitingRowsStrategy.OFFSET_LIMIT;
        return getSelf();
    }

    /**
     * Set the limit value.
     *
     * @param value an offset value
     * @return a self instance
     * @see #offset(long)
     * @since 3.5.2
     */
    public T limit(int value) {
        return limit(String.valueOf(value));
    }

    /**
     * Set the offset variable string(e.g. {@code "#{offset}"}).
     *
     * @param variable a offset variable string
     * @return a self instance
     * @see #limit(String)
     * @since 3.5.2
     */
    public T offset(String variable) {
        sql().offset = variable;
        sql().limitingRowsStrategy = SQLStatement.LimitingRowsStrategy.OFFSET_LIMIT;
        return getSelf();
    }

    /**
     * Set the offset value.
     *
     * @param value an offset value
     * @return a self instance
     * @see #limit(int)
     * @since 3.5.2
     */
    public T offset(long value) {
        return offset(String.valueOf(value));
    }

    /**
     * Set the fetch first rows variable string(e.g. {@code "#{fetchFirstRows}"}).
     *
     * @param variable a fetch first rows variable string
     * @return a self instance
     * @see #offsetRows(String)
     * @since 3.5.2
     */
    public T fetchFirstRowsOnly(String variable) {
        sql().limit = variable;
        sql().limitingRowsStrategy = SQLStatement.LimitingRowsStrategy.ISO;
        return getSelf();
    }

    /**
     * Set the fetch first rows value.
     *
     * @param value a fetch first rows value
     * @return a self instance
     * @see #offsetRows(long)
     * @since 3.5.2
     */
    public T fetchFirstRowsOnly(int value) {
        return fetchFirstRowsOnly(String.valueOf(value));
    }

    /**
     * Set the offset rows variable string(e.g. {@code "#{offset}"}).
     *
     * @param variable a offset rows variable string
     * @return a self instance
     * @see #fetchFirstRowsOnly(String)
     * @since 3.5.2
     */
    public T offsetRows(String variable) {
        sql().offset = variable;
        sql().limitingRowsStrategy = SQLStatement.LimitingRowsStrategy.ISO;
        return getSelf();
    }

    /**
     * Set the offset rows value.
     *
     * @param value an offset rows value
     * @return a self instance
     * @see #fetchFirstRowsOnly(int)
     * @since 3.5.2
     */
    public T offsetRows(long value) {
        return offsetRows(String.valueOf(value));
    }

    /**
     * used to add a new inserted row while do multi-row insert.
     *
     * @return the t
     * @since 3.5.2
     */
    public T addRow() {
        sql().valuesList.add(new ArrayList<>());
        return getSelf();
    }

    protected SQLStatement sql() {
        return sql;
    }

    public <A extends Appendable> A usingAppender(A a) {
        sql().sql(a);
        return a;
    }

    /**
     * Apply sql phrases that provide by SQL consumer if condition is matches.
     *
     * @param applyCondition if {@code true} apply sql phrases
     * @param sqlConsumer    a consumer that append sql phrase to SQL instance
     * @return a self instance
     * @see #applyIf(BooleanSupplier, Consumer)
     * @since 3.5.15
     */
    public T applyIf(boolean applyCondition, Consumer<T> sqlConsumer) {
        T self = getSelf();
        if (applyCondition) {
            sqlConsumer.accept(self);
        }
        return self;
    }

    /**
     * Apply sql phrases that provide by SQL consumer if condition is matches.
     *
     * @param applyConditionSupplier if supplier return {@code true} apply sql phrases
     * @param sqlConsumer            a consumer that append sql phrase to SQL instance
     * @return a self instance
     * @see #applyIf(boolean, Consumer)
     * @since 3.5.15
     */
    public T applyIf(BooleanSupplier applyConditionSupplier, Consumer<T> sqlConsumer) {
        return applyIf(applyConditionSupplier.getAsBoolean(), sqlConsumer);
    }

    /**
     * Apply sql phrases that provide by SQL consumer for iterable.
     *
     * @param iterable           an iterable
     * @param forEachSqlConsumer a consumer that append sql phrase to SQL instance
     * @param <E>                element type of iterable
     * @return a self instance
     * @since 3.5.15
     */
    public <E> T applyForEach(Iterable<E> iterable, ForEachConsumer<T, E> forEachSqlConsumer) {
        T self = getSelf();
        int elementIndex = 0;
        for (E element : iterable) {
            forEachSqlConsumer.accept(self, element, elementIndex);
            elementIndex++;
        }
        return self;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sql().sql(sb);
        return sb.toString();
    }

    private static class SafeAppendable {
        private final Appendable appendable;
        private boolean empty = true;

        public SafeAppendable(Appendable a) {
            this.appendable = a;
        }

        public SafeAppendable append(CharSequence s) {
            try {
                if (empty && s.length() > 0) {
                    empty = false;
                }
                appendable.append(s);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public boolean isEmpty() {
            return empty;
        }

    }

    protected static class SQLStatement {

        public enum StatementType {

            DELETE,

            INSERT,

            SELECT,

            UPDATE

        }

        private enum LimitingRowsStrategy {
            NOP {
                @Override
                protected void appendClause(SafeAppendable builder, String offset, String limit) {
                    // NOP
                }
            },
            ISO {
                @Override
                protected void appendClause(SafeAppendable builder, String offset, String limit) {
                    if (offset != null) {
                        builder.append(" OFFSET ").append(offset).append(" ROWS");
                    }
                    if (limit != null) {
                        builder.append(" FETCH FIRST ").append(limit).append(" ROWS ONLY");
                    }
                }
            },
            OFFSET_LIMIT {
                @Override
                protected void appendClause(SafeAppendable builder, String offset, String limit) {
                    if (limit != null) {
                        builder.append(" LIMIT ").append(limit);
                    }
                    if (offset != null) {
                        builder.append(" OFFSET ").append(offset);
                    }
                }
            };

            protected abstract void appendClause(SafeAppendable builder, String offset, String limit);

        }

        StatementType statementType;
        List<String> sets = new ArrayList<>();
        List<String> select = new ArrayList<>();
        List<String> tables = new ArrayList<>();
        List<String> join = new ArrayList<>();
        List<String> innerJoin = new ArrayList<>();
        List<String> outerJoin = new ArrayList<>();
        List<String> leftOuterJoin = new ArrayList<>();
        List<String> rightOuterJoin = new ArrayList<>();
        List<String> where = new ArrayList<>();
        List<String> having = new ArrayList<>();
        List<String> groupBy = new ArrayList<>();
        List<String> orderBy = new ArrayList<>();
        List<String> lastList = new ArrayList<>();
        List<String> columns = new ArrayList<>();
        List<List<String>> valuesList = new ArrayList<>();
        boolean distinct;
        String offset;
        String limit;
        LimitingRowsStrategy limitingRowsStrategy = LimitingRowsStrategy.NOP;

        public SQLStatement() {
            // Prevent Synthetic Access
            valuesList.add(new ArrayList<>());
        }

        private void sqlClause(SafeAppendable builder, String keyword, List<String> parts, String open, String close,
                               String conjunction) {
            if (!parts.isEmpty()) {
                if (!builder.isEmpty()) {
                    builder.append("\n");
                }
                builder.append(keyword);
                builder.append(" ");
                builder.append(open);
                String last = "________";
                for (int i = 0, n = parts.size(); i < n; i++) {
                    String part = parts.get(i);
                    if (i > 0 && !DEFINE_KEYS.contains(part) && !DEFINE_KEYS.contains(last)) {
                        builder.append(conjunction);
                    }
                    builder.append(part);
                    last = part;
                }
                builder.append(close);
            }
        }

        private String selectSQL(SafeAppendable builder) {
            if (distinct) {
                sqlClause(builder, "SELECT DISTINCT", select, "", "", ", ");
            } else {
                sqlClause(builder, "SELECT", select, "", "", ", ");
            }

            sqlClause(builder, "FROM", tables, "", "", ", ");
            joins(builder);
            sqlClause(builder, "WHERE", where, "", "", " AND ");
            sqlClause(builder, "GROUP BY", groupBy, "", "", ", ");
            sqlClause(builder, "HAVING", having, "", "", " AND ");
            sqlClause(builder, "ORDER BY", orderBy, "", "", ", ");
            limitingRowsStrategy.appendClause(builder, offset, limit);
            return builder.toString();
        }

        private void joins(SafeAppendable builder) {
            sqlClause(builder, "JOIN", join, "", "", "\nJOIN ");
            sqlClause(builder, "INNER JOIN", innerJoin, "", "", "\nINNER JOIN ");
            sqlClause(builder, "OUTER JOIN", outerJoin, "", "", "\nOUTER JOIN ");
            sqlClause(builder, "LEFT OUTER JOIN", leftOuterJoin, "", "", "\nLEFT OUTER JOIN ");
            sqlClause(builder, "RIGHT OUTER JOIN", rightOuterJoin, "", "", "\nRIGHT OUTER JOIN ");
        }

        private String insertSQL(SafeAppendable builder) {
            sqlClause(builder, "INSERT INTO", tables, "", "", "");
            sqlClause(builder, "", columns, "(", ")", ", ");
            for (int i = 0; i < valuesList.size(); i++) {
                sqlClause(builder, i > 0 ? "," : "VALUES", valuesList.get(i), "(", ")", ", ");
            }
            return builder.toString();
        }

        private String deleteSQL(SafeAppendable builder) {
            sqlClause(builder, "DELETE FROM", tables, "", "", "");
            sqlClause(builder, "WHERE", where, "", "", " AND ");
            limitingRowsStrategy.appendClause(builder, null, limit);
            return builder.toString();
        }

        private String updateSQL(SafeAppendable builder) {
            sqlClause(builder, "UPDATE", tables, "", "", "");
            joins(builder);
            sqlClause(builder, "SET", sets, "", "", ", ");
            sqlClause(builder, "WHERE", where, "", "", " AND ");
            limitingRowsStrategy.appendClause(builder, null, limit);
            return builder.toString();
        }

        public String sql(Appendable a) {
            SafeAppendable builder = new SafeAppendable(a);
            if (statementType == null) {
                return null;
            }

            String answer;

            switch (statementType) {
                case DELETE:
                    answer = deleteSQL(builder);
                    break;

                case INSERT:
                    answer = insertSQL(builder);
                    break;

                case SELECT:
                    answer = selectSQL(builder);
                    break;

                case UPDATE:
                    answer = updateSQL(builder);
                    break;

                default:
                    answer = null;
            }

            return answer;
        }
    }

    /**
     * Consumer for 'forEach' operation.
     *
     * @param <T> SQL type
     * @param <E> Element type of iterable
     * @since 3.5.15
     */
    public interface ForEachConsumer<T, E> {

        /**
         * Accept an iterable element with index.
         *
         * @param sql          SQL instance
         * @param element      an iterable element
         * @param elementIndex an element index
         */
        void accept(T sql, E element, int elementIndex);

    }

}

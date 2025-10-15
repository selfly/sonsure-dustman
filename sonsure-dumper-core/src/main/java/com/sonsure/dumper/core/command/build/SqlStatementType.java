package com.sonsure.dumper.core.command.build;

/**
 * @author selfly
 */

public enum SqlStatementType {

    /**
     * Set sql statement.
     */
    SET,

    SELECT,

    TABLE,

    JOIN,

    INNER_JOIN,

    OUTER_JOIN,

    LEFT_OUTER_JOIN,

    RIGHT_OUTER_JOIN,

    WHERE,

    HAVING,

    GROUP_BY,

    ORDER_BY;

//    List<String> sets = new ArrayList<>();
//    List<String> select = new ArrayList<>();
//    List<String> tables = new ArrayList<>();
//    List<String> join = new ArrayList<>();
//    List<String> innerJoin = new ArrayList<>();
//    List<String> outerJoin = new ArrayList<>();
//    List<String> leftOuterJoin = new ArrayList<>();
//    List<String> rightOuterJoin = new ArrayList<>();
//    List<String> where = new ArrayList<>();
//    List<String> having = new ArrayList<>();
//    List<String> groupBy = new ArrayList<>();
//    List<String> orderBy = new ArrayList<>();
//    List<String> lastList = new ArrayList<>();
//    List<String> columns = new ArrayList<>();
//    List<List<String>> valuesList = new ArrayList<>();
//    boolean distinct;
//    String offset;
//    String limit;
//    AbstractSQL.SQLStatement.LimitingRowsStrategy limitingRowsStrategy = AbstractSQL.SQLStatement.LimitingRowsStrategy.NOP;
}

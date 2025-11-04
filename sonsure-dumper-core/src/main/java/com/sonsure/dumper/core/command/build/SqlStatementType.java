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
}

package com.sonsure.dumper.core.config;

/**
 * @author selfly
 */
public enum DatabaseDialect {

    /**
     * Sqlite database dialect.
     */
    SQLITE("sqlite"),

    H2("h2"),

    MYSQL("mysql"),

    ORACLE("oracle"),

    POSTGRESQL("postgresql"),

    SQL_SERVER("sql server");

    private final String code;

    DatabaseDialect(String code) {
        this.code = code;
    }

    public boolean belong(String dialect) {
        return dialect.toLowerCase().contains(this.getCode());
    }

    public String getCode() {
        return code;
    }
}

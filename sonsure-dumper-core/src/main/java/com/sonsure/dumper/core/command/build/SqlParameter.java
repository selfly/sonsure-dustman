package com.sonsure.dumper.core.command.build;

/**
 * The type Sql parameter.
 *
 * @author selfly
 */
public class SqlParameter {

    /**
     * The Name.
     */
    String name;

    /**
     * The Value.
     */
    Object value;

    public SqlParameter(String name, Object value) {
        this.name = name;
        this.value = value;
    }
}

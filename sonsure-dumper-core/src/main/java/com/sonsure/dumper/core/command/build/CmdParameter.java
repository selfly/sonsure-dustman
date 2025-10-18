package com.sonsure.dumper.core.command.build;

import lombok.Getter;

/**
 * The type Sql parameter.
 *
 * @author selfly
 */
@Getter
public class CmdParameter {

    /**
     * The Name.
     */
    String name;

    /**
     * The Value.
     */
    Object value;

    public CmdParameter(String name, Object value) {
        this.name = name;
        this.value = value;
    }
}

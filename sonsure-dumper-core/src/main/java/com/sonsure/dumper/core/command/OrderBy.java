package com.sonsure.dumper.core.command;

import lombok.Getter;

/**
 * @author selfly
 * <p>
 * The enum Order by type.
 */
@Getter
public enum OrderBy {

    /**
     * Asc order by type.
     */
    ASC("asc"),

    DESC("desc");

    private final String code;

    OrderBy(String code) {
        this.code = code;
    }
}

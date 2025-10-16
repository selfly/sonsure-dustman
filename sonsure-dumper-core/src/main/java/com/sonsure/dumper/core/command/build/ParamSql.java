package com.sonsure.dumper.core.command.build;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author selfly
 */
@AllArgsConstructor
@Getter
public class ParamSql {

    private final String sql;
    private final List<SqlParameter> sqlParameters;

}

package com.sonsure.dumper.core.page;

import com.sonsure.dumper.core.config.DatabaseDialect;

/**
 * @author selfly
 */
public class H2PageHandler extends MysqlPageHandler {

    @Override
    public boolean support(String dialect) {
        return DatabaseDialect.H2.belong(dialect);
    }

}

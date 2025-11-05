package com.sonsure.dustman.jdbc.page;

import com.sonsure.dustman.jdbc.config.DatabaseDialect;

/**
 * @author selfly
 */
public class H2PageHandler extends MysqlPageHandler {

    @Override
    public boolean support(String dialect) {
        return DatabaseDialect.H2.belong(dialect);
    }

}

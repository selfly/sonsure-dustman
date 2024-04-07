package com.sonsure.dumper.flyable;

import org.apache.commons.lang3.StringUtils;

/**
 * @author selfly
 * 使用mysql模式
 */
public class H2DatabaseExecutorImpl extends MysqlDatabaseExecutorImpl {

    @Override
    public boolean support(String databaseProduct) {
        return StringUtils.containsIgnoreCase(databaseProduct, "h2");
    }
}

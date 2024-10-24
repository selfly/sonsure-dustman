package com.sonsure.dumper.database;

import org.apache.commons.lang3.StringUtils;

/**
 * @author selfly
 * 使用mysql模式
 */
public class H2DatabaseMigrationTaskExecutorImpl extends MysqlDatabaseMigrationTaskExecutorImpl {

    @Override
    public boolean support(String databaseProduct) {
        return StringUtils.containsIgnoreCase(databaseProduct, "h2");
    }

}

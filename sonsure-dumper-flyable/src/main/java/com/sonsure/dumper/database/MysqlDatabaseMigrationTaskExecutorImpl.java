package com.sonsure.dumper.database;

import org.apache.commons.lang3.StringUtils;

/**
 * @author selfly
 */
public class MysqlDatabaseMigrationTaskExecutorImpl implements DatabaseMigrationTaskExecutor {

    @Override
    public boolean support(String databaseProduct) {
        return StringUtils.containsIgnoreCase(databaseProduct, "mysql");
    }

    @Override
    public String getResourcePattern() {
        return "classpath*:db/migration/mysql/*.sql";
    }
}

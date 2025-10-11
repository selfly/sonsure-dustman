package com.sonsure.dumper.database;


/**
 * @author selfly
 */
public class MysqlDatabaseMigrationTaskExecutorImpl implements DatabaseMigrationTaskExecutor {

    @Override
    public boolean support(String databaseProduct) {
        return databaseProduct.toLowerCase().contains("mysql");
    }

    @Override
    public String getResourcePattern() {
        return "classpath*:db/migration/mysql/*.sql";
    }
}

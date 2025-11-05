package com.sonsure.dustman.database;


/**
 * @author selfly
 */
public class MysqlDatabaseMigrationTaskImpl implements DatabaseMigrationTask {

    @Override
    public boolean support(String databaseProduct) {
        return databaseProduct.toLowerCase().contains("mysql");
    }

    @Override
    public String getResourcePattern() {
        return "classpath*:db/migration/mysql/*.sql";
    }
}

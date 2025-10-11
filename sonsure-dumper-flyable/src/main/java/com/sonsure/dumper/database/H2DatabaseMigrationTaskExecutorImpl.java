package com.sonsure.dumper.database;


/**
 * @author selfly
 * 使用mysql模式
 */
public class H2DatabaseMigrationTaskExecutorImpl extends MysqlDatabaseMigrationTaskExecutorImpl {

    @Override
    public boolean support(String databaseProduct) {
        return databaseProduct.toLowerCase().contains("h2");
    }

}

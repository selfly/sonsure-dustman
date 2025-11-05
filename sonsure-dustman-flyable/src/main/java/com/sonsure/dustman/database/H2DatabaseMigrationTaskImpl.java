package com.sonsure.dustman.database;


/**
 * @author selfly
 * 使用mysql模式
 */
public class H2DatabaseMigrationTaskImpl extends MysqlDatabaseMigrationTaskImpl {

    @Override
    public boolean support(String databaseProduct) {
        return databaseProduct.toLowerCase().contains("h2");
    }

}

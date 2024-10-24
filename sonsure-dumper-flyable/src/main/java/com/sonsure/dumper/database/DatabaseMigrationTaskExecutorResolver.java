package com.sonsure.dumper.database;

import com.sonsure.dumper.exception.FlyableException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author selfly
 */
public class DatabaseMigrationTaskExecutorResolver {

    private final List<DatabaseMigrationTaskExecutor> databaseMigrationTaskExecutors = new ArrayList<>(8);

    public DatabaseMigrationTaskExecutorResolver() {
        this.databaseMigrationTaskExecutors.add(new MysqlDatabaseMigrationTaskExecutorImpl());
        this.databaseMigrationTaskExecutors.add(new H2DatabaseMigrationTaskExecutorImpl());
    }

    public DatabaseMigrationTaskExecutor resolveDatabaseExecutor(String databaseProduct) {
        for (DatabaseMigrationTaskExecutor databaseMigrationTaskExecutor : databaseMigrationTaskExecutors) {
            if (databaseMigrationTaskExecutor.support(databaseProduct)) {
                return databaseMigrationTaskExecutor;
            }
        }
        throw new FlyableException("不支持的DatabaseExecutor");
    }

    public void registerDatabaseExecutor(DatabaseMigrationTaskExecutor databaseMigrationTaskExecutor) {
        this.databaseMigrationTaskExecutors.add(databaseMigrationTaskExecutor);
    }
}

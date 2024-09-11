package com.sonsure.dumper.database;

import com.sonsure.dumper.exception.FlyableException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author selfly
 */
public class DatabaseExecutorResolver {

    private final List<DatabaseExecutor> databaseExecutors = new ArrayList<>(8);

    public DatabaseExecutorResolver() {
        this.databaseExecutors.add(new MysqlDatabaseTaskExecutorImpl());
        this.databaseExecutors.add(new H2DatabaseTaskExecutorImpl());
    }

    public DatabaseExecutor resolveDatabaseExecutor(String databaseProduct) {
        for (DatabaseExecutor databaseExecutor : databaseExecutors) {
            if (databaseExecutor.support(databaseProduct)) {
                return databaseExecutor;
            }
        }
        throw new FlyableException("不支持的DatabaseExecutor");
    }

    public void registerDatabaseExecutor(DatabaseExecutor databaseExecutor) {
        this.databaseExecutors.add(databaseExecutor);
    }
}

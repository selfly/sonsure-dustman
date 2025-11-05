package com.sonsure.dustman.database;

import java.util.ArrayList;
import java.util.List;

/**
 * @author selfly
 */
public class NegotiatingDatabaseMigrationTaskImpl implements DatabaseMigrationTask {

    private final List<DatabaseMigrationTask> databaseMigrationTasks = new ArrayList<>(8);
    private DatabaseMigrationTask latestDatabaseMigrationTask;

    public NegotiatingDatabaseMigrationTaskImpl() {
        this.registerDatabaseExecutor(new MysqlDatabaseMigrationTaskImpl());
        this.registerDatabaseExecutor(new H2DatabaseMigrationTaskImpl());
    }

    public void registerDatabaseExecutor(DatabaseMigrationTask databaseMigrationTaskExecutor) {
        this.databaseMigrationTasks.add(databaseMigrationTaskExecutor);
    }

    @Override
    public boolean support(String databaseProduct) {
        for (DatabaseMigrationTask databaseMigrationTask : databaseMigrationTasks) {
            if (databaseMigrationTask.support(databaseProduct)) {
                this.latestDatabaseMigrationTask = databaseMigrationTask;
                return true;
            }
        }
        return false;
    }

    @Override
    public String getResourcePattern() {
        return this.latestDatabaseMigrationTask.getResourcePattern();
    }
}

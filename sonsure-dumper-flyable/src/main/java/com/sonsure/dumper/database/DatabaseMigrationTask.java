package com.sonsure.dumper.database;

import com.sonsure.dumper.core.persist.JdbcDao;
import com.sonsure.dumper.flyable.MigrationTask;
import com.sonsure.dumper.resource.MigrationResource;

import java.nio.charset.StandardCharsets;

/**
 * @author selfly
 */
public interface DatabaseMigrationTask extends MigrationTask {

    /**
     * Support boolean.
     *
     * @param databaseProduct the database product
     * @return the boolean
     */
    boolean support(String databaseProduct);

    /**
     * Table exists boolean.
     *
     * @param jdbcDao        the jdbc dao
     * @param flyableHistory the flyable history
     * @return the boolean
     */
    default boolean isHistoryTableExists(JdbcDao jdbcDao, String flyableHistory) {
        return jdbcDao.executeInConnection(connection -> connection.getMetaData()
                .getTables(null, null, flyableHistory, new String[]{"TABLE"}).next());
    }

    /**
     * Execute resource.
     *
     * @param jdbcDao  the jdbc dao
     * @param resource the resource
     */
    @Override
    default void execute(JdbcDao jdbcDao, MigrationResource resource) {
        jdbcDao.executeScript(resource.getResourceContent(StandardCharsets.UTF_8));
    }

}

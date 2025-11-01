package com.sonsure.dumper.database;

import com.sonsure.dumper.core.persist.JdbcDao;
import com.sonsure.dumper.flyable.MigrationTaskExecutor;
import com.sonsure.dumper.resource.MigrationResource;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;

/**
 * @author selfly
 */
public interface DatabaseMigrationTaskExecutor extends MigrationTaskExecutor {

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
    @SneakyThrows
    default boolean existFlyableHistoryTable(JdbcDao jdbcDao, String flyableHistory) {
        try (Connection conn = jdbcDao.getJdbcContext().getDataSource().getConnection()) {
            return conn.getMetaData()
                    .getTables(null, null, flyableHistory, new String[]{"TABLE"}).next();
        }
    }

    /**
     * Execute resource.
     *
     * @param jdbcDao  the jdbc dao
     * @param resource the resource
     */
    @Override
    default void executeResource(JdbcDao jdbcDao, MigrationResource resource) {
        jdbcDao.executeScript(resource.getResourceContent(StandardCharsets.UTF_8));
    }

}

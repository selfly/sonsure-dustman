package com.sonsure.dumper.flyable;

import com.sonsure.dumper.core.persist.JdbcDao;

import java.util.List;

/**
 * @author selfly
 */
public interface DatabaseExecutor {

    /**
     * Support boolean.
     *
     * @param databaseProduct the database product
     * @return the boolean
     */
    boolean support(String databaseProduct);

    /**
     * Gets resources.
     *
     * @return the resources
     */
    List<MigrationResource> getMigrationResources();

    /**
     * Table exists boolean.
     *
     * @param jdbcDao        the jdbc dao
     * @param flyableHistory the flyable history
     * @return the boolean
     */
    boolean existFlyableHistory(JdbcDao jdbcDao, String flyableHistory);

    /**
     * Execute script.
     *
     * @param jdbcDao  the jdbc dao
     * @param resource the resource
     */
    void executeResource(JdbcDao jdbcDao, MigrationResource resource);
}

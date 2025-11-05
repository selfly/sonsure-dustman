package com.sonsure.dustman.flyable;

import com.sonsure.dustman.jdbc.persist.JdbcDao;
import com.sonsure.dustman.resource.MigrationResource;

/**
 * @author selfly
 */
public interface MigrationTask {

    /**
     * Get resource pattern string.
     *
     * @return the string
     */
    String getResourcePattern();

    /**
     * Execute resource.
     *
     * @param jdbcDao  the jdbc dao
     * @param resource the resource
     */
    void execute(JdbcDao jdbcDao, MigrationResource resource);
}

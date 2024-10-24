package com.sonsure.dumper.flyable;

import com.sonsure.dumper.core.persist.JdbcDao;
import com.sonsure.dumper.resource.MigrationResource;

/**
 * @author selfly
 */
public interface MigrationTaskExecutor {

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
    void executeResource(JdbcDao jdbcDao, MigrationResource resource);
}

package com.sonsure.dumper.flyable;

import com.sonsure.dumper.core.persist.JdbcDao;
import com.sonsure.dumper.resource.MigrationResource;

import java.nio.charset.StandardCharsets;

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
     * Execute script.
     *
     * @param jdbcDao  the jdbc dao
     * @param resource the resource
     */
    default void executeResource(JdbcDao jdbcDao, MigrationResource resource){
        jdbcDao.executeScript(resource.getResourceContent(StandardCharsets.UTF_8));
    }
}

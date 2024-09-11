package com.sonsure.dumper.resource;

import java.util.List;

/**
 * @author selfly
 */
public interface MigrationResourceResolver {

    /**
     * Gets resources.
     *
     * @param resourcePattern the resource pattern
     * @return the resources
     */
    List<MigrationResource> resolveMigrationResources(String resourcePattern);
}

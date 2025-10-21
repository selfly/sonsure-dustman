package com.sonsure.dumper.common.utility;

import java.util.List;

/**
 * @author selfly
 */
public interface GenericResourcePatternResolver {

    /**
     * Gets resources.
     *
     * @param locationPattern the location pattern
     * @return the resources
     */
    List<GenericResource> getResources(String locationPattern);

    /**
     * Gets resources.
     *
     * @param packageName the package name
     * @return the resources
     */
    List<Class<?>> getResourcesClasses(String packageName);


}

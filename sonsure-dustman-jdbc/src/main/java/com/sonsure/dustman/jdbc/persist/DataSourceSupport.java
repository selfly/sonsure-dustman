package com.sonsure.dustman.jdbc.persist;

import javax.sql.DataSource;

/**
 * @author selfly
 */
public interface DataSourceSupport {

    /**
     * Gets data source.
     *
     * @return the data source
     */
    DataSource getDataSource();
}

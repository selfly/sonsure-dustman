package com.sonsure.dumper.database;

import com.sonsure.dumper.core.persist.JdbcDao;
import lombok.SneakyThrows;

import java.sql.Connection;

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
     * Table exists boolean.
     *
     * @param jdbcDao        the jdbc dao
     * @param flyableHistory the flyable history
     * @return the boolean
     */
    @SneakyThrows
    default boolean existFlyableHistoryTable(JdbcDao jdbcDao, String flyableHistory) {
        try (Connection conn = jdbcDao.getDataSource().getConnection()) {
            return conn.getMetaData()
                    .getTables(null, null, flyableHistory, new String[]{"TABLE"}).next();
        }
    }

}

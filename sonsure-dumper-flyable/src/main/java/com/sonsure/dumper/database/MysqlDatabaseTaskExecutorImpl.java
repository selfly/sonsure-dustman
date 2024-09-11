package com.sonsure.dumper.database;

import com.sonsure.dumper.flyable.MigrationTaskExecutor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author selfly
 */
public class MysqlDatabaseTaskExecutorImpl implements DatabaseExecutor, MigrationTaskExecutor {

    @Override
    public boolean support(String databaseProduct) {
        return StringUtils.containsIgnoreCase(databaseProduct, "mysql");
    }

//    /**
//     * Table exists boolean.
//     *
//     * @param jdbcDao        the jdbc dao
//     * @param flyableHistory the flyable history
//     * @return the boolean
//     */
//    @Override
//    public boolean existFlyableHistoryTable(JdbcDao jdbcDao, String flyableHistory) {
//        Map<String, Object> map = jdbcDao.nativeExecutor()
//                .command("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ?")
//                .parameters(flyableHistory)
//                .nativeCommand()
//                .singleMapResult();
//        return map != null && !map.isEmpty();
//    }

    @Override
    public String getResourcePattern() {
        return "classpath*:db/migration/mysql/*.sql";
    }
}

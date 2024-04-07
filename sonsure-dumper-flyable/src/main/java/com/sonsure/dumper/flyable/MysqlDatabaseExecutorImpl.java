package com.sonsure.dumper.flyable;

import com.sonsure.dumper.core.persist.JdbcDao;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author selfly
 */
public class MysqlDatabaseExecutorImpl extends AbstractDatabaseExecutor {

    public MysqlDatabaseExecutorImpl() {
        super("classpath*:db/migration/mysql/*.sql");
    }

    @Override
    public boolean support(String databaseProduct) {
        return StringUtils.containsIgnoreCase(databaseProduct, "mysql");
    }


    /**
     * Table exists boolean.
     *
     * @param jdbcDao        the jdbc dao
     * @param flyableHistory the flyable history
     * @return the boolean
     */
    @Override
    public boolean existFlyableHistory(JdbcDao jdbcDao, String flyableHistory) {
        Map<String, Object> map = jdbcDao.nativeExecutor()
                .command("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ?")
                .parameters(flyableHistory)
                .nativeCommand()
                .singleResult();
        return map != null && !map.isEmpty();
    }
}

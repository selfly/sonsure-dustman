package com.sonsure.dumper.test.flyable;

import com.sonsure.dumper.core.persist.JdbcDao;
import com.sonsure.dumper.flyable.FlyableExecutor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;

public class FlyableInitializer implements InitializingBean, Ordered {

    private final JdbcDao jdbcDao;

    public FlyableInitializer(JdbcDao jdbcDao) {
        this.jdbcDao = jdbcDao;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        FlyableExecutor flyableExecutor = new FlyableExecutor(jdbcDao);
        flyableExecutor.setFlyablePrefix("sd_");
        flyableExecutor.migrate();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

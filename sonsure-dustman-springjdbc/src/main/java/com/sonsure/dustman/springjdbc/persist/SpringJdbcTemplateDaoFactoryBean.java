package com.sonsure.dustman.springjdbc.persist;

import com.sonsure.dustman.jdbc.config.JdbcContextImpl;
import com.sonsure.dustman.jdbc.persist.JdbcDao;
import com.sonsure.dustman.jdbc.persist.JdbcDaoImpl;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author selfly
 */
public class SpringJdbcTemplateDaoFactoryBean extends JdbcContextImpl implements FactoryBean<JdbcDao> {

    @Override
    public JdbcDao getObject() throws Exception {
        JdbcDaoImpl jdbcDao = new JdbcDaoImpl();
        jdbcDao.setJdbcContext(this);
        return jdbcDao;
    }

    @Override
    public Class<?> getObjectType() {
        return JdbcDao.class;
    }
}

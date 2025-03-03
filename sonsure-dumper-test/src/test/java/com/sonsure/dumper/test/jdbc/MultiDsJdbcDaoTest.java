/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.jdbc;

import com.sonsure.dumper.core.persist.FlexibleJdbcDaoImpl;
import com.sonsure.dumper.core.persist.JdbcDao;
import com.sonsure.dumper.test.model.OracleUser;
import com.sonsure.dumper.test.model.UserInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;
import java.util.Date;

/**
 * 限于环境，不参与构建
 */
@Disabled
@ContextConfiguration(locations = {"classpath:applicationContext-multi-ds.xml"})
public class MultiDsJdbcDaoTest {

    @Autowired
    private JdbcDao jdbcDao;

    @Test
    public void defaultDsJdbcDaoTest() {

        jdbcDao.executeDelete(UserInfo.class);

        //默认
        for (int i = 0; i < 5; i++) {
            UserInfo user = new UserInfo();
            user.setLoginName("name-" + i);
            user.setUserAge(i);
            user.setPassword("123456-" + i);
            user.setGmtCreate(new Date());
            jdbcDao.executeInsert(user);
        }
        long count = jdbcDao.findCount(UserInfo.class);
        Assertions.assertEquals(count, 5);
    }

    @Test
    public void mysqlDsJdbcDaoTest() {

        jdbcDao.use("mysql").executeDelete(UserInfo.class);

        //默认
        for (int i = 0; i < 5; i++) {
            UserInfo user = new UserInfo();
            user.setLoginName("name-" + i);
            user.setUserAge(i);
            user.setPassword("123456-" + i);
            user.setGmtCreate(new Date());
            jdbcDao.use("mysql").executeInsert(user);
        }
        long count = jdbcDao.use("mysql").findCount(UserInfo.class);
        Assertions.assertEquals(count, 5);
    }

    @Test
    public void oracleDsJdbcDaoTest() {

        jdbcDao.use("oracle").executeDelete(OracleUser.class);

        //默认
        for (int i = 0; i < 5; i++) {
            OracleUser oracleUser = new OracleUser();
            oracleUser.setUsername("liyd");

            jdbcDao.use("oracle").executeInsert(oracleUser);
        }
        long count = jdbcDao.use("oracle").findCount(OracleUser.class);
        Assertions.assertEquals(count, 5);
    }

    @Test
    public void jdbcDefaultDsJdbcDaoTest() {

        jdbcDao.executeDelete(UserInfo.class);

        //默认
        for (int i = 0; i < 5; i++) {
            UserInfo user = new UserInfo();
            user.setLoginName("name-" + i);
            user.setUserAge(i);
            user.setPassword("123456-" + i);
            user.setGmtCreate(new Date());
            jdbcDao.executeInsert(user);
        }
        long count = jdbcDao.findCount(UserInfo.class);
        Assertions.assertEquals(count, 5);
    }

    @Test
    public void jdbcMysqlDsJdbcDaoTest() {

        jdbcDao.use("mysql").executeDelete(UserInfo.class);

        //默认
        for (int i = 0; i < 5; i++) {
            UserInfo user = new UserInfo();
            user.setLoginName("name-" + i);
            user.setUserAge(i);
            user.setPassword("123456-" + i);
            user.setGmtCreate(new Date());
            jdbcDao.use("mysql").executeInsert(user);
        }
        long count = jdbcDao.use("mysql").findCount(UserInfo.class);
        Assertions.assertEquals(count, 5);
    }

    @Test
    public void jdbcOracleDsJdbcDaoTest() {

        jdbcDao.use("oracle").executeDelete(OracleUser.class);

        //默认
        for (int i = 0; i < 5; i++) {
            OracleUser oracleUser = new OracleUser();
            oracleUser.setUsername("liyd");

            jdbcDao.use("oracle").executeInsert(oracleUser);
        }
        long count = jdbcDao.use("oracle").findCount(OracleUser.class);
        Assertions.assertEquals(count, 5);
    }

    @Test
    public void use() {

        try {
            jdbcDao.use("oracle").use("oracle").executeDelete(OracleUser.class);
        } catch (UnsupportedOperationException e) {
            Assertions.assertEquals("不支持的方法", e.getMessage());
        }
    }

    @Test
    public void getDataSource() {

        DataSource dataSource = ((FlexibleJdbcDaoImpl) jdbcDao.use("oracle")).getDefaultJdbcEngine().getDataSource();
        Assertions.assertNotNull(dataSource);
    }
}

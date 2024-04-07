/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.jdbc;

import com.sonsure.dumper.core.persist.DaoTemplate;
import com.sonsure.dumper.core.persist.FlexibleDaoTemplate;
import com.sonsure.dumper.test.model.OracleUser;
import com.sonsure.dumper.test.model.UserInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;
import java.util.Date;

/**
 * 限于环境，不参与构建
 */
//@Ignore
//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext-multi-ds.xml"})
public class MultiDsJdbcDaoTest {

    @Autowired
    private DaoTemplate daoTemplate;

    @Test
    public void defaultDsJdbcDaoTest() {

        daoTemplate.executeDelete(UserInfo.class);

        //默认
        for (int i = 0; i < 5; i++) {
            UserInfo user = new UserInfo();
            user.setLoginName("name-" + i);
            user.setUserAge(i);
            user.setPassword("123456-" + i);
            user.setGmtCreate(new Date());
            daoTemplate.executeInsert(user);
        }
        long count = daoTemplate.findCount(UserInfo.class);
        Assertions.assertEquals(count, 5);
    }

    @Test
    public void mysqlDsJdbcDaoTest() {

        daoTemplate.use("mysql").executeDelete(UserInfo.class);

        //默认
        for (int i = 0; i < 5; i++) {
            UserInfo user = new UserInfo();
            user.setLoginName("name-" + i);
            user.setUserAge(i);
            user.setPassword("123456-" + i);
            user.setGmtCreate(new Date());
            daoTemplate.use("mysql").executeInsert(user);
        }
        long count = daoTemplate.use("mysql").findCount(UserInfo.class);
        Assertions.assertEquals(count, 5);
    }

    @Test
    public void oracleDsJdbcDaoTest() {

        daoTemplate.use("oracle").executeDelete(OracleUser.class);

        //默认
        for (int i = 0; i < 5; i++) {
            OracleUser oracleUser = new OracleUser();
            oracleUser.setUsername("liyd");

            daoTemplate.use("oracle").executeInsert(oracleUser);
        }
        long count = daoTemplate.use("oracle").findCount(OracleUser.class);
        Assertions.assertEquals(count, 5);
    }

    @Test
    public void jdbcDefaultDsJdbcDaoTest() {

        daoTemplate.executeDelete(UserInfo.class);

        //默认
        for (int i = 0; i < 5; i++) {
            UserInfo user = new UserInfo();
            user.setLoginName("name-" + i);
            user.setUserAge(i);
            user.setPassword("123456-" + i);
            user.setGmtCreate(new Date());
            daoTemplate.executeInsert(user);
        }
        long count = daoTemplate.findCount(UserInfo.class);
        Assertions.assertEquals(count, 5);
    }

    @Test
    public void jdbcMysqlDsJdbcDaoTest() {

        daoTemplate.use("mysql").executeDelete(UserInfo.class);

        //默认
        for (int i = 0; i < 5; i++) {
            UserInfo user = new UserInfo();
            user.setLoginName("name-" + i);
            user.setUserAge(i);
            user.setPassword("123456-" + i);
            user.setGmtCreate(new Date());
            daoTemplate.use("mysql").executeInsert(user);
        }
        long count = daoTemplate.use("mysql").findCount(UserInfo.class);
        Assertions.assertEquals(count, 5);
    }

    @Test
    public void jdbcOracleDsJdbcDaoTest() {

        daoTemplate.use("oracle").executeDelete(OracleUser.class);

        //默认
        for (int i = 0; i < 5; i++) {
            OracleUser oracleUser = new OracleUser();
            oracleUser.setUsername("liyd");

            daoTemplate.use("oracle").executeInsert(oracleUser);
        }
        long count = daoTemplate.use("oracle").findCount(OracleUser.class);
        Assertions.assertEquals(count, 5);
    }

    @Test
    public void use() {

        try {
            daoTemplate.use("oracle").use("oracle").executeDelete(OracleUser.class);
        } catch (UnsupportedOperationException e) {
            Assertions.assertEquals("不支持的方法", e.getMessage());
        }
    }

    @Test
    public void getDataSource() {

        DataSource dataSource = ((FlexibleDaoTemplate) daoTemplate.use("oracle")).getDefaultJdbcEngine().getDataSource();
        Assertions.assertNotNull(dataSource);
    }
}

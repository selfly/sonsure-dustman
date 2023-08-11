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
import com.sonsure.dumper.core.persist.Jdbc;
import com.sonsure.dumper.test.model.TestUser;
import com.sonsure.dumper.test.model.UserInfo;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.util.Date;

/**
 * 限于环境，不参与构建
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
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
        Assert.assertEquals(count, 5);
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
        Assert.assertEquals(count, 5);
    }

    @Test
    public void oracleDsJdbcDaoTest() {

        daoTemplate.use("oracle").executeDelete(TestUser.class);

        //默认
        for (int i = 0; i < 5; i++) {
            TestUser testUser = new TestUser();
            testUser.setUsername("liyd");

            daoTemplate.use("oracle").executeInsert(testUser);
        }
        long count = daoTemplate.use("oracle").findCount(TestUser.class);
        Assert.assertEquals(count, 5);
    }

    @Test
    public void jdbcDefaultDsJdbcDaoTest() {

        Jdbc.executeDelete(UserInfo.class);

        //默认
        for (int i = 0; i < 5; i++) {
            UserInfo user = new UserInfo();
            user.setLoginName("name-" + i);
            user.setUserAge(i);
            user.setPassword("123456-" + i);
            user.setGmtCreate(new Date());
            Jdbc.executeInsert(user);
        }
        long count = Jdbc.findCount(UserInfo.class);
        Assert.assertEquals(count, 5);
    }

    @Test
    public void jdbcMysqlDsJdbcDaoTest() {

        Jdbc.use("mysql").executeDelete(UserInfo.class);

        //默认
        for (int i = 0; i < 5; i++) {
            UserInfo user = new UserInfo();
            user.setLoginName("name-" + i);
            user.setUserAge(i);
            user.setPassword("123456-" + i);
            user.setGmtCreate(new Date());
            Jdbc.use("mysql").executeInsert(user);
        }
        long count = Jdbc.use("mysql").findCount(UserInfo.class);
        Assert.assertEquals(count, 5);
    }

    @Test
    public void jdbcOracleDsJdbcDaoTest() {

        Jdbc.use("oracle").executeDelete(TestUser.class);

        //默认
        for (int i = 0; i < 5; i++) {
            TestUser testUser = new TestUser();
            testUser.setUsername("liyd");

            Jdbc.use("oracle").executeInsert(testUser);
        }
        long count = Jdbc.use("oracle").findCount(TestUser.class);
        Assert.assertEquals(count, 5);
    }

    @Test
    public void use() {

        try {
            daoTemplate.use("oracle").use("oracle").executeDelete(TestUser.class);
        } catch (UnsupportedOperationException e) {
            Assert.assertEquals("不支持的方法", e.getMessage());
        }
    }

    @Test
    public void getDataSource() {

        DataSource dataSource = ((FlexibleDaoTemplate) daoTemplate.use("oracle")).getDefaultJdbcEngine().getDataSource();
        Assert.assertNotNull(dataSource);
    }
}

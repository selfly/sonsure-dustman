/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.jdbc.extension.executor;

import com.sonsure.dumper.core.persist.JdbcDao;
import com.sonsure.dumper.test.model.UserInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

@Disabled
@SpringBootTest
@ContextConfiguration(locations = {"classpath:applicationContext-executor.xml"})
public class CustomExecutorTest {

    @Autowired
    protected JdbcDao jdbcDao;

    @BeforeEach
    public void before() {
        //初始化测试数据
        jdbcDao.deleteFrom(UserInfo.class)
                .execute();
        UserInfo user = new UserInfo();
        user.setUserInfoId(10000L);
        user.setLoginName("name-10000");
        user.setPassword("123456-10000");
        user.setUserAge(18);
        user.setGmtCreate(new Date());

        jdbcDao.executeInsert(user);
    }

    @Test
    public void countExecutorTest() {

        long count = jdbcDao.createExecutor(CountCommandExecutor.class)
                .clazz(UserInfo.class)
                .getCount();

        Assertions.assertTrue(count > 0);
    }
}

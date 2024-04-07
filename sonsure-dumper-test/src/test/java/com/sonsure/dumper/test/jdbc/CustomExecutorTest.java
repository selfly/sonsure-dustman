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
import com.sonsure.dumper.test.executor.CountCommandExecutor;
import com.sonsure.dumper.test.model.UserInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext-executor.xml"})
public class CustomExecutorTest {

    @Autowired
    protected DaoTemplate daoTemplate;

    @BeforeEach
    public void before() {
        //初始化测试数据
        daoTemplate.deleteFrom(UserInfo.class)
                .execute();
        UserInfo user = new UserInfo();
        user.setUserInfoId(10000L);
        user.setLoginName("name-10000");
        user.setPassword("123456-10000");
        user.setUserAge(18);
        user.setGmtCreate(new Date());

        daoTemplate.executeInsert(user);
    }

    @Test
    public void countExecutorTest() {

        long count = daoTemplate.executor(CountCommandExecutor.class)
                .clazz(UserInfo.class)
                .getCount();

        Assertions.assertTrue(count > 0);
    }
}

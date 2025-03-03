/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.jdbc;


import com.sonsure.dumper.core.persist.JdbcDao;
import com.sonsure.dumper.test.model.HbUserInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

/**
 * hibernate测试
 */
@Disabled
@ContextConfiguration(locations = {"classpath:applicationContext-hibernate.xml"})
public class HibernateJdbcDaoTest {

    @Autowired
    private JdbcDao jdbcDao;

    @Test
    public void findList() {
        List<HbUserInfo> userInfos = jdbcDao.find(HbUserInfo.class);
        Assertions.assertNotNull(userInfos);
    }
}

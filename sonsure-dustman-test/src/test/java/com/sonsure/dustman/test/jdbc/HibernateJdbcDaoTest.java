/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.test.jdbc;


import com.sonsure.dustman.jdbc.persist.JdbcDao;
import com.sonsure.dustman.test.model.HbUserInfo;
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
public class HibernateJdbcDaoTest {

    @Autowired
    private JdbcDao jdbcDao;

    @Test
    public void findAllList() {
        List<HbUserInfo> userInfos = jdbcDao.findAll(HbUserInfo.class);
        Assertions.assertNotNull(userInfos);
    }
}

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
import com.sonsure.dumper.test.model.OracleUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 限于环境，oracle不参与构建
 */
@Disabled
@SpringBootTest
public class OracleJdbcTemplateDaoImplTest {

    @Autowired
    @Qualifier("oracleJdbcDao")
    protected JdbcDao jdbcDao;

    @Test
    public void jdbcDaoInsert() {

        OracleUser oracleUser = new OracleUser();
        oracleUser.setUsername("liyd");

        Long id = (Long) jdbcDao.executeInsert(oracleUser);

        Assertions.assertNotNull(id);
    }

}

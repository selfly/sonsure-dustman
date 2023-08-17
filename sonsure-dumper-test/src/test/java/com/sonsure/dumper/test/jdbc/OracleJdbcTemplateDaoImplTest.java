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
import com.sonsure.dumper.test.model.OracleUser;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 限于环境，oracle不参与构建
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext-oracle.xml"})
public class OracleJdbcTemplateDaoImplTest {

    @Autowired
    protected DaoTemplate daoTemplate;

    @Test
    public void jdbcDaoInsert() {

        OracleUser oracleUser = new OracleUser();
        oracleUser.setUsername("liyd");

        Long id = (Long) daoTemplate.executeInsert(oracleUser);

        Assert.assertNotNull(id);
    }

    @Test
    public void oneCol() {
        final Long id = daoTemplate.select(OracleUser::getOracleUserId)
                .from(OracleUser.class)
                .oneColFirstResult(Long.class);
        Assert.assertNotNull(id);
    }

}

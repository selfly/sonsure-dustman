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
import com.sonsure.dumper.test.model.UidUser;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by liyd on 17/4/12.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext-uuid.xml"})
public class UuidJdbcTemplateDaoImplTest {

    @Autowired
    protected DaoTemplate daoTemplate;

    @Test
    public void jdbcDaoInsert() {

        UidUser uidUser = new UidUser();
        uidUser.setLoginName("liyd");

        String id = (String) daoTemplate.executeInsert(uidUser);
        Assert.assertNotNull(id);
    }

}

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
import com.sonsure.dumper.test.model.UuidUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by liyd on 17/4/12.
 */

@Disabled
@ContextConfiguration(locations = {"classpath:applicationContext-uuid.xml"})
public class UuidJdbcTemplateDaoImplTest {

    @Autowired
    protected DaoTemplate daoTemplate;

    @Test
    public void jdbcDaoInsert() {

        UuidUser user = new UuidUser();
        user.setLoginName("liyd");
        user.setPassword("123456");
        String id = (String) daoTemplate.executeInsert(user);
        Assertions.assertNotNull(id);
    }

}

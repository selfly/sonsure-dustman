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
import com.sonsure.dumper.test.model.HbUserInfo;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * rasp下构建失败，暂时忽略
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext-hibernate.xml"})
public class HibernateDaoTemplateTest {

    @Autowired
    private DaoTemplate daoTemplate;

    @Test
    public void findList() {

        List<HbUserInfo> userInfos = daoTemplate.find(HbUserInfo.class);

        System.out.println(userInfos);
    }
}

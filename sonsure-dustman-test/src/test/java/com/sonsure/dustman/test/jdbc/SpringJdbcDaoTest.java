/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.test.jdbc;

import com.sonsure.dustman.common.model.Page;
import com.sonsure.dustman.common.model.Pageable;
import com.sonsure.dustman.jdbc.command.build.BeanParameter;
import com.sonsure.dustman.jdbc.command.build.GetterFunction;
import com.sonsure.dustman.jdbc.command.build.OrderBy;
import com.sonsure.dustman.jdbc.command.build.SqlOperator;
import com.sonsure.dustman.jdbc.command.entity.Select;
import com.sonsure.dustman.jdbc.config.JdbcContext;
import com.sonsure.dustman.jdbc.persist.JdbcDao;
import com.sonsure.dustman.test.basic.BaseTest;
import com.sonsure.dustman.test.config.DustmanTestConfig;
import com.sonsure.dustman.test.jdbc.extension.executor.CustomResultHandler;
import com.sonsure.dustman.test.model.Account;
import com.sonsure.dustman.test.model.AnnotationUserInfo;
import com.sonsure.dustman.test.model.UserInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.IncorrectResultSetColumnCountException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.lang.NonNull;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by liyd on 17/4/12.
 */
public class SpringJdbcDaoTest extends BaseTest {

    protected JdbcDao jdbcDao;

    public SpringJdbcDaoTest(@Qualifier("mysqlJdbcDao") JdbcDao jdbcDao) {
        this.jdbcDao = jdbcDao;
    }

    @BeforeEach
    public void before() {

        jdbcDao.executeDeleteAll(UserInfo.class);
        List<UserInfo> users = new ArrayList<>();
        for (int i = 1; i < 51; i++) {
            UserInfo user = new UserInfo();
            user.setUserInfoId((long) i);
            user.setLoginName("name-" + i);
            user.setPassword("123456-" + i);
            user.setUserAge(i);
            user.setStatus("" + i);
            user.setUserType(String.valueOf(i % 5));
            user.setGmtCreate(new Date());

            users.add(user);
        }

        String sql = "insert into UserInfo(userInfoId,loginName,password,userAge,status,userType,gmtCreate) values(?,?,?,?,?,?,?)";
        Object result = jdbcDao.executeBatchUpdate(sql, users, users.size(), (ps, paramNames, argument) -> {
            ps.setLong(1, argument.getUserInfoId());
            ps.setString(2, argument.getLoginName());
            ps.setString(3, argument.getPassword());
            ps.setInt(4, argument.getUserAge());
            ps.setString(5, argument.getStatus());
            ps.setString(6, argument.getUserType());
            ps.setObject(7, argument.getGmtCreate());
        });
        Assertions.assertNotNull(result);
    }

    @Test
    public void get() {
        UserInfo user = jdbcDao.get(UserInfo.class, 1L);
        Assertions.assertNotNull(user);
        Assertions.assertEquals(1L, (long) user.getUserInfoId());
        Assertions.assertEquals("name-1", user.getLoginName());
        Assertions.assertEquals("123456-1", user.getPassword());
    }

    @Test
    public void executeInsert() {

        UserInfo user = new UserInfo();
        user.setLoginName("liyd2017");
        user.setPassword("2017");
        user.setUserAge(18);
        user.setGmtCreate(new Date());

        Long id = jdbcDao.executeInsert(user);
        Assertions.assertTrue(id > 0);

        UserInfo userInfo = jdbcDao.get(UserInfo.class, id);
        Assertions.assertEquals(userInfo.getUserInfoId(), id);
        Assertions.assertEquals(user.getLoginName(), userInfo.getLoginName());
        Assertions.assertEquals(user.getPassword(), userInfo.getPassword());
    }

    @Test
    public void executeUpdate() {
        UserInfo user = new UserInfo();
        user.setUserInfoId(2L);
        user.setPassword("666666");
        user.setLoginName("666777");
        user.setGmtModify(new Date());
        int count = jdbcDao.executeUpdate(user);
        Assertions.assertEquals(1, count);

        UserInfo user1 = jdbcDao.get(UserInfo.class, 2L);
        Assertions.assertNotNull(user1);
        Assertions.assertEquals(2L, (long) user1.getUserInfoId());
        Assertions.assertEquals("666777", user1.getLoginName());
        Assertions.assertEquals("666666", user1.getPassword());
        Assertions.assertNotNull(user1.getGmtModify());
    }

    @Test
    public void executeDeleteAll() {
        int count = jdbcDao.executeDelete(UserInfo.class, 3L);
        Assertions.assertEquals(1, count);
        UserInfo user = jdbcDao.get(UserInfo.class, 3L);
        Assertions.assertNull(user);
    }

    @Test
    public void findAllForClass() {
        List<UserInfo> users = jdbcDao.findAll(UserInfo.class);
        Assertions.assertNotNull(users);
        long count = jdbcDao.findAllCount(UserInfo.class);
        Assertions.assertEquals(count, users.size());
    }

    @Test
    public void findListForEntityNotNullField() {
        UserInfo user = new UserInfo();
        user.setUserAge(10);
        List<UserInfo> users = jdbcDao.findList(user);
        Assertions.assertNotNull(users);
        Assertions.assertEquals(1, users.size());
    }

    @Test
    public void findPageSetPageSize() {
        UserInfo user = new UserInfo();
        user.setPageSize(10);
        Page<UserInfo> page = jdbcDao.findPage(user);
        Assertions.assertNotNull(page);
        Assertions.assertEquals(10, page.getList().size());
    }


    @Test
    public void jdbcDaoFindPageEntityNotNullFieldCondition() {
        UserInfo user = new UserInfo();
        user.setPageSize(10);
        user.setUserAge(10);
        Page<UserInfo> page = jdbcDao.findPage(user);
        Assertions.assertNotNull(page);
        Assertions.assertEquals(1, page.getList().size());
    }

    @Test
    public void jdbcDaoFindPageSetLimit() {
        Page<UserInfo> page = jdbcDao.selectFrom(UserInfo.class)
                .orderBy("userInfoId", OrderBy.ASC)
                .limit(15, 10)
                .findPage(UserInfo.class);
        Assertions.assertEquals(11L, (long) page.getList().get(0).getUserInfoId());
        Assertions.assertEquals(20L, (long) page.getList().get(9).getUserInfoId());
    }


    @Test
    public void jdbcDaoCountForEntityNotNullField() {
        UserInfo user = new UserInfo();
        user.setUserAge(10);
        long count = jdbcDao.findCount(user);
        Assertions.assertEquals(1, count);
    }

    @Test
    public void jdbcDaoCountAllForClass() {
        long count = jdbcDao.findAllCount(UserInfo.class);
        Assertions.assertEquals(50, count);
    }

    @Test
    public void jdbcDaoFindOneForEntityNotNullField() {
        UserInfo tmp = new UserInfo();
        tmp.setUserAge(10);
        UserInfo user = jdbcDao.findOne(tmp);
        Assertions.assertNotNull(user);
        Assertions.assertEquals(10L, (long) user.getUserInfoId());
        Assertions.assertEquals("name-10", user.getLoginName());
        Assertions.assertEquals(10, (int) user.getUserAge());
        Assertions.assertNotNull(user.getGmtCreate());
    }


    @Test
    public void jdbcDaoFindFirstForEntityNotNullField() {
        UserInfo tmp = new UserInfo();
        tmp.setUserAge(10);
        UserInfo user = jdbcDao.findFirst(tmp);
        Assertions.assertNotNull(user);
        Assertions.assertEquals(10L, (long) user.getUserInfoId());
        Assertions.assertEquals("name-10", user.getLoginName());
        Assertions.assertEquals(10, (int) user.getUserAge());
        Assertions.assertNotNull(user.getGmtCreate());
    }

    @Test
    public void jdbcDaoDeleteForEntityNotNullField() {
        UserInfo user = new UserInfo();
        user.setLoginName("name-17");
        int count = jdbcDao.executeDelete(user);
        Assertions.assertEquals(1, count);

        UserInfo tmp = new UserInfo();
        tmp.setLoginName("name-17");
        UserInfo user1 = jdbcDao.findOne(tmp);
        Assertions.assertNull(user1);
    }

    @Test
    public void jdbcDaoDeleteAllForClass() {
        int count = jdbcDao.executeDeleteAll(UserInfo.class);
        Assertions.assertTrue(count > 0);
        long result = jdbcDao.findAllCount(UserInfo.class);
        Assertions.assertEquals(0, result);
    }

    @Test
    public void insertForSetStrField() {

        Long id = (Long) jdbcDao.insertInto(UserInfo.class)
                .intoField("loginName", "name123")
                .intoField("password", "123321")
                .execute();

        UserInfo user1 = jdbcDao.get(UserInfo.class, id);
        Assertions.assertNotNull(user1);
        Assertions.assertEquals(user1.getUserInfoId(), id);
        Assertions.assertEquals("name123", user1.getLoginName());
        Assertions.assertEquals("123321", user1.getPassword());
    }

    @Test
    public void insertForAnnotation() {

        AnnotationUserInfo ku = new AnnotationUserInfo();
        ku.setLoginName("selfly");
        ku.setPassword("123456");
        ku.setUserAge(18);
        ku.setGmtCreate(new Date());
        ku.setGmtModify(new Date());

        Long id = jdbcDao.executeInsert(ku);

        AnnotationUserInfo annotationUserInfo = jdbcDao.get(AnnotationUserInfo.class, id);
        Assertions.assertEquals("selfly", annotationUserInfo.getLoginName());
        Assertions.assertEquals("123456", annotationUserInfo.getPassword());
    }

    @Test
    public void selectSingleTableWhereAliasName() {
        UserInfo userInfo = jdbcDao.selectFrom(UserInfo.class).as("t1")
                .where(UserInfo::getUserInfoId, 1L)
                .findOne();
        Assertions.assertEquals(1L, userInfo.getUserInfoId());
    }

    @Test
    public void selectSingleTableOrderByAliasName() {
        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class).as("t1")
                .orderBy(UserInfo::getUserInfoId, OrderBy.ASC)
                .orderBy(UserInfo::getGmtCreate, OrderBy.DESC)
                .findList();
        Assertions.assertEquals(50, list.size());
        Assertions.assertEquals(1L, list.get(0).getUserInfoId());
    }

    @Test
    public void selectWhereField() {

        jdbcDao.executeDeleteAll(UserInfo.class);
        for (int i = 60; i < 70; i++) {
            UserInfo user = new UserInfo();
            user.setUserInfoId((long) i);
            user.setLoginName("nameStrField");
            user.setPassword("123456-" + i);
            user.setUserAge(19);
            user.setGmtCreate(new Date());
            jdbcDao.executeInsert(user);
        }
        Select<UserInfo> select1 = jdbcDao.selectFrom(UserInfo.class)
                .where("loginName", "nameStrField");
        long count1 = select1.findCount();
        Assertions.assertEquals(10, count1);

        List<UserInfo> list1 = select1.findList();
        Assertions.assertEquals(10, list1.size());

        Page<UserInfo> page1 = select1.paginate(1, 5).findPage(UserInfo.class);
        Assertions.assertEquals(5, page1.getList().size());

        Select<UserInfo> select2 = jdbcDao.selectFrom(UserInfo.class)
                .where(UserInfo::getUserAge, 19)
                .where(UserInfo::getLoginName, "nameStrField");
        long count2 = select2.findCount();
        Assertions.assertEquals(10, count2);

        List<UserInfo> list2 = select2.findList();
        Assertions.assertEquals(10, list2.size());

        Page<UserInfo> page2 = select2.paginate(1, 5).findPage();
        Assertions.assertEquals(5, page2.getList().size());
    }

    @Test
    public void selectWhereOr() {

        jdbcDao.executeDeleteAll(UserInfo.class);
        for (int i = 1; i < 3; i++) {
            UserInfo user = new UserInfo();
            user.setUserInfoId((long) i);
            user.setLoginName("name-19");
            user.setPassword("123456-" + i);
            user.setUserAge(21);
            user.setGmtCreate(new Date());
            jdbcDao.executeInsert(user);
        }
        try {
            jdbcDao.selectFrom(UserInfo.class)
                    .where("loginName", "name-19")
                    .or()
                    .condition("userAge", 21)
                    .findOne(UserInfo.class);
        } catch (IncorrectResultSizeDataAccessException e) {
            Assertions.assertEquals("Incorrect result size: expected 1, actual 2", e.getMessage());
        }
    }

    @Test
    public void selectWhereAndSqlParen() {

        jdbcDao.executeDeleteAll(UserInfo.class);
        for (int i = 1; i < 3; i++) {
            UserInfo user = new UserInfo();
            user.setUserInfoId((long) i);
            user.setLoginName("whereAnd");
            user.setPassword("123456-" + i);
            user.setUserAge(21);
            user.setGmtCreate(new Date());
            jdbcDao.executeInsert(user);
        }
        UserInfo userInfo = jdbcDao.selectFrom(UserInfo.class)
                .where()
                .openParen()
                .condition(UserInfo::getLoginName, SqlOperator.EQ, "whereAnd")
                .or()
                .condition(UserInfo::getPassword, SqlOperator.EQ, "123456-5")
                .closeParen()
                .and()
                .condition(UserInfo::getUserInfoId, 2L)
                .findOne(UserInfo.class);
        Assertions.assertEquals(2L, userInfo.getUserInfoId());
    }

    @Test
    public void select3() {

        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class)
                .addColumn("userInfoId", "password")
                .addColumn(UserInfo::getLoginName)
                //add之后又移除
                .dropColumn("password")
                .where("userAge", SqlOperator.LTE, 10)
                .findList();
        Assertions.assertNotNull(list);
        Assertions.assertEquals(10, list.size());
        for (UserInfo user : list) {
            Assertions.assertNotNull(user.getUserInfoId());
            Assertions.assertNull(user.getPassword());
            Assertions.assertNotNull(user.getLoginName());
            Assertions.assertNull(user.getUserAge());
            Assertions.assertNull(user.getGmtCreate());
        }
    }

    @Test
    public void selectDropColumn() {

        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class)
                .addBeanColumns()
                .dropColumn("password")
                .where("userAge", SqlOperator.LTE, 10)
                .findList();
        Assertions.assertNotNull(list);
        Assertions.assertEquals(10, list.size());
        for (UserInfo user : list) {
            Assertions.assertNotNull(user.getUserInfoId());
            Assertions.assertNull(user.getPassword());
            Assertions.assertNotNull(user.getLoginName());
            Assertions.assertNotNull(user.getUserAge());
            Assertions.assertNotNull(user.getGmtCreate());
        }
    }

    @Test
    public void selectNotAddAllDropColumn() {

        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class)
                .dropColumn("password")
                .where("userAge", SqlOperator.LTE, 10)
                .findList();
        Assertions.assertNotNull(list);
        Assertions.assertEquals(10, list.size());
        for (UserInfo user : list) {
            Assertions.assertNotNull(user.getUserInfoId());
            Assertions.assertNull(user.getPassword());
            Assertions.assertNotNull(user.getLoginName());
            Assertions.assertNotNull(user.getUserAge());
            Assertions.assertNotNull(user.getGmtCreate());
        }
    }

    @Test
    public void select4() {

        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class)
                .addBeanColumns()
                .dropColumn("userInfoId", "password")
                .orderBy("userAge", OrderBy.ASC)
                .findList(UserInfo.class);
        Assertions.assertNotNull(list);
        int preAge = 0;
        for (UserInfo user : list) {
            Assertions.assertNull(user.getUserInfoId());
            Assertions.assertNull(user.getPassword());
            Assertions.assertNotNull(user.getLoginName());
            Assertions.assertNotNull(user.getUserAge());
            Assertions.assertNotNull(user.getGmtCreate());
            Assertions.assertTrue(user.getUserAge() > preAge);
            preAge = user.getUserAge();
        }
    }

    @Test
    public void select5() {

        Long maxId = jdbcDao.selectFrom(UserInfo.class)
                .addColumn("max(userInfoId) maxid")
                .findOneForScalar(Long.class);
        Assertions.assertEquals(50, (long) maxId);
    }

    @Test
    public void select6() {
        List<Long> ids = jdbcDao.selectFrom(UserInfo.class)
                .addColumn(UserInfo::getUserInfoId)
                .findListForScalar(Long.class);
        Assertions.assertNotNull(ids);
    }


    @Test
    public void select8() {
        Object result = jdbcDao.selectFrom(UserInfo.class)
                .where("userInfoId", 15L)
                .findOneForMap();
        Assertions.assertNotNull(result);
    }

    @Test
    public void select10() {
        List<?> result = jdbcDao.selectFrom(UserInfo.class)
                .findListForMap();
        Assertions.assertNotNull(result);
        Assertions.assertInstanceOf(Map.class, result.get(0));
    }

    @Test
    public void select11() {
        UserInfo pageable = new UserInfo();
        pageable.setPageSize(5);
        Page<?> result = jdbcDao.selectFrom(UserInfo.class)
                .paginate(pageable)
                .findPageForMap();
        Assertions.assertEquals(5, result.getList().size());
        Assertions.assertInstanceOf(Map.class, result.getList().get(0));
    }

    @Test
    public void select13() {
        List<UserInfo> users = jdbcDao.selectFrom(UserInfo.class).as("t1")
                .where("t1.userInfoId", SqlOperator.IN, new Object[]{11L, 12L, 13L})
                .and("t1.loginName", SqlOperator.IN, new Object[]{"name-11", "name-12", "name-13"})
                .and("t1.userAge", SqlOperator.IN, new Object[]{11L, 12L, 13L})
                .findList(UserInfo.class);
        Assertions.assertEquals(3, users.size());
    }

    @Test
    public void select15() {


        Select<UserInfo> select = jdbcDao.selectFrom(UserInfo.class);
        select.where("userAge", 5);

        UserInfo user = new UserInfo();
        user.setUserInfoId(10L);
        user.setLoginName("liyd");
        select.whereForBean(user);

        List<UserInfo> users = select.findList(UserInfo.class);
        Assertions.assertNotNull(users);
    }

    @Test
    public void select16() {
        Select<UserInfo> select = jdbcDao.selectFrom(UserInfo.class);
        //自动处理where情况
        select.where("userAge", 15);
        select.where("userInfoId", 10L);

        List<UserInfo> users = select.findList();
        Assertions.assertNotNull(users);
    }

    @Test
    public void select19() {
        Map<String, Object> result = jdbcDao.selectFrom(UserInfo.class)
                .orderBy("userInfoId", OrderBy.ASC)
                .findFirstForMap();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.get("USER_INFO_ID"));
    }

    @Test
    public void select20() {
        Long result = jdbcDao.selectFrom(UserInfo.class)
                .addColumn("userInfoId")
                .orderBy("userInfoId", OrderBy.ASC)
                .findFirstForScalar(Long.class);
        Assertions.assertEquals(1L, (long) result);
    }

    @Test
    public void select21() {
        Page<Long> page = jdbcDao.selectFrom(UserInfo.class)
                .addColumn("userInfoId")
                .orderBy("userInfoId", OrderBy.ASC)
                .paginate(1, 10)
                .findPageForScalar(Long.class);
        Assertions.assertEquals(10, page.getList().size());
        Assertions.assertEquals(1L, (long) page.getList().get(0));
    }

    @Test
    public void selectColumn() {
        Page<UserInfo> userInfoPage = jdbcDao.selectFrom(UserInfo.class)
                .addColumn((UserInfo::getLoginName))
                .paginate(1, 10)
                .findPage(UserInfo.class);
        Assertions.assertNotNull(userInfoPage.getList());
        Assertions.assertEquals(10, userInfoPage.getList().size());
        for (UserInfo userInfo : userInfoPage.getList()) {
            Assertions.assertNotNull(userInfo.getLoginName());
            Assertions.assertNull(userInfo.getPassword());
            Assertions.assertNull(userInfo.getUserInfoId());
            Assertions.assertNull(userInfo.getGmtCreate());
            Assertions.assertNull(userInfo.getUserAge());
        }
    }

    @Test
    public void findOneObject() {

        Object object = jdbcDao.selectFrom(UserInfo.class)
                .addColumn("loginName", "password")
                .where("userInfoId", 5L)
                .findOneForMap();

        Assertions.assertNotNull(object);
    }

    @Test
    public void selectForAnnotation() {
        Page<AnnotationUserInfo> page = jdbcDao.selectFrom(AnnotationUserInfo.class)
                .paginate(1, 10)
                .findPage(AnnotationUserInfo.class);
        Assertions.assertFalse(page.getList().isEmpty());
        Assertions.assertNotNull(page.getList().get(0).getLoginName());
    }


    @Test
    public void groupByAndOrderBy() {
        UserInfo user = new UserInfo();
        user.setLoginName("name2-19");
        user.setPassword("123456-");
        user.setUserAge(19);
        user.setGmtCreate(new Date());
        jdbcDao.executeInsert(user);

        Select<UserInfo> select = jdbcDao.selectFrom(UserInfo.class)
                .addColumn("count(*) Num")
                .groupBy(UserInfo::getUserAge)
                .orderBy("Num", OrderBy.DESC);
        Page<Map<String, Object>> page = select.paginate(1, 5).findPageForMap();

        Assertions.assertEquals(5, page.getList().size());

        List<Map<String, Object>> objects = select.findListForMap();
        Assertions.assertEquals(50, objects.size());
    }

    @Test
    public void updateSetStrField() {

        jdbcDao.update(UserInfo.class)
                .set("loginName", "newName")
                .where("userInfoId", 15L)
                .execute();

        UserInfo user1 = jdbcDao.get(UserInfo.class, 15L);
        Assertions.assertEquals("newName", user1.getLoginName());
    }

    @Test
    public void updateForEntity() {

        UserInfo user = new UserInfo();
        user.setLoginName("newName22");
        user.setPassword("abc");
        //没有设置where条件，将更新所有
        jdbcDao.update(UserInfo.class)
                .setForBean(user)
                .execute();

        UserInfo user1 = jdbcDao.get(UserInfo.class, 17L);
        Assertions.assertEquals("newName22", user1.getLoginName());
        Assertions.assertEquals("abc", user1.getPassword());
    }

    @Test
    public void updateNull() {

        UserInfo user = new UserInfo();
        user.setUserInfoId(17L);
        jdbcDao.update(UserInfo.class)
                .updateNull()
                .setForBean(user)
                .whereForBeanPrimaryKey(user)
                .execute();

        UserInfo user1 = jdbcDao.get(UserInfo.class, 17L);
        Assertions.assertNull(user1.getLoginName());
        Assertions.assertNull(user1.getPassword());
        Assertions.assertNull(user1.getUserAge());
        Assertions.assertNull(user1.getGmtCreate());
    }


    @Test
    public void updateForEntityAndWhere() {
        UserInfo user = new UserInfo();
        String newName = "newName22";
        user.setLoginName(newName);
        user.setPassword("abc");
        int count = jdbcDao.update(UserInfo.class)
                .setForBean(user)
                .where(UserInfo::getUserInfoId, 1L)
                .execute();
        Assertions.assertEquals(1, count);

        UserInfo user1 = jdbcDao.get(UserInfo.class, 1L);
        Assertions.assertEquals(newName, user1.getLoginName());
    }

    @Test
    public void updatePkNull() {

        try {
            UserInfo user = new UserInfo();
            user.setLoginName("newName22");
            user.setPassword("abc");
            jdbcDao.update(UserInfo.class)
                    .setForBean(user)
                    .whereForBeanPrimaryKey(user)
                    .execute();
        } catch (Exception e) {
            Assertions.assertEquals("主键属性值不能为空:userInfoId", e.getMessage());
        }
    }

    @Test
    public void insertAnnotation() {

        AnnotationUserInfo ku = new AnnotationUserInfo();
        ku.setLoginName("insertName");
        ku.setPassword("123456");
        ku.setUserAge(18);
        ku.setGmtCreate(new Date());
        Long id = jdbcDao.executeInsert(ku);

        AnnotationUserInfo annotationUserInfo = jdbcDao.get(AnnotationUserInfo.class, id);
        Assertions.assertEquals(ku.getLoginName(), annotationUserInfo.getLoginName());
        Assertions.assertEquals(ku.getPassword(), annotationUserInfo.getPassword());
    }

    @Test
    public void updateAnnotation() {
        jdbcDao.executeDeleteAll(AnnotationUserInfo.class);
        AnnotationUserInfo ai = new AnnotationUserInfo();
        ai.setRowId(36L);
        ai.setLoginName("666");
        ai.setPassword("6666");
        Object id = jdbcDao.executeInsert(ai);
        Assertions.assertEquals(36L, id);

        AnnotationUserInfo ku = new AnnotationUserInfo();
        ku.setRowId(36L);
        ku.setLoginName("777777");
        ku.setPassword("787878");
        ku.setGmtModify(new Date());
        int count = jdbcDao.executeUpdate(ku);
        Assertions.assertEquals(1, count);
        AnnotationUserInfo annotationUserInfo = jdbcDao.get(AnnotationUserInfo.class, 36L);
        Assertions.assertEquals(annotationUserInfo.getLoginName(), ku.getLoginName());
        Assertions.assertEquals(annotationUserInfo.getPassword(), ku.getPassword());
    }

    @Test
    public void deleteAnnotation() {

        AnnotationUserInfo ku = new AnnotationUserInfo();
        ku.setLoginName("liyd");
        Serializable id = (Serializable) jdbcDao.executeInsert(ku);

        int count = jdbcDao.executeDelete(AnnotationUserInfo.class, id);
        Assertions.assertEquals(1, count);
    }


    @Test
    public void updateNative() {

        UserInfo user = new UserInfo();
        user.setLoginName("name-native");
        user.setPassword("123456-native");
        user.setUserAge(17);
        user.setGmtCreate(new Date());

        Long id = jdbcDao.executeInsert(user);
        jdbcDao.update(UserInfo.class)
                .set("{{userAge}}", "userAge+1")
                .where("userInfoId", id)
                .execute();

        UserInfo user2 = jdbcDao.get(UserInfo.class, id);
        Assertions.assertEquals(18, (int) user2.getUserAge());
    }

    @Test
    public void nativeExecutor() {
        int count = jdbcDao.nativeExecutor()
                .command("update UserInfo set loginName = ? where userInfoId = ?")
                .parameters("newName", 39L)
                .update();
        Assertions.assertEquals(1, count);
        UserInfo user = jdbcDao.get(UserInfo.class, 39L);
        Assertions.assertEquals("newName", user.getLoginName());
    }


    @Test
    public void nativeExecutorBeanParam() {
        UserInfo user = new UserInfo();
        user.setUserInfoId(39L);
        user.setLoginName("name-bean");
        int count = jdbcDao.nativeExecutor()
                .command("update UserInfo set loginName = :loginName where userInfoId = :userInfoId")
                .namedParameter()
                .parameter(BeanParameter.of(user))
                .update();
        Assertions.assertEquals(1, count);
        UserInfo user2 = jdbcDao.get(UserInfo.class, 39L);
        Assertions.assertEquals("name-bean", user2.getLoginName());
    }

    @Test
    public void nativeExecutorForceNativeAndNamed() {
        int count = jdbcDao.nativeExecutor()
                .namedParameter()
                .forceNative()
                .command("update sd_User_Info set login_Name = :loginName where user_Info_Id = :userInfoId")
                .parameter("loginName", "newName")
                .parameter("userInfoId", 39L)
                .update();
        Assertions.assertEquals(1, count);
        UserInfo user = jdbcDao.get(UserInfo.class, 39L);
        Assertions.assertEquals("newName", user.getLoginName());
    }

    @Test
    public void nativeExecutorNamedIn() {

        List<Long> ids = new ArrayList<>();
        ids.add(39L);
        ids.add(40L);

        Long[] userInfoId2 = new Long[]{23L, 24L};

        Map<String, Object> params = new HashMap<>();
        params.put("loginName", "newName");
        params.put("userInfoId", ids);
        params.put("userInfoId2", userInfoId2);

        String sql = "update sd_User_Info set login_Name = :loginName where user_Info_Id in (:userInfoId) or user_info_id in (:userInfoId2)";

        int count = jdbcDao.nativeExecutor()
                .namedParameter()
                .forceNative()
                .command(sql)
                .parameters(params)
                .update();
        Assertions.assertEquals(4, count);
        UserInfo user = jdbcDao.get(UserInfo.class, 39L);
        Assertions.assertEquals("newName", user.getLoginName());
    }


    @Test
    public void nativeExecutor4() {
        int count = jdbcDao.nativeExecutor()
                .command("update UserInfo set loginName = ? where userInfoId = ?")
                .parameters("newName", 39L)
                .update();
        Assertions.assertEquals(1, count);
        UserInfo user = jdbcDao.get(UserInfo.class, 39L);
        Assertions.assertEquals("newName", user.getLoginName());
    }


    @Test
    public void nativeExecutor5() {

        long result = jdbcDao.nativeExecutor()
                .command("select count(*) from UserInfo")
                .findCount();
        Assertions.assertEquals(50, result);
    }

    @Test
    public void nativeExecutor7() {

        jdbcDao.nativeExecutor()
                .command("update sd_user_info set user_age = 18 where user_age < 18")
                .forceNative()
                .execute();

        long count = jdbcDao.selectFrom(UserInfo.class)
                .where("userAge", SqlOperator.LT, 18)
                .findCount();
        Assertions.assertEquals(0, count);
    }

    @Test
    public void nativeExecutor8() {

        jdbcDao.nativeExecutor()
                .command("update UserInfo set userAge = 18 where userAge < 18")
                .execute();

        long count = jdbcDao.selectFrom(UserInfo.class)
                .where("userAge", SqlOperator.LT, 18)
                .findCount();
        Assertions.assertEquals(0, count);
    }

    @Test
    public void nativeOneColResult() {
        Integer integer = jdbcDao.nativeExecutor()
                .command("select sum(userAge) from UserInfo")
                .findOneForScalar(Integer.class);
        Assertions.assertTrue(integer > 0);
    }

    @Test
    public void nativeFindFirst() {
        UserInfo userInfo = jdbcDao.nativeExecutor()
                .command("select * from UserInfo order by userInfoId asc")
                .findFirst(UserInfo.class);

        Assertions.assertEquals(1L, (long) userInfo.getUserInfoId());
    }

    @Test
    public void nativeOneColFindFirst() {
        Long id = jdbcDao.nativeExecutor()
                .command("select userInfoId from UserInfo order by userInfoId asc")
                .findFirstForScalar(Long.class);

        Assertions.assertEquals(1L, (long) id);
    }

    @Test
    public void nativeInsert() {
        jdbcDao.nativeExecutor()
                .command("insert into UserInfo(userInfoId,loginName,password,userAge) values(?,?,?,?)")
                .parameters(100L, "100user", "123321", 18)
                .insert();

        UserInfo userInfo = jdbcDao.get(UserInfo.class, 100L);
        Assertions.assertEquals(100L, (long) userInfo.getUserInfoId());
        Assertions.assertEquals("100user", userInfo.getLoginName());
        Assertions.assertEquals("123321", userInfo.getPassword());
        Assertions.assertEquals(18, (int) userInfo.getUserAge());
    }

    @Test
    public void nativeInsert2() {
        Serializable id = jdbcDao.nativeExecutor()
                .command("insert into UserInfo(loginName,password,userAge) values(?,?,?)")
                .parameters("100user", "123321", 18)
                .insert(UserInfo.class);

        UserInfo userInfo = jdbcDao.get(UserInfo.class, id);
        Assertions.assertEquals("100user", userInfo.getLoginName());
        Assertions.assertEquals("123321", userInfo.getPassword());
        Assertions.assertEquals(18, (int) userInfo.getUserAge());
    }

    @Test
    public void nativeInsertNativeCommandNotPkColumn() {
        Serializable id =
                jdbcDao.nativeExecutor()
                        .command("insert into sd_user_info (login_name,password,user_age) values(?,?,?)")
                        .parameters("100user", "123321", 18)
                        .forceNative()
                        .insert();
        UserInfo userInfo = jdbcDao.get(UserInfo.class, id);
        Assertions.assertEquals("100user", userInfo.getLoginName());
        Assertions.assertEquals("123321", userInfo.getPassword());
        Assertions.assertEquals(18, (int) userInfo.getUserAge());
    }

    @Test
    public void nativeInsertNativeCommandPkValue() {
        Serializable id =
                jdbcDao.nativeExecutor()
                        .command("insert into sd_user_info (user_info_id,login_name,password,user_age) values(?,?,?,?)")
                        .parameters(-1, "100user", "123321", 18)
                        .forceNative()
                        .insert();
        UserInfo userInfo = jdbcDao.get(UserInfo.class, id);
        Assertions.assertEquals("100user", userInfo.getLoginName());
        Assertions.assertEquals("123321", userInfo.getPassword());
        Assertions.assertEquals(18, (int) userInfo.getUserAge());
    }

    @Test
    public void nativeList() {
        List<UserInfo> list = jdbcDao.nativeExecutor()
                .command("select * from UserInfo")
                .findList(UserInfo.class);

        Assertions.assertTrue(list != null && !list.isEmpty());
    }

    @Test
    public void nativeOneColList() {
        List<Long> list = jdbcDao.nativeExecutor()
                .command("select userInfoId from UserInfo")
                .findListForScalar(Long.class);

        Assertions.assertTrue(list != null && !list.isEmpty());
    }

    @SuppressWarnings("CastCanBeRemovedNarrowingVariableType")
    @Test
    public void nativeFirstList() {
        Object result = jdbcDao.nativeExecutor()
                .command("select userInfoId from UserInfo")
                .findFirstForMap();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, ((Map<?, ?>) result).size());
    }

    @Test
    public void nativePage() {
        Pageable pageable = new UserInfo();
        pageable.setPageNum(2);
        pageable.setPageSize(10);
        Page<Map<String, Object>> page = jdbcDao.nativeExecutor()
                .command("select userInfoId from UserInfo order by userInfoId asc")
                .paginate(pageable)
                .findPageForMap();
        Assertions.assertEquals(10, page.getList().size());
        Assertions.assertNotNull(page.getList().get(0));
        Map<String, Object> map = page.getList().get(0);
        Assertions.assertEquals(11L, map.get("USER_INFO_ID"));
        Map<String, Object> map2 = page.getList().get(9);
        Assertions.assertEquals(20L, map2.get("USER_INFO_ID"));
    }

    @Test
    public void nativeResultHandler() {
        //这里只演示把结果转为UidUser类
        Page<Account> page = jdbcDao.nativeExecutor()
                .command("select * from UserInfo order by userInfoId asc")
                .paginate(2, 5)
                .resultHandler(new CustomResultHandler())
                .findPage(Account.class);
        Assertions.assertEquals(5, page.getList().size());
        Assertions.assertEquals("name-6", page.getList().get(0).getLoginName());
        Assertions.assertEquals("name-10", page.getList().get(4).getLoginName());
    }

    @Test
    public void nativeLimitPage() {
        Page<Map<String, Object>> page = jdbcDao.nativeExecutor()
                .command("select userInfoId from UserInfo order by userInfoId asc")
                .limit(15, 10)
                .findPageForMap();
        Assertions.assertEquals(10, page.getList().size());
        Assertions.assertNotNull(page.getList());
        Assertions.assertNotNull(page.getList().get(0));
        Map<String, Object> map = page.getList().get(0);
        Assertions.assertEquals(11L, map.get("USER_INFO_ID"));
        Map<String, Object> map2 = page.getList().get(9);
        Assertions.assertEquals(20L, map2.get("USER_INFO_ID"));
    }

    @Test
    public void mybatisExecutor1() {
        UserInfo user = jdbcDao.myBatisExecutor()
                .command("getUser")
                .parameter("id", 9L)
                .parameter("loginName", "name-9")
                .findOne(UserInfo.class);

        Assertions.assertNotNull(user);
    }

    @Test
    public void mybatisExecutor2() {
        Map<String, Object> params = new HashMap<>();
        params.put("id", 9L);
        params.put("loginName", "name-9");

        UserInfo user = jdbcDao.myBatisExecutor()
                .command("getUserSql")
                .parameters(params)
                .forceNative()
                .findOne(UserInfo.class);

        Assertions.assertNotNull(user);
    }

    @Test
    public void mybatisExecutor3() {

        UserInfo userInfo = new UserInfo();
        userInfo.setUserInfoId(10L);
        userInfo.setLoginName("name-10");

        Object user = jdbcDao.myBatisExecutor()
                .command("getUser2")
                .parameter("user", userInfo)
                .forceNative()
                .findOneForMap();

        Assertions.assertNotNull(user);
    }

    @Test
    public void mybatisExecutor4() {

        List<String> names = new ArrayList<>();
        names.add("name-8");
        names.add("name-9");
        names.add("name-10");

        List<?> list = jdbcDao.myBatisExecutor()
                .command("queryUserList")
                .parameter("names", names)
                .forceNative()
                .findListForMap();

        Assertions.assertEquals(3, list.size());
    }

    @Test
    public void mybatisExecutor5() {

        int update = jdbcDao.myBatisExecutor()
                .command("updateUser")
                .parameter("loginName", "newName")
                .parameter("userInfoId", 9L)
                .forceNative()
                .update();

        Assertions.assertEquals(1, update);
        UserInfo userInfo = jdbcDao.get(UserInfo.class, 9L);
        Assertions.assertEquals("newName", userInfo.getLoginName());
    }


    @Test
    public void mybatisExecutor6() {
        Object user = jdbcDao.myBatisExecutor()
                .command("getUser3")
                .parameter("userInfoId", 9L)
                .parameter("loginName", "name-9")
                .findOneForMap();

        Assertions.assertNotNull(user);
    }


    @Test
    public void mybatisExecutor7() {
        UserInfo userInfo = jdbcDao.myBatisExecutor()
                .command("getUser3")
                .parameter("userInfoId", 9L)
                .parameter("loginName", "name-9")
                .findOne(UserInfo.class);

        Assertions.assertEquals("name-9", userInfo.getLoginName());
    }

    @Test
    public void mybatisExecutor8() {
        Page<UserInfo> page = jdbcDao.myBatisExecutor()
                .command("queryUserList2")
                .paginate(1, 10)
                .findPage(UserInfo.class);

        Assertions.assertEquals(10, page.getList().size());
    }

    @Test
    public void lambdaSelect() {
        UserInfo userInfo = new UserInfo();
        userInfo.setLoginName("name-6");
        userInfo.setPassword("123456-6");
        GetterFunction<UserInfo> getLoginName = UserInfo::getLoginName;
        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class)
                .where(getLoginName, userInfo.getLoginName())
                .where(UserInfo::getPassword, userInfo.getPassword())
                .findList(UserInfo.class);

        Assertions.assertFalse(list.isEmpty());

    }

    @Test
    public void batchUpdate() {

        jdbcDao.executeDeleteAll(UserInfo.class);
        List<UserInfo> userInfoList = new ArrayList<>();
        for (int i = 1; i < 10000; i++) {
            UserInfo user = new UserInfo();
            user.setUserInfoId((long) i);
            user.setLoginName("name-" + i);
            user.setPassword("123456-" + i);
            user.setUserAge(i);
            user.setGmtCreate(new Date());
            userInfoList.add(user);
        }

        String sql = "INSERT INTO sd_USER_INFO (PASSWORD, LOGIN_NAME, GMT_CREATE, USER_AGE, USER_INFO_ID) VALUES (?, ?, ?, ?, ?)";
        final long begin = System.currentTimeMillis();
        jdbcDao.batchUpdate()
                .forceNative()
                .execute(sql, userInfoList, 1000, (ps, names, userInfo) -> {
                    ps.setString(1, userInfo.getPassword());
                    ps.setString(2, userInfo.getLoginName());
                    ps.setDate(3, new java.sql.Date(userInfo.getGmtCreate().getTime()));
                    ps.setInt(4, userInfo.getUserAge());
                    ps.setLong(5, userInfo.getUserInfoId());
                });


        final long end = System.currentTimeMillis();
        System.out.println("jdbcDao插入耗时:" + (end - begin));
        Assertions.assertEquals(userInfoList.size(), jdbcDao.findAllCount(UserInfo.class));

    }

    @Test
    public void batchUpdate1() {

        jdbcDao.executeDeleteAll(UserInfo.class);
        List<UserInfo> userInfoList = new ArrayList<>();
        for (int i = 1; i < 10000; i++) {
            UserInfo user = new UserInfo();
            user.setUserInfoId((long) i);
            user.setLoginName("name-" + i);
            user.setPassword("123456-" + i);
            user.setUserAge(i);
            user.setGmtCreate(new Date());
            userInfoList.add(user);
        }

        String sql1 = "INSERT INTO UserInfo (PASSWORD, loginName, gmtCreate, userAge, userInfoId) VALUES (?, ?, ?, ?, ?)";
        final long begin1 = System.currentTimeMillis();

        jdbcDao.executeBatchUpdate(sql1, userInfoList, userInfoList.size(), (ps, names, userInfo) -> {
            ps.setString(1, userInfo.getPassword());
            ps.setString(2, userInfo.getLoginName());
            ps.setDate(3, new java.sql.Date(userInfo.getGmtCreate().getTime()));
            ps.setInt(4, userInfo.getUserAge());
            ps.setLong(5, userInfo.getUserInfoId());
        });

        final long end1 = System.currentTimeMillis();
        System.out.println("jdbcDao插入耗时:" + (end1 - begin1));
        Assertions.assertEquals(userInfoList.size(), jdbcDao.findAllCount(UserInfo.class));

    }

    @Test
    public void deleteWhereAppendParam() {
        int count = jdbcDao.deleteFrom(UserInfo.class)
                .appendSegment("userInfoId = ?", 4L)
                .execute();

        Assertions.assertEquals(1, count);
        UserInfo user = jdbcDao.get(UserInfo.class, 4L);
        Assertions.assertNull(user);
    }

    @Test
    public void deleteWhereAppendNamed() {
        Map<String, Object> params = new HashMap<>();
        params.put("userInfoId", 4L);
        int count = jdbcDao.deleteFrom(UserInfo.class)
                .namedParameter()
                .appendSegment("userInfoId = :userInfoId", params)
                .execute();

        Assertions.assertEquals(1, count);
        UserInfo user = jdbcDao.get(UserInfo.class, 4L);
        Assertions.assertNull(user);
    }


    @Test
    public void whereAppendSubSqlParam() {
        UserInfo userInfo = jdbcDao.selectFrom(UserInfo.class)
                .where("userAge", SqlOperator.GT, 5)
                .and()
                .appendSegment("userInfoId = (select max(t2.userInfoId) from UserInfo t2 where t2.userInfoId < ?)", 40)
                .findOne(UserInfo.class);
        Assertions.assertNotNull(userInfo);
    }

    @Test
    public void whereAppendSubSqlNamed() {
        Map<String, Object> params = new HashMap<>();
        params.put("userInfoId", 40L);
        UserInfo userInfo = jdbcDao.selectFrom(UserInfo.class)
                .namedParameter()
                .where("userAge", SqlOperator.GT, 5)
                .and()
                .appendSegment("userInfoId = (select max(t2.userInfoId) from UserInfo t2 where t2.userInfoId < :userInfoId)", params)
                .findOne(UserInfo.class);
        Assertions.assertNotNull(userInfo);
    }

    @Test
    public void namedParamHandler() {
        String sql = "insert into UserInfo(userInfoId,loginName,password,userAge) values(:userInfoId,:loginName,:password,:random_z10_z30)";
        Set<Integer> ages = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            jdbcDao.nativeExecutor()
                    .namedParameter()
                    .command(sql)
                    .parameter("userInfoId", 99L + i)
                    .parameter("loginName", "newName")
                    .parameter("password", "123456")
                    .parameter("random_z10_z30", new JdbcRandomNamedParamHandler())
                    .insert();

            UserInfo user = jdbcDao.get(UserInfo.class, 99L + i);
            ages.add(user.getUserAge());
            Assertions.assertEquals("newName", user.getLoginName());
            Assertions.assertTrue(user.getUserAge() >= 10 && user.getUserAge() <= 30);
        }
        Assertions.assertTrue(ages.size() > 1);
    }

    @Test
    public void batchUpdate2() {

        jdbcDao.executeDeleteAll(UserInfo.class);
        String sql = "INSERT INTO sd_USER_INFO (PASSWORD, LOGIN_NAME, GMT_CREATE, USER_AGE, USER_INFO_ID) VALUES (:PASSWORD, :LOGIN_NAME, :GMT_CREATE, :USER_AGE, :USER_INFO_ID)";
        List<Map<String, Object>> userInfoList = new ArrayList<>();
        for (int i = 1; i <= 10000; i++) {
            Map<String, Object> map = new LinkedCaseInsensitiveMap<>();
            map.put("password", "123456-" + i);
            map.put("login_name", "name-" + i);
            map.put("gmt_create", new Date());
            map.put("user_age", i);
            map.put("USER_INFO_ID", (long) i);
            userInfoList.add(map);
        }
        jdbcDao.batchUpdate()
                .forceNative()
                .namedParameter()
                .execute(sql, userInfoList, userInfoList.size(), (ps, names, map) -> {
                    for (int j = 0; j < names.size(); j++) {
                        Object val = map.get(names.get(j));
                        if (val == null) {
                            throw new RuntimeException("参数不存在");
                        }
                        ps.setObject(j + 1, val);
                    }
                });

        long count = jdbcDao.findAllCount(UserInfo.class);
        Assertions.assertTrue(count >= 10000);

        long count1 = jdbcDao.selectFrom(UserInfo.class)
                .where(UserInfo::getLoginName, "name-9999")
                .findCount();
        Assertions.assertTrue(count1 > 0);

    }

    @Test
    public void innerJoinClassFieldStr() {
        jdbcDao.executeDeleteAll(Account.class);
        for (int i = 1; i < 11; i++) {
            Account account = new Account();
            account.setAccountId((long) i);
            account.setLoginName("account" + i);
            account.setAccountName("accountName" + i);
            account.setPassword("password" + i);
            account.setUserAge(i);
            jdbcDao.executeInsert(account);
        }
        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class).as("t1").addBeanColumns()
                .innerJoin(Account.class).as("t2")
                .on("t1.userInfoId = t2.accountId")
                .findList();
        Assertions.assertEquals(10, list.size());
    }

    @Test
    public void innerJoinClassFieldLambda() {
        jdbcDao.executeDeleteAll(Account.class);
        for (int i = 1; i < 11; i++) {
            Account account = new Account();
            account.setAccountId((long) i);
            account.setLoginName("account" + i);
            account.setAccountName("accountName" + i);
            account.setPassword("password" + i);
            account.setUserAge(i);
            jdbcDao.executeInsert(account);
        }
        List<Map<String, Object>> list = jdbcDao.selectFrom(UserInfo.class).as("t1")
                .innerJoin(Account.class).as("t2")
                .on(UserInfo::getUserInfoId, Account::getAccountId)
                //lambda自动识别属性属于哪个表
                .addColumn(UserInfo::getLoginName)
                //明确指定哪个表的属性
                .addAliasColumn("t2", "accountName")
                .findListForMap();
        Assertions.assertEquals(10, list.size());
        for (Map<String, Object> map : list) {
            Assertions.assertEquals(2, map.size());
            Assertions.assertNotNull(map.get("login_name"));
            Assertions.assertNotNull(map.get("account_name"));
        }
    }

    @Test
    public void innerJoinClassAddColumn() {
        jdbcDao.executeDeleteAll(Account.class);
        for (int i = 1; i < 11; i++) {
            Account account = new Account();
            account.setAccountId((long) i);
            account.setLoginName("account" + i);
            account.setAccountName("accountName" + i);
            account.setPassword("password" + i);
            account.setUserAge(i);
            jdbcDao.executeInsert(account);
        }

        List<UserInfo> users = jdbcDao.selectFrom(UserInfo.class).as("t1")
                //lambda自动识别属于哪个表
                .addColumn(UserInfo::getUserInfoId)
                .innerJoin(Account.class).as("t2")
                .on(UserInfo::getUserInfoId, Account::getAccountId)
                .findList();
        Assertions.assertEquals(10, users.size());
        for (UserInfo user : users) {
            Assertions.assertNotNull(user.getUserInfoId());
            Assertions.assertNull(user.getLoginName());
            Assertions.assertNull(user.getPassword());
        }

        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class).as("t1").addAliasColumn("t1", "loginName")
                .innerJoin(Account.class).as("t2")
                .on(UserInfo::getUserInfoId, Account::getAccountId)
                .findList();
        Assertions.assertEquals(10, list.size());
    }


    @Test
    public void testTableAliasInWhereCondition() {
        // 测试使用表别名的where条件
        UserInfo userInfo = jdbcDao.selectFrom(UserInfo.class).as("t1")
                .where("t1.userInfoId", 1L)
                .findOne();

        Assertions.assertNotNull(userInfo);
        Assertions.assertEquals(1L, userInfo.getUserInfoId());
        Assertions.assertEquals("name-1", userInfo.getLoginName());
    }

    @Test
    public void testTableAliasWithLambdaInWhereCondition() {
        // 测试使用Lambda表达式的where条件
        UserInfo userInfo = jdbcDao.selectFrom(UserInfo.class).as("t1")
                .where(UserInfo::getUserInfoId, 1L)
                .findOne();

        Assertions.assertNotNull(userInfo);
        Assertions.assertEquals(1L, userInfo.getUserInfoId());
        Assertions.assertEquals("name-1", userInfo.getLoginName());
    }

    @Test
    public void testTableAliasWithMultipleConditions() {
        // 测试多个where条件
        List<UserInfo> users = jdbcDao.selectFrom(UserInfo.class).as("t1")
                .where("t1.userInfoId", 1L)
                .where("t1.loginName", "name-1")
                .findList();

        Assertions.assertEquals(1, users.size());
        Assertions.assertEquals(1L, users.get(0).getUserInfoId());
    }

    @Test
    public void testTableAliasWithOperator() {
        // 测试使用操作符的where条件
        List<UserInfo> users = jdbcDao.selectFrom(UserInfo.class).as("t1")
                .where("t1.userAge", SqlOperator.GTE, 40)
                .findList();

        Assertions.assertEquals(11, users.size());
        Assertions.assertEquals(40, users.get(0).getUserAge());
    }

    @Test
    public void testHavingStr() {
        String userType = "3";
        List<Map<String, Object>> list = jdbcDao.selectFrom(UserInfo.class)
                .addColumn(UserInfo::getUserType)
                .addColumn("count(*) as num")
                .groupBy(UserInfo::getUserType)
                .having("num", SqlOperator.GTE, 5)
                .and(UserInfo::getUserType, SqlOperator.EQ, userType)
                .findListForMap();
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals(10L, (Long) list.get(0).get("num"));
        Assertions.assertEquals(userType, list.get(0).get("user_type"));
    }

    @Test
    public void testHavingLambda() {
        String userType = "3";
        List<Map<String, Object>> list = jdbcDao.selectFrom(UserInfo.class)
                .addColumn(UserInfo::getUserType)
                .addColumn("count(*) as num")
                .groupBy(UserInfo::getUserType)
                .having(UserInfo::getUserType, SqlOperator.EQ, userType)
                .and("num", SqlOperator.GTE, 5)
                .findListForMap();
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals(10L, (Long) list.get(0).get("num"));
        Assertions.assertEquals(userType, list.get(0).get("user_type"));
    }

    @Test
    public void testInterceptor() {
        //sql不执行通过拦截器返回数据
        List<UserInfo> list = jdbcDao.nativeExecutor()
                .command(DustmanTestConfig.TestBeforeInterceptor.SQL)
                .findList(UserInfo.class);

        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals("interceptorUserAfter", list.get(0).getLoginName());
    }

    // ============ 补充测试用例以提高覆盖率 ============

    @Test
    public void testLeftJoin() {
        jdbcDao.executeDeleteAll(Account.class);
        for (int i = 1; i < 6; i++) {
            Account account = new Account();
            account.setAccountId((long) i);
            account.setLoginName("account" + i);
            account.setAccountName("accountName" + i);
            account.setPassword("password" + i);
            account.setUserAge(i);
            jdbcDao.executeInsert(account);
        }
        // left join测试
        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class).as("t1").addBeanColumns()
                .leftJoin(Account.class).as("t2")
                .on("t1.userInfoId = t2.accountId")
                .where("t2.accountId", SqlOperator.IS, null)
                .orderBy("t1.userInfoId", OrderBy.ASC)
                .findList();
        Assertions.assertTrue(list.size() >= 45); // 应该有很多没有关联的UserInfo
    }

    @Test
    public void testRightJoin() {
        jdbcDao.executeDeleteAll(Account.class);
        for (int i = 1; i < 11; i++) {
            Account account = new Account();
            account.setAccountId((long) (i + 50)); // 使用不存在的ID，测试右连接
            account.setLoginName("account" + i);
            account.setAccountName("accountName" + i);
            account.setPassword("password" + i);
            account.setUserAge(i);
            jdbcDao.executeInsert(account);
        }
        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class).as("t1").addBeanColumns()
                .rightJoin(Account.class).as("t2")
                .on("t1.userInfoId = t2.accountId")
                .orderBy("t2.accountId", OrderBy.ASC)
                .findList();
        Assertions.assertEquals(10, list.size());
    }

    @Test
    public void testJoinWithStringTableName() {
        jdbcDao.executeDeleteAll(Account.class);
        Account account = new Account();
        account.setAccountId(1L);
        account.setLoginName("account1");
        account.setAccountName("accountName1");
        account.setPassword("password1");
        account.setUserAge(1);
        jdbcDao.executeInsert(account);

        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class).as("t1").addBeanColumns()
                .innerJoin("Account").as("t2")
                .on("t1.userInfoId = t2.accountId")
                .findList();
        Assertions.assertEquals(1, list.size());
    }

    @Test
    public void testLikeOperator() {
        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class)
                .where("loginName", SqlOperator.LIKE, "name-1%")
                .findList();
        Assertions.assertTrue(list.size() >= 1); // name-1, name-10, name-11等
        Assertions.assertTrue(list.stream().anyMatch(u -> u.getLoginName().startsWith("name-1")));
    }

    @Test
    public void testNotInOperator() {
        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class)
                .where("userInfoId", SqlOperator.NOT_IN, new Object[]{1L, 2L, 3L, 4L, 5L})
                .findList();
        Assertions.assertEquals(45, list.size());
        Assertions.assertTrue(list.stream().noneMatch(u -> u.getUserInfoId() <= 5));
    }

    @Test
    public void testNotEqualOperator() {
        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class)
                .where("userInfoId", SqlOperator.NEQ, 1L)
                .findList();
        Assertions.assertEquals(49, list.size());
        Assertions.assertTrue(list.stream().noneMatch(u -> u.getUserInfoId() == 1));
    }

    @Test
    public void testIsNullOperator() {
        // 先创建一个gmtModify为null的记录
        UserInfo user = new UserInfo();
        user.setUserInfoId(100L);
        user.setLoginName("nullTest");
        user.setPassword("123456");
        user.setUserAge(20);
        user.setGmtCreate(new Date());
        // gmtModify不设置，应该为null
        jdbcDao.executeInsert(user);

        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class)
                .where("gmtModify", SqlOperator.IS, null)
                .where("userInfoId", 100L)
                .findList();
        Assertions.assertEquals(1, list.size());
        Assertions.assertNull(list.get(0).getGmtModify());
    }

    @Test
    public void testIsNotNullOperator() {
        // 更新一个记录的gmtModify
        UserInfo user = new UserInfo();
        user.setUserInfoId(1L);
        user.setGmtModify(new Date());
        jdbcDao.executeUpdate(user);

        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class)
                .where("gmtModify", SqlOperator.IS_NOT, null)
                .where("userInfoId", 1L)
                .findList();
        Assertions.assertEquals(1, list.size());
        Assertions.assertNotNull(list.get(0).getGmtModify());
    }

    @Test
    public void testInsertWithoutInto() {
        Long id = (Long) jdbcDao.insert()
                .into(UserInfo.class)
                .intoField("loginName", "insertTest")
                .intoField("password", "insertPassword")
                .intoField("userAge", 25)
                .execute();
        Assertions.assertNotNull(id);
        UserInfo user = jdbcDao.get(UserInfo.class, id);
        Assertions.assertEquals("insertTest", user.getLoginName());
        Assertions.assertEquals("insertPassword", user.getPassword());
    }

    @Test
    public void testInsertWithLambda() {
        Long id = (Long) jdbcDao.insert()
                .into(UserInfo.class)
                .intoField(UserInfo::getLoginName, "lambdaInsert")
                .intoField(UserInfo::getPassword, "lambdaPassword")
                .intoField(UserInfo::getUserAge, 26)
                .execute();
        Assertions.assertNotNull(id);
        UserInfo user = jdbcDao.get(UserInfo.class, id);
        Assertions.assertEquals("lambdaInsert", user.getLoginName());
        Assertions.assertEquals("lambdaPassword", user.getPassword());
    }

    @Test
    public void testDeleteWithoutFrom() {
        // 先创建一个测试记录
        UserInfo user = new UserInfo();
        user.setUserInfoId(200L);
        user.setLoginName("deleteTest");
        user.setPassword("123456");
        user.setUserAge(30);
        user.setGmtCreate(new Date());
        jdbcDao.executeInsert(user);

        int count = jdbcDao.delete()
                .from(UserInfo.class)
                .where("userInfoId", 200L)
                .execute();
        Assertions.assertEquals(1, count);
        UserInfo deleted = jdbcDao.get(UserInfo.class, 200L);
        Assertions.assertNull(deleted);
    }

    @Test
    public void testUpdateWithoutTable() {
        int count = jdbcDao.update()
                .table(UserInfo.class)
                .set("loginName", "updateTest")
                .where("userInfoId", 1L)
                .execute();
        Assertions.assertEquals(1, count);
        UserInfo user = jdbcDao.get(UserInfo.class, 1L);
        Assertions.assertEquals("updateTest", user.getLoginName());
    }

    @Test
    public void testUpdateWithLambda() {
        int count = jdbcDao.update(UserInfo.class)
                .set(UserInfo::getLoginName, "lambdaUpdate")
                .set(UserInfo::getPassword, "lambdaPassword")
                .where(UserInfo::getUserInfoId, 2L)
                .execute();
        Assertions.assertEquals(1, count);
        UserInfo user = jdbcDao.get(UserInfo.class, 2L);
        Assertions.assertEquals("lambdaUpdate", user.getLoginName());
        Assertions.assertEquals("lambdaPassword", user.getPassword());
    }

    @Test
    public void testExecuteScript() {
        // 测试执行SQL脚本
        String script = "update sd_user_info set login_name = 'scriptTest' where user_info_id = 5";
        jdbcDao.executeScript(script);
        UserInfo user = jdbcDao.get(UserInfo.class, 5L);
        Assertions.assertEquals("scriptTest", user.getLoginName());
    }

    @Test
    public void testExecuteInConnection() {
        // 测试在连接中执行函数
        String dbProduct = jdbcDao.executeInConnection(connection -> {
            try {
                return connection.getMetaData().getDatabaseProductName();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        Assertions.assertNotNull(dbProduct);
    }

    @Test
    public void testExecuteInRaw() {
        // 测试在原生执行器中执行函数
        String result = (String) jdbcDao.executeInRaw(rawExecutor -> {
            JdbcOperations jdbcOperations = (JdbcOperations) rawExecutor;
            return jdbcOperations.execute(new ConnectionCallback<String>() {
                @Override
                public String doInConnection(@NonNull Connection con) throws SQLException, DataAccessException {
                    return con.getMetaData().getDatabaseProductName();
                }
            });
        });
        Assertions.assertNotNull(result);
    }

    @Test
    public void testGetJdbcContext() {
        // 测试获取JdbcContext
        JdbcContext context = jdbcDao.getJdbcContext();
        Assertions.assertNotNull(context);
        Assertions.assertNotNull(context.getPersistExecutor());
        Assertions.assertNotNull(context.getMappingHandler());
    }

    @Test
    public void testSelectAddAliasColumn() {
        jdbcDao.executeDeleteAll(Account.class);
        Account account = new Account();
        account.setAccountId(1L);
        account.setLoginName("aliasTest");
        account.setAccountName("aliasName");
        account.setPassword("password");
        account.setUserAge(1);
        jdbcDao.executeInsert(account);

        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class).as("t1")
                .addAliasColumn("t1", "loginName", "userInfoId")
                .innerJoin(Account.class).as("t2")
                .on(UserInfo::getUserInfoId, Account::getAccountId)
                .addAliasColumn("t2", "accountName")
                .findList();
        Assertions.assertEquals(1, list.size());
        Assertions.assertNotNull(list.get(0).getLoginName());
        Assertions.assertNotNull(list.get(0).getUserInfoId());
    }

    @Test
    public void testSelectWithMultipleGroupBy() {
        List<Map<String, Object>> list = jdbcDao.selectFrom(UserInfo.class)
                .addColumn(UserInfo::getUserType)
                .addColumn(UserInfo::getUserAge)
                .addColumn("count(*) as num")
                .groupBy(UserInfo::getUserType)
                .groupBy(UserInfo::getUserAge)
                .orderBy("num", OrderBy.DESC)
                .findListForMap();
        Assertions.assertTrue(list.size() > 0);
    }

    @Test
    public void testSelectWithMultipleHaving() {
        List<Map<String, Object>> list = jdbcDao.selectFrom(UserInfo.class)
                .addColumn(UserInfo::getUserType)
                .addColumn("count(*) as num")
                .groupBy(UserInfo::getUserType)
                .having("num", SqlOperator.GTE, 5)
                .having(UserInfo::getUserType, SqlOperator.NEQ, "0")
                .findListForMap();
        Assertions.assertTrue(list.size() > 0);
    }

    @Test
    public void testSelectWhereWithLambda() {
        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class)
                .where(UserInfo::getUserAge, SqlOperator.GT, 40)
                .where(UserInfo::getLoginName, SqlOperator.LIKE, "name-%")
                .orderBy(UserInfo::getUserInfoId, OrderBy.ASC)
                .findList();
        Assertions.assertEquals(10, list.size());
        Assertions.assertTrue(list.stream().allMatch(u -> u.getUserAge() > 40));
    }

    @Test
    public void testSelectCondition() {
        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class)
                .where()
                .condition("userAge", 10)
                .and()
                .condition("userInfoId", SqlOperator.GT, 5)
                .findList();
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals(10, (int) list.get(0).getUserAge());
    }

    @Test
    public void testSelectConditionWithLambda() {
        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class)
                .where()
                .condition(UserInfo::getUserAge, 15)
                .and()
                .condition(UserInfo::getUserInfoId, SqlOperator.LT, 20)
                .findList();
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals(15, (int) list.get(0).getUserAge());
    }

    @Test
    public void testSelectComplexWhereConditions() {
        // 测试复杂的where条件组合
        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class)
                .where()
                .openParen()
                .where(UserInfo::getUserAge, SqlOperator.GTE, 20)
                .and()
                .where(UserInfo::getUserAge, SqlOperator.LTE, 30)
                .closeParen()
                .or()
                .openParen()
                .where(UserInfo::getUserInfoId, SqlOperator.IN, new Object[]{1L, 2L, 3L})
                .closeParen()
                .findList();
        Assertions.assertTrue(list.size() > 0);
    }

    @Test
    public void testSelectWithJoinAndComplexConditions() {
        jdbcDao.executeDeleteAll(Account.class);
        for (int i = 1; i < 11; i++) {
            Account account = new Account();
            account.setAccountId((long) i);
            account.setLoginName("account" + i);
            account.setAccountName("accountName" + i);
            account.setPassword("password" + i);
            account.setUserAge(i);
            jdbcDao.executeInsert(account);
        }

        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class).as("t1").addBeanColumns()
                .innerJoin(Account.class).as("t2")
                .on(UserInfo::getUserInfoId, Account::getAccountId)
                .where("t1.userAge", SqlOperator.GTE, 5)
                .where("t2.userAge", SqlOperator.LTE, 8)
                .orderBy("t1.userInfoId", OrderBy.ASC)
                .findList();
        Assertions.assertEquals(4, list.size()); // userInfoId 5, 6, 7, 8
    }

    @Test
    public void testNativeExecutorFindOneForScalarWithNull() {
        // 测试查找不存在的记录
        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
            Long result = jdbcDao.nativeExecutor()
                    .command("select userInfoId from UserInfo where userInfoId = ?")
                    .parameters(90000L)
                    .findOneForScalar(Long.class);
            Assertions.assertNull(result);
        });
    }

    @Test
    public void testNativeExecutorFindListForScalarEmpty() {
        // 测试查找不存在的记录列表
        List<Long> list = jdbcDao.nativeExecutor()
                .command("select userInfoId from UserInfo where userInfoId > ?")
                .parameters(99999L)
                .findListForScalar(Long.class);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(0, list.size());
    }

    @Test
    public void testNativeExecutorFindOneForMapEmpty() {
        // 测试查找不存在的记录
        Map<String, Object> result = jdbcDao.nativeExecutor()
                .command("select * from UserInfo where userInfoId = ?")
                .parameters(99999L)
                .findOneForMap();
        Assertions.assertNull(result);
    }

    @Test
    public void testSelectFindOneReturnsNull() {
        // 测试查找不存在的记录
        UserInfo user = jdbcDao.selectFrom(UserInfo.class)
                .where("userInfoId", 99999L)
                .findOne();
        Assertions.assertNull(user);
    }

    @Test
    public void testSelectFindFirstReturnsNull() {
        // 测试查找不存在的记录
        UserInfo user = jdbcDao.selectFrom(UserInfo.class)
                .where("userInfoId", 99999L)
                .findFirst();
        Assertions.assertNull(user);
    }

    @Test
    public void testSelectFindListForMapWithEmptyResult() {
        // 测试查找不存在的记录
        List<?> result = jdbcDao.selectFrom(UserInfo.class)
                .where("userInfoId", 99999L)
                .findListForMap();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void testSelectFindPageForMapWithEmptyResult() {
        // 测试查找不存在的记录
        Page<Map<String, Object>> page = jdbcDao.selectFrom(UserInfo.class)
                .where("userInfoId", 99999L)
                .paginate(1, 10)
                .findPageForMap();
        Assertions.assertNotNull(page);
        Assertions.assertEquals(0, page.getList().size());
        Assertions.assertEquals(0, page.getPagination().getTotalItems());
    }

    @Test
    public void testSelectFindPageForScalarWithEmptyResult() {
        // 测试查找不存在的记录
        Page<Long> page = jdbcDao.selectFrom(UserInfo.class)
                .addColumn("userInfoId")
                .where("userInfoId", 99999L)
                .paginate(1, 10)
                .findPageForScalar(Long.class);
        Assertions.assertNotNull(page);
        Assertions.assertEquals(0, page.getList().size());
        Assertions.assertEquals(0, page.getPagination().getTotalItems());
    }

    @Test
    public void testUpdateWithNativeExpression() {
        // 测试使用原生表达式更新
        int count = jdbcDao.update(UserInfo.class)
                .set("{{userAge}}", "userAge * 2")
                .where("userInfoId", 1L)
                .execute();
        Assertions.assertEquals(1, count);
        UserInfo user = jdbcDao.get(UserInfo.class, 1L);
        Assertions.assertEquals(2, (int) user.getUserAge());
    }

    @Test
    public void testDeleteWithMultipleConditions() {
        // 创建测试数据
        UserInfo user1 = new UserInfo();
        user1.setUserInfoId(300L);
        user1.setLoginName("multiDelete1");
        user1.setPassword("123456");
        user1.setUserAge(30);
        user1.setGmtCreate(new Date());
        jdbcDao.executeInsert(user1);

        UserInfo user2 = new UserInfo();
        user2.setUserInfoId(301L);
        user2.setLoginName("multiDelete2");
        user2.setPassword("123456");
        user2.setUserAge(30);
        user2.setGmtCreate(new Date());
        jdbcDao.executeInsert(user2);

        // 删除多条记录
        int count = jdbcDao.deleteFrom(UserInfo.class)
                .where("userAge", 30)
                .where("loginName", SqlOperator.LIKE, "multiDelete%")
                .execute();
        Assertions.assertEquals(2, count);
    }

    @Test
    public void testSelectWithDistinct() {
        // 测试distinct查询（如果支持）
        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class)
                .addColumn("distinct userType")
                .where("userType", SqlOperator.IS_NOT, null)
                .findList();
        Assertions.assertTrue(list.size() > 0);
    }

    @Test
    public void testSelectOrderByMultipleFields() {
        Page<UserInfo> page = jdbcDao.selectFrom(UserInfo.class)
                .orderBy("userType", OrderBy.ASC)
                .orderBy("userAge", OrderBy.DESC)
                .orderBy("userInfoId", OrderBy.ASC)
                .limit(0, 10)
                .findPage();
        List<UserInfo> list = page.getList();
        Assertions.assertEquals(10, list.size());
    }

    @Test
    public void testInsertIntoFieldMultiple() {
        Long id = (Long) jdbcDao.insertInto(UserInfo.class)
                .intoField("loginName", "multiField1")
                .intoField("password", "multiField2")
                .intoField("userAge", 27)
                .intoField("userType", "multiType")
                .execute();
        Assertions.assertNotNull(id);
        UserInfo user = jdbcDao.get(UserInfo.class, id);
        Assertions.assertEquals("multiField1", user.getLoginName());
        Assertions.assertEquals("multiField2", user.getPassword());
        Assertions.assertEquals(27, (int) user.getUserAge());
        Assertions.assertEquals("multiType", user.getUserType());
    }

    @Test
    public void testUpdateSetMultipleFields() {
        int count = jdbcDao.update(UserInfo.class)
                .set("loginName", "multiUpdate1")
                .set("password", "multiUpdate2")
                .set("userAge", 28)
                .where("userInfoId", 1L)
                .execute();
        Assertions.assertEquals(1, count);
        UserInfo user = jdbcDao.get(UserInfo.class, 1L);
        Assertions.assertEquals("multiUpdate1", user.getLoginName());
        Assertions.assertEquals("multiUpdate2", user.getPassword());
        Assertions.assertEquals(28, (int) user.getUserAge());
    }

    @Test
    public void testSelectWithSubQuery() {
        // 测试子查询
        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class)
                .where("userInfoId", SqlOperator.IN,
                        jdbcDao.selectFrom(UserInfo.class)
                                .addColumn("userInfoId")
                                .where("userAge", SqlOperator.GT, 40)
                                .findListForScalar(Long.class).toArray())
                .findList();
        Assertions.assertEquals(10, list.size());
        Assertions.assertTrue(list.stream().allMatch(u -> u.getUserAge() > 40));
    }

    @Test
    public void testBatchUpdateWithDifferentBatchSize() {
        jdbcDao.executeDeleteAll(UserInfo.class);
        List<UserInfo> userInfoList = new ArrayList<>();
        for (int i = 1; i < 100; i++) {
            UserInfo user = new UserInfo();
            user.setUserInfoId((long) i);
            user.setLoginName("batch-" + i);
            user.setPassword("123456-" + i);
            user.setUserAge(i);
            user.setGmtCreate(new Date());
            userInfoList.add(user);
        }

        String sql = "INSERT INTO UserInfo (userInfoId, loginName, password, userAge, gmtCreate) VALUES (?, ?, ?, ?, ?)";
        jdbcDao.executeBatchUpdate(sql, userInfoList, 20, (ps, names, userInfo) -> {
            ps.setLong(1, userInfo.getUserInfoId());
            ps.setString(2, userInfo.getLoginName());
            ps.setString(3, userInfo.getPassword());
            ps.setInt(4, userInfo.getUserAge());
            ps.setObject(5, userInfo.getGmtCreate());
        });

        long count = jdbcDao.findAllCount(UserInfo.class);
        Assertions.assertEquals(99, count);
    }

    @Test
    public void testSelectWithHavingAndOr() {
        // having子句需要使用and()来连接多个条件
        List<Map<String, Object>> list = jdbcDao.selectFrom(UserInfo.class)
                .addColumn(UserInfo::getUserType)
                .addColumn("count(*) as num")
                .groupBy(UserInfo::getUserType)
                .having("num", SqlOperator.GTE, 5)
                .and(UserInfo::getUserType, SqlOperator.EQ, "0")
                .findListForMap();
        Assertions.assertFalse(list.isEmpty());
    }

    @Test
    public void testSelectLimitWithoutOffset() {
        Page<UserInfo> page = jdbcDao.selectFrom(UserInfo.class)
                .orderBy("userInfoId", OrderBy.ASC)
                .limit(0, 5)
                .findPage();
        List<UserInfo> list = page.getList();
        Assertions.assertEquals(5, list.size());
        Assertions.assertEquals(1L, (long) list.get(0).getUserInfoId());
        Assertions.assertEquals(5L, (long) list.get(4).getUserInfoId());
    }

    @Test
    public void testSelectLimitWithOffset() {
        Page<UserInfo> page = jdbcDao.selectFrom(UserInfo.class)
                .orderBy("userInfoId", OrderBy.ASC)
                //offset所在记录的那一页
                .limit(5, 10)
                .findPage();
        List<UserInfo> list = page.getList();
        Assertions.assertEquals(10, list.size());
        Assertions.assertEquals(1L, (long) list.get(0).getUserInfoId());
        Assertions.assertEquals(10L, (long) list.get(9).getUserInfoId());
    }

    @Test
    public void testSelectPaginateWithDifferentPageSize() {
        Page<UserInfo> page1 = jdbcDao.selectFrom(UserInfo.class)
                .orderBy("userInfoId", OrderBy.ASC)
                .paginate(1, 5)
                .findPage();
        Assertions.assertEquals(5, page1.getList().size());
        Assertions.assertEquals(50, page1.getPagination().getTotalItems());

        Page<UserInfo> page2 = jdbcDao.selectFrom(UserInfo.class)
                .orderBy("userInfoId", OrderBy.ASC)
                .paginate(2, 5)
                .findPage();
        Assertions.assertEquals(5, page2.getList().size());
        Assertions.assertEquals(6L, (long) page2.getList().get(0).getUserInfoId());
    }

    @Test
    public void testSelectFindOneForScalarWithAggregate() {
        Long count = jdbcDao.selectFrom(UserInfo.class)
                .addColumn("count(*)")
                .findOneForScalar(Long.class);
        Assertions.assertEquals(50, (long) count);
    }

    @Test
    public void testSelectFindListForScalarWithMultipleFields() {
        // 当查询多个字段指定为标量时
        Assertions.assertThrows(IncorrectResultSetColumnCountException.class, () -> {
            Page<Long> page = jdbcDao.selectFrom(UserInfo.class)
                    .addColumn("userInfoId", "userAge")
                    .orderBy("userInfoId", OrderBy.ASC)
                    .limit(0, 5)
                    .findPageForScalar(Long.class);
            List<Long> ids = page.getList();
            Assertions.assertEquals(5, ids.size());
            Assertions.assertEquals(1L, (long) ids.get(0));
        });
    }

    @Test
    public void testInsertIntoForObjectWithNullValues() {
        UserInfo user = new UserInfo();
        user.setLoginName("nullTest");
        user.setPassword("123456");
        user.setUserAge(30);
        // 不设置gmtCreate，应该为null或使用默认值
        Long id = (Long) jdbcDao.insertInto(UserInfo.class)
                .intoForObject(user)
                .execute();
        Assertions.assertNotNull(id);
        UserInfo saved = jdbcDao.get(UserInfo.class, id);
        Assertions.assertEquals("nullTest", saved.getLoginName());
    }

    @Test
    public void testUpdateSetForBeanWithNullHandling() {
        UserInfo user = new UserInfo();
        user.setUserInfoId(1L);
        user.setLoginName("nullUpdate");
        // 不设置password，不应该更新为null（默认行为）
        int count = jdbcDao.update(UserInfo.class)
                .setForBean(user)
                .whereForBeanPrimaryKey(user)
                .execute();
        Assertions.assertEquals(1, count);
        UserInfo updated = jdbcDao.get(UserInfo.class, 1L);
        Assertions.assertEquals("nullUpdate", updated.getLoginName());
        // password应该保持原值或为null，取决于数据库
    }

    @Test
    public void testUpdateSetForBeanWithUpdateNull() {
        UserInfo user = new UserInfo();
        user.setUserInfoId(1L);
        user.setLoginName("nullUpdate2");
        // 不设置password，但指定updateNull，应该更新为null
        int count = jdbcDao.update(UserInfo.class)
                .updateNull()
                .setForBean(user)
                .whereForBeanPrimaryKey(user)
                .execute();
        Assertions.assertEquals(1, count);
        UserInfo updated = jdbcDao.get(UserInfo.class, 1L);
        Assertions.assertEquals("nullUpdate2", updated.getLoginName());
    }

    @Test
    public void testSelectWhereForBeanWithPartialFields() {
        UserInfo condition = new UserInfo();
        condition.setUserAge(10);
        condition.setUserType("0");
        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class)
                .whereForBean(condition)
                .findList();
        // 应该找到userAge=10且userType=0的记录
        Assertions.assertTrue(list.size() > 0);
        Assertions.assertTrue(list.stream().allMatch(u -> u.getUserAge() == 10 && "0".equals(u.getUserType())));
    }

    @Test
    public void testDeleteWhereForBean() {
        // 创建测试数据
        UserInfo user = new UserInfo();
        user.setUserInfoId(400L);
        user.setLoginName("beanDelete");
        user.setPassword("123456");
        user.setUserAge(40);
        user.setGmtCreate(new Date());
        jdbcDao.executeInsert(user);

        // 使用bean条件删除
        UserInfo condition = new UserInfo();
        condition.setLoginName("beanDelete");
        condition.setUserAge(40);
        int count = jdbcDao.delete()
                .from(UserInfo.class)
                .whereForBean(condition)
                .execute();
        Assertions.assertEquals(1, count);
        UserInfo deleted = jdbcDao.get(UserInfo.class, 400L);
        Assertions.assertNull(deleted);
    }


    @Test
    public void testOuterJoin() {
        jdbcDao.executeDeleteAll(Account.class);
        for (int i = 1; i < 6; i++) {
            Account account = new Account();
            account.setAccountId((long) i);
            account.setLoginName("account" + i);
            account.setAccountName("accountName" + i);
            account.setPassword("password" + i);
            account.setUserAge(i);
            jdbcDao.executeInsert(account);
        }
        //h2 不支持单独的 outer join
//        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class).as("t1").addAllColumns()
//                .outerJoin(Account.class).as("t2")
//                .on(UserInfo::getUserInfoId, Account::getAccountId)
//                .orderBy("t1.userInfoId", OrderBy.ASC)
//                .findList();
//        Assertions.assertTrue(list.size() >= 50);
    }
}

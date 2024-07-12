/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.jdbc;

import com.sonsure.dumper.common.model.Page;
import com.sonsure.dumper.common.model.Pageable;
import com.sonsure.dumper.core.command.entity.Select;
import com.sonsure.dumper.core.command.lambda.Function;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import com.sonsure.dumper.core.persist.JdbcDao;
import com.sonsure.dumper.test.model.Account;
import com.sonsure.dumper.test.model.AnnotationUserInfo;
import com.sonsure.dumper.test.model.UserInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.io.Serializable;
import java.util.*;

/**
 * Created by liyd on 17/4/12.
 */
@SpringBootTest
public class SpringJdbcDaoTest {

    protected JdbcDao jdbcDao;

    public SpringJdbcDaoTest(@Qualifier("mysqlJdbcDao")JdbcDao jdbcDao) {
        this.jdbcDao = jdbcDao;
    }

    @BeforeEach
    public void before() {

        jdbcDao.executeDelete(UserInfo.class);
        List<UserInfo> users = new ArrayList<>();
        for (int i = 1; i < 51; i++) {
            UserInfo user = new UserInfo();
            user.setUserInfoId((long) i);
            user.setLoginName("name-" + i);
            user.setPassword("123456-" + i);
            user.setUserAge(i);
            user.setGmtCreate(new Date());

            users.add(user);
        }

        String sql = "insert into UserInfo(userInfoId,loginName,password,userAge,gmtCreate) values(?,?,?,?,?)";
        Object count = jdbcDao.executeBatchUpdate(sql, users, users.size(), (ps, paramNames, argument) -> {
            ps.setLong(1, argument.getUserInfoId());
            ps.setString(2, argument.getLoginName());
            ps.setString(3, argument.getPassword());
            ps.setInt(4, argument.getUserAge());
            ps.setObject(5, argument.getGmtCreate());
        });
        Assertions.assertNotNull(count);
    }

    @Test
    public void jdbcDaoGet() {
        UserInfo user = jdbcDao.get(UserInfo.class, 1L);
        Assertions.assertNotNull(user);
        Assertions.assertEquals(1L, (long) user.getUserInfoId());
        Assertions.assertEquals("name-1", user.getLoginName());
        Assertions.assertEquals("123456-1", user.getPassword());
    }

    @Test
    public void jdbcDaoInsert() {

        UserInfo user = new UserInfo();
        user.setLoginName("liyd2017");
        user.setPassword("2017");
        user.setUserAge(18);
        user.setGmtCreate(new Date());

        Long id = (Long) jdbcDao.executeInsert(user);
        Assertions.assertTrue(id > 0);

        UserInfo userInfo = jdbcDao.get(UserInfo.class, id);
        Assertions.assertEquals(user.getLoginName(), userInfo.getLoginName());
        Assertions.assertEquals(user.getPassword(), userInfo.getPassword());
    }


    @Test
    public void jdbcDaoUpdate() {
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
    public void jdbcDaoDelete() {
        int count = jdbcDao.executeDelete(UserInfo.class, 3L);
        Assertions.assertEquals(1, count);
        UserInfo user = jdbcDao.get(UserInfo.class, 3L);
        Assertions.assertNull(user);
    }

    @Test
    public void jdbcDaoNamedDelete() {
        Map<String, Object> params = new HashMap<>();
        params.put("userInfoId", 4L);
        int count = jdbcDao.deleteFrom(UserInfo.class)
                .where()
                .append("userInfoId = :userInfoId", params)
                .namedParameter()
                .execute();

        Assertions.assertEquals(1, count);
        UserInfo user = jdbcDao.get(UserInfo.class, 4L);
        Assertions.assertNull(user);
    }


    @Test
    public void jdbcDaoFind() {
        List<UserInfo> users = jdbcDao.find(UserInfo.class);
        Assertions.assertNotNull(users);
        long count = jdbcDao.findCount(UserInfo.class);
        Assertions.assertEquals(count, users.size());
    }

    @Test
    public void jdbcDaoFind1() {
        UserInfo user = new UserInfo();
        user.setUserAge(10);
        List<UserInfo> users = jdbcDao.find(user);
        Assertions.assertNotNull(users);
        Assertions.assertEquals(1, users.size());
    }

    @Test
    public void jdbcDaoPageResult() {
        UserInfo user = new UserInfo();
        user.setPageSize(10);
        Page<UserInfo> page = jdbcDao.pageResult(user);
        Assertions.assertNotNull(page);
        Assertions.assertEquals(10, page.getList().size());
    }


    @Test
    public void jdbcDaoPageResult2() {
        UserInfo user = new UserInfo();
        user.setPageSize(10);
        user.setUserAge(10);
        Page<UserInfo> page = jdbcDao.pageResult(user);
        Assertions.assertNotNull(page);
        Assertions.assertEquals(1, page.getList().size());
    }

    @Test
    public void jdbcDaoPageResult3() {
        Page<UserInfo> page = jdbcDao.selectFrom(UserInfo.class)
                .orderBy("userInfoId").asc()
                .limit(15, 10)
                .pageResult(UserInfo.class);
        Assertions.assertEquals(11L, (long) page.getList().get(0).getUserInfoId());
        Assertions.assertEquals(20L, (long) page.getList().get(9).getUserInfoId());
    }


    @Test
    public void jdbcDaoCount() {
        UserInfo user = new UserInfo();
        user.setUserAge(10);
        long count = jdbcDao.findCount(user);
        Assertions.assertEquals(1, count);
    }

    @Test
    public void jdbcDaoQueryCount2() {
        long count = jdbcDao.findCount(UserInfo.class);
        Assertions.assertEquals(50, count);
    }

    @Test
    public void jdbcDaoSingleResult() {
        UserInfo tmp = new UserInfo();
        tmp.setUserAge(10);
        UserInfo user = jdbcDao.singleResult(tmp);
        Assertions.assertNotNull(user);
        Assertions.assertEquals(10L, (long) user.getUserInfoId());
        Assertions.assertEquals("name-10", user.getLoginName());
        Assertions.assertEquals(10, (int) user.getUserAge());
        Assertions.assertNotNull(user.getGmtCreate());
    }


    @Test
    public void jdbcDaoFirstResult() {
        UserInfo tmp = new UserInfo();
        tmp.setUserAge(10);
        UserInfo user = jdbcDao.firstResult(tmp);
        Assertions.assertNotNull(user);
        Assertions.assertEquals(10L, (long) user.getUserInfoId());
        Assertions.assertEquals("name-10", user.getLoginName());
        Assertions.assertEquals(10, (int) user.getUserAge());
        Assertions.assertNotNull(user.getGmtCreate());
    }

    @Test
    public void jdbcDaoDelete2() {
        UserInfo user = new UserInfo();
        user.setLoginName("name-17");
        int count = jdbcDao.executeDelete(user);
        Assertions.assertEquals(1, count);

        UserInfo tmp = new UserInfo();
        tmp.setLoginName("name-17");
        UserInfo user1 = jdbcDao.singleResult(tmp);
        Assertions.assertNull(user1);
    }

    @Test
    public void jdbcDaoDelete3() {
        int count = jdbcDao.executeDelete(UserInfo.class);
        Assertions.assertTrue(count > 0);
        long result = jdbcDao.findCount(UserInfo.class);
        Assertions.assertEquals(0, result);
    }

    @Test
    public void insert() {

        UserInfo user = new UserInfo();
        user.setLoginName("name-60");
        user.setPassword("606060");
        user.setUserAge(60);
        user.setGmtCreate(new Date());

        Long id = (Long) jdbcDao.executeInsert(user);

        UserInfo user1 = jdbcDao.get(UserInfo.class, id);
        Assertions.assertNotNull(user1);
        Assertions.assertEquals(user1.getUserInfoId(), id);
        Assertions.assertEquals("name-60", user1.getLoginName());
        Assertions.assertEquals("606060", user1.getPassword());
    }

    @Test
    public void insert2() {

        Long id = (Long) jdbcDao.insertInto(UserInfo.class)
                .set("loginName", "name123")
                .set("password", "123321")
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

        Long id = (Long) jdbcDao.executeInsert(ku);

        AnnotationUserInfo annotationUserInfo = jdbcDao.get(AnnotationUserInfo.class, id);
        Assertions.assertEquals("selfly", annotationUserInfo.getLoginName());
        Assertions.assertEquals("123456", annotationUserInfo.getPassword());
    }

    @Test
    public void select() {

        jdbcDao.executeDelete(UserInfo.class);
        for (int i = 60; i < 70; i++) {
            UserInfo user = new UserInfo();
            user.setUserInfoId((long) i);
            user.setLoginName("name2-19");
            user.setPassword("123456-" + i);
            user.setUserAge(19);
            user.setGmtCreate(new Date());
            jdbcDao.executeInsert(user);
        }

        Select<UserInfo> select1 = jdbcDao.selectFrom(UserInfo.class)
                .where("userAge", 19);
        long count1 = select1.count();
        Assertions.assertEquals(10, count1);

        List<UserInfo> list1 = select1.list();
        Assertions.assertEquals(10, list1.size());

        Page<UserInfo> page1 = select1.paginate(1, 5).pageResult(UserInfo.class);
        Assertions.assertEquals(5, page1.getList().size());

        Select<UserInfo> select2 = jdbcDao.selectFrom(UserInfo.class)
                .where("userAge", 19)
                .and("loginName", "name2-19");
        long count2 = select2.count();
        Assertions.assertEquals(10, count2);

        List<UserInfo> list2 = select2.list();
        Assertions.assertEquals(10, list2.size());

        Page<UserInfo> page2 = select2.paginate(1, 5).pageResult();
        Assertions.assertEquals(5, page2.getList().size());
    }

    @Test
    public void select2() {

        jdbcDao.executeDelete(UserInfo.class);
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
                    .where()
                    .begin()
                    .condition("loginName", "name-19")
                    .or("userAge", 21)
                    .end()
                    .singleResult(UserInfo.class);
        } catch (IncorrectResultSizeDataAccessException e) {
            Assertions.assertEquals("Incorrect result size: expected 1, actual 2", e.getMessage());
        }

        UserInfo user2 = jdbcDao.selectFrom(UserInfo.class)
                .where()
                .begin()
                .condition("loginName", "name-19")
                .or("userAge", 21)
                .end()
                .and("password", "123456-1")
                .singleResult(UserInfo.class);
        Assertions.assertNotNull(user2);
    }

    @Test
    public void select3() {

        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class)
                .addColumn("userInfoId", "password")
                .addColumn(UserInfo::getLoginName)
                //同时指定以白名单为准
                .dropColumn("password")
                .where("userAge", "<=", 10)
                .list();
        Assertions.assertNotNull(list);
        Assertions.assertEquals(10, list.size());
        for (UserInfo user : list) {
            Assertions.assertNotNull(user.getUserInfoId());
            Assertions.assertNotNull(user.getPassword());
            Assertions.assertNotNull(user.getLoginName());
            Assertions.assertNull(user.getUserAge());
            Assertions.assertNull(user.getGmtCreate());
        }
    }

    @Test
    public void select3_1() {

        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class)
                .dropColumn("password")
                .where("userAge", "<=", 10)
                .list();
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
                .dropColumn("userInfoId", "password")
                .orderBy("userAge").asc()
                .list(UserInfo.class);
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
                .oneColResult(Long.class);
        Assertions.assertEquals(50, (long) maxId);
    }

    @Test
    public void select6() {
        List<Long> ids = jdbcDao.selectFrom(UserInfo.class)
                .addColumn(UserInfo::getUserInfoId)
                .oneColList(Long.class);
        Assertions.assertNotNull(ids);
    }


    @Test
    public void select8() {
        Object result = jdbcDao.selectFrom(UserInfo.class)
                .where("userInfoId", 15L)
                .singleMapResult();
        Assertions.assertNotNull(result);
    }

    @Test
    public void select10() {
        Object result = jdbcDao.selectFrom(UserInfo.class)
                .listMaps();
        Assertions.assertNotNull(result);
        //noinspection rawtypes
        Assertions.assertInstanceOf(Map.class, ((List) result).get(0));
    }

    @Test
    public void select11() {
        UserInfo pageable = new UserInfo();
        pageable.setPageSize(5);
        Page<?> result = jdbcDao.selectFrom(UserInfo.class)
                .paginate(pageable)
                .pageMapResult();
        Assertions.assertEquals(5, result.getList().size());
        Assertions.assertInstanceOf(Map.class, result.getList().get(0));
    }

    @Test
    public void select13() {
        List<UserInfo> users = jdbcDao.selectFrom(UserInfo.class, "t1")
                .where("t1.userInfoId", new Object[]{11L, 12L, 13L})
                .and("t1.loginName", new Object[]{"name-11", "name-12", "name-13"})
                .and()
                .condition("t1.userAge", new Object[]{11L, 12L, 13L})
                .list(UserInfo.class);
        Assertions.assertEquals(3, users.size());
    }

    @Test
    public void select14() {
        List<UserInfo> users = jdbcDao.selectFrom(UserInfo.class)
                .where("userInfoId", ">", 10L)
                .and()
                .begin()
                .condition("userAge", 5)
                .or()
                .condition("userAge", 8)
                .or("userAge", new Object[]{7L, 9L})
                .end()
                .list(UserInfo.class);
        Assertions.assertNotNull(users);
    }


    @Test
    public void select15() {


        Select<UserInfo> select = jdbcDao.selectFrom(UserInfo.class);
        select.where("userAge", 5);

        UserInfo user = new UserInfo();
        user.setUserInfoId(10L);
        user.setLoginName("liyd");
        select.andConditionEntity(user);

        List<UserInfo> users = select.list(UserInfo.class);
        Assertions.assertNotNull(users);
    }


    @Test
    public void select16() {


        Select<UserInfo> select = jdbcDao.selectFrom(UserInfo.class);
        //自动处理where情况
        select.and("userAge", 15);

        select.and("userInfoId", 10L);

        List<UserInfo> users = select.list();
        Assertions.assertNotNull(users);
    }


    @Test
    public void select17() {


        Select<UserInfo> select = jdbcDao.selectFrom(UserInfo.class);
        select.where("userAge", 5);

        UserInfo user = new UserInfo();
        user.setUserInfoId(10L);
        user.setLoginName("liyd");
        select.conditionEntity(user, "and", "or");

        List<UserInfo> users = select.list(UserInfo.class);
        Assertions.assertNotNull(users);
    }


    @Test
    public void select18() {
        try {
            jdbcDao.selectFrom(UserInfo.class)
                    .asc()
                    .firstMapResult();
        } catch (SonsureJdbcException e) {
            Assertions.assertEquals("请先指定需要排序的属性", e.getMessage());
        }
    }

    @Test
    public void select19() {
        Map<String, Object> result = jdbcDao.selectFrom(UserInfo.class)
                .orderBy("userInfoId").asc()
                .firstMapResult();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.get("USER_INFO_ID"));
    }

    @Test
    public void select20() {
        Long result = jdbcDao.selectFrom(UserInfo.class)
                .addColumn("userInfoId")
                .orderBy("userInfoId").asc()
                .oneColFirstResult(Long.class);
        Assertions.assertEquals(1L, (long) result);
    }

    @Test
    public void select21() {
        Page<Long> page = jdbcDao.selectFrom(UserInfo.class)
                .addColumn("userInfoId")
                .orderBy("userInfoId").asc()
                .paginate(1, 10)
                .oneColPageResult(Long.class);
        Assertions.assertEquals(10, page.getList().size());
        Assertions.assertEquals(1L, (long) page.getList().get(0));
    }

    @Test
    public void selectColumn() {
        Page<UserInfo> userInfoPage = jdbcDao.selectFrom(UserInfo.class)
                .addColumn((UserInfo::getLoginName))
                .paginate(1, 10)
                .pageResult(UserInfo.class);
        Assertions.assertNotNull(userInfoPage.getList());
        for (UserInfo userInfo : userInfoPage.getList()) {
            Assertions.assertNotNull(userInfo.getLoginName());
            Assertions.assertNull(userInfo.getPassword());
            Assertions.assertNull(userInfo.getUserInfoId());
            Assertions.assertNull(userInfo.getGmtCreate());
            Assertions.assertNull(userInfo.getUserAge());
        }
    }


    @Test
    public void singleResultObject() {

        Object object = jdbcDao.selectFrom(UserInfo.class)
                .addColumn("loginName", "password")
                .where("userInfoId", 5L)
                .singleMapResult();

        Assertions.assertNotNull(object);
    }

    @Test
    public void singleResultObject2() {

        List<Map<String, Object>> list = jdbcDao.selectFrom(UserInfo.class).tableAlias("t1")
                .from(Account.class, "t2")
                .where("{{t1.userInfoId}}", "t2.accountId")
                .listMaps();

        Assertions.assertNotNull(list);

        List<Map<String, Object>> list1 = jdbcDao.selectFrom(UserInfo.class).tableAlias("t1")
                .from(Account.class, "t2")
                .addColumn("t1.loginName as name1", "t2.loginName as name2")
                .where()
                .append("t1.userInfoId = t2.accountId")
                .listMaps();

        Assertions.assertNotNull(list1);
    }

    @Test
    public void selectForAnnotation() {
        Page<AnnotationUserInfo> page = jdbcDao.selectFrom(AnnotationUserInfo.class)
                .paginate(1, 10)
                .pageResult(AnnotationUserInfo.class);
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
                .orderBy("Num").desc();
        Page<Map<String, Object>> page = select.paginate(1, 5).pageMapResult();

        Assertions.assertEquals(5, page.getList().size());

        List<Map<String, Object>> objects = select.listMaps();
        Assertions.assertEquals(50, objects.size());
    }

    @Test
    public void append() {
        UserInfo userInfo = jdbcDao.selectFrom(UserInfo.class)
                .where("userAge", ">", 5)
                .append("and userInfoId = (select max(t2.userInfoId) from UserInfo t2 where t2.userInfoId < ?)", 40)
                .singleResult(UserInfo.class);
        Assertions.assertNotNull(userInfo);
    }

    @Test
    public void append2() {
        Map<String, Object> params = new HashMap<>();
        params.put("userInfoId", 40L);
        UserInfo userInfo = jdbcDao.selectFrom(UserInfo.class)
                .namedParameter()
                .where("userAge", ">", 5)
                .append("and userInfoId = (select max(t2.userInfoId) from UserInfo t2 where t2.userInfoId < :userInfoId)", params)
                .singleResult(UserInfo.class);
        Assertions.assertNotNull(userInfo);
    }

    @Test
    public void updateSet() {

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
        user.setUserInfoId(17L);
        user.setLoginName("newName22");
        user.setPassword("abc");
        //没有设置where条件，将更新所有
        jdbcDao.update(UserInfo.class)
                .setForEntity(user)
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
                .setForEntityWhereId(user)
                .updateNull()
                .execute();

        UserInfo user1 = jdbcDao.get(UserInfo.class, 17L);
        Assertions.assertNull(user1.getLoginName());
        Assertions.assertNull(user1.getPassword());
        Assertions.assertNull(user1.getUserAge());
        Assertions.assertNull(user1.getGmtCreate());
    }


    @Test
    public void updatePkNull() {

        try {
            UserInfo user = new UserInfo();
            user.setLoginName("newName22");
            user.setPassword("abc");
            //没有设置where条件，将更新所有
            jdbcDao.update(UserInfo.class)
                    .setForEntityWhereId(user)
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
        Long id = (Long) jdbcDao.executeInsert(ku);

        AnnotationUserInfo annotationUserInfo = jdbcDao.get(AnnotationUserInfo.class, id);
        Assertions.assertEquals(ku.getLoginName(), annotationUserInfo.getLoginName());
        Assertions.assertEquals(ku.getPassword(), annotationUserInfo.getPassword());
    }

    @Test
    public void updateAnnotation() {
        jdbcDao.executeDelete(AnnotationUserInfo.class);
        AnnotationUserInfo ai = new AnnotationUserInfo();
        ai.setRowId(36L);
        ai.setLoginName("666");
        ai.setPassword("6666");
        Object id = jdbcDao.executeInsert(ai);
        Assertions.assertEquals(id, 36L);

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

        Long id = (Long) jdbcDao.executeInsert(user);
        jdbcDao.update(UserInfo.class)
                .set("{{userAge}}", "userAge+1")
                .where("userInfoId", id)
                .execute();

        UserInfo user2 = jdbcDao.get(UserInfo.class, id);
        Assertions.assertEquals(18, (int) user2.getUserAge());
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
            Assertions.assertEquals(user.getLoginName(), "newName");
            Assertions.assertTrue(user.getUserAge() >= 10 && user.getUserAge() <= 30);
        }
        Assertions.assertTrue(ages.size() > 1);
    }


    @Test
    public void nativeExecutor() {
        int count = jdbcDao.nativeExecutor()
                .command("update UserInfo set loginName = ? where userInfoId = ?")
                .parameters(new Object[]{"newName", 39L})
                .update();
        Assertions.assertEquals(1, count);
        UserInfo user = jdbcDao.get(UserInfo.class, 39L);
        Assertions.assertEquals(user.getLoginName(), "newName");
    }

    @Test
    public void nativeExecutor1() {
        int count = jdbcDao.nativeExecutor()
                .namedParameter()
                .nativeCommand()
                .command("update sd_User_Info set login_Name = :loginName where user_Info_Id = :userInfoId")
                .parameter("loginName", "newName")
                .parameter("userInfoId", 39L)
                .update();
        Assertions.assertEquals(1, count);
        UserInfo user = jdbcDao.get(UserInfo.class, 39L);
        Assertions.assertEquals(user.getLoginName(), "newName");
    }

    @Test
    public void nativeExecutor2() {

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
                .nativeCommand()
                .command(sql)
                .parameters(params)
                .update();
        Assertions.assertEquals(4, count);
        UserInfo user = jdbcDao.get(UserInfo.class, 39L);
        Assertions.assertEquals(user.getLoginName(), "newName");
    }


    @Test
    public void nativeExecutor4() {
        int count = jdbcDao.nativeExecutor()
                .command("update UserInfo set loginName = ? where userInfoId = ?")
                .parameters("newName", 39L)
                .update();
        Assertions.assertEquals(1, count);
        UserInfo user = jdbcDao.get(UserInfo.class, 39L);
        Assertions.assertEquals(user.getLoginName(), "newName");
    }


    @Test
    public void nativeExecutor5() {

        long result = jdbcDao.nativeExecutor()
                .command("select count(*) from UserInfo")
                .count();
        Assertions.assertEquals(50, result);
    }

    @Test
    public void nativeExecutor7() {

        jdbcDao.nativeExecutor()
                .command("update sd_user_info set user_age = 18 where user_age < 18")
                .nativeCommand()
                .execute();

        long count = jdbcDao.selectFrom(UserInfo.class)
                .where("userAge", "<", 18)
                .count();
        Assertions.assertEquals(0, count);
    }

    @Test
    public void nativeExecutor8() {

        jdbcDao.nativeExecutor()
                .command("update UserInfo set userAge = 18 where userAge < 18")
                .execute();

        long count = jdbcDao.selectFrom(UserInfo.class)
                .where("userAge", "<", 18)
                .count();
        Assertions.assertEquals(0, count);
    }

    @Test
    public void nativeOneColResult() {
        Integer integer = jdbcDao.nativeExecutor()
                .command("select sum(userAge) from UserInfo")
                .oneColResult(Integer.class);
        Assertions.assertTrue(integer > 0);
    }

    @Test
    public void nativeFirstResult() {
        UserInfo userInfo = jdbcDao.nativeExecutor()
                .command("select * from UserInfo order by userInfoId asc")
                .firstResult(UserInfo.class);

        Assertions.assertEquals(1L, (long) userInfo.getUserInfoId());
    }

    @Test
    public void nativeOneColFirstResult() {
        Long id = jdbcDao.nativeExecutor()
                .command("select userInfoId from UserInfo order by userInfoId asc")
                .oneColFirstResult(Long.class);

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
    public void nativeList() {
        List<UserInfo> list = jdbcDao.nativeExecutor()
                .command("select * from UserInfo")
                .list(UserInfo.class);

        Assertions.assertTrue(list != null && !list.isEmpty());
    }

    @Test
    public void nativeOneColList() {
        List<Long> list = jdbcDao.nativeExecutor()
                .command("select userInfoId from UserInfo")
                .oneColList(Long.class);

        Assertions.assertTrue(list != null && !list.isEmpty());
    }

    @SuppressWarnings("CastCanBeRemovedNarrowingVariableType")
    @Test
    public void nativeFirstList() {
        Object result = jdbcDao.nativeExecutor()
                .command("select userInfoId from UserInfo")
                .firstMapResult();
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
                .pageMapResult();
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
                .pageResult(Account.class);
        Assertions.assertEquals(5, page.getList().size());
        Assertions.assertEquals("name-6", page.getList().get(0).getLoginName());
        Assertions.assertEquals("name-10", page.getList().get(4).getLoginName());
    }

    @Test
    public void nativeLimitPage() {
        Page<Map<String, Object>> page = jdbcDao.nativeExecutor()
                .command("select userInfoId from UserInfo order by userInfoId asc")
                .limit(15, 10)
                .pageMapResult();
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
                .singleResult(UserInfo.class);

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
                .nativeCommand()
                .singleResult(UserInfo.class);

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
                .nativeCommand()
                .singleMapResult();

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
                .nativeCommand()
                .listMaps();

        Assertions.assertEquals(3, list.size());
    }

    @Test
    public void mybatisExecutor5() {

        int update = jdbcDao.myBatisExecutor()
                .command("updateUser")
                .parameter("loginName", "newName")
                .parameter("userInfoId", 9L)
                .nativeCommand()
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
                .singleMapResult();

        Assertions.assertNotNull(user);
    }


    @Test
    public void mybatisExecutor7() {
        UserInfo userInfo = jdbcDao.myBatisExecutor()
                .command("getUser3")
                .parameter("userInfoId", 9L)
                .parameter("loginName", "name-9")
                .singleResult(UserInfo.class);

        Assertions.assertEquals("name-9", userInfo.getLoginName());
    }

    @Test
    public void mybatisExecutor8() {
        Page<UserInfo> page = jdbcDao.myBatisExecutor()
                .command("queryUserList2")
                .paginate(1, 10)
                .pageResult(UserInfo.class);

        Assertions.assertEquals(10, page.getList().size());
    }

    @Test
    public void lambdaSelect() {
        UserInfo userInfo = new UserInfo();
        userInfo.setLoginName("name-6");
        userInfo.setPassword("123456-6");
        Function<UserInfo, String> getLoginName = UserInfo::getLoginName;
        List<UserInfo> list = jdbcDao.selectFrom(UserInfo.class)
                .and(getLoginName, userInfo.getLoginName())
                .and(UserInfo::getPassword, userInfo.getPassword())
                .list(UserInfo.class);

        Assertions.assertFalse(list.isEmpty());

    }

    @Test
    public void batchUpdate() {

        jdbcDao.executeDelete(UserInfo.class);
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
                .nativeCommand()
                .execute(sql, userInfoList, 1000, (ps, names, userInfo) -> {
                    ps.setString(1, userInfo.getPassword());
                    ps.setString(2, userInfo.getLoginName());
                    ps.setDate(3, new java.sql.Date(userInfo.getGmtCreate().getTime()));
                    ps.setInt(4, userInfo.getUserAge());
                    ps.setLong(5, userInfo.getUserInfoId());
                });


        final long end = System.currentTimeMillis();
        System.out.println("daoTemplate插入耗时:" + (end - begin));
        Assertions.assertEquals(userInfoList.size(), jdbcDao.findCount(UserInfo.class));

    }

    @Test
    public void batchUpdate1() {

        jdbcDao.executeDelete(UserInfo.class);
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
        System.out.println("daoTemplate插入耗时:" + (end1 - begin1));
        Assertions.assertEquals(userInfoList.size(), jdbcDao.findCount(UserInfo.class));

    }

    @Test
    public void batchUpdate2() {

        jdbcDao.executeDelete(UserInfo.class);
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
                .nativeCommand()
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

        long count = jdbcDao.findCount(UserInfo.class);
        Assertions.assertTrue(count >= 10000);

        long count1 = jdbcDao.selectFrom(UserInfo.class)
                .where(UserInfo::getLoginName, "name-9999")
                .count();
        Assertions.assertTrue(count1 > 0);

    }


    @Test
    public void testIf() {

        UserInfo addUser = new UserInfo();
        addUser.setLoginName("name-1");
        addUser.setPassword("123456-a");
        addUser.setUserAge(18);
        addUser.setGmtCreate(new Date());

        System.out.println("jdbcDao:" + jdbcDao);
        jdbcDao.executeInsert(addUser);

        long count = jdbcDao.selectFrom(UserInfo.class)
                .where(UserInfo::getLoginName, "name-1")
                .and(UserInfo::getPassword, "123456-1")
                .count();
        Assertions.assertEquals(1, count);

        long count1 = jdbcDao.selectFrom(UserInfo.class)
                .where(UserInfo::getLoginName, "name-1")
                .iff(false).and(UserInfo::getPassword, "123456-1")
                .count();
        Assertions.assertEquals(2, count1);

        UserInfo user = new UserInfo();
        user.setLoginName("name-1");
        long count2 = jdbcDao.selectFrom(UserInfo.class)
                .where()
                .conditionEntity(user)
                .count();
        Assertions.assertEquals(2, count2);

        long count3 = jdbcDao.selectFrom(UserInfo.class)
                .where()
                .iff(user.getPassword() != null).conditionEntity(user)
                .count();
        Assertions.assertEquals(51, count3);

        long count4 = jdbcDao.selectFrom(UserInfo.class)
                .where()
                .iff(user.getPassword() != null).conditionEntity(user)
                .condition(UserInfo::getLoginName, "name-5")
                .count();
        Assertions.assertEquals(1, count4);

        long count5 = jdbcDao.selectFrom(UserInfo.class)
                .where()
                .iff(addUser.getUserAge() != null).condition(UserInfo::getUserAge, addUser.getUserAge())
                .and(UserInfo::getPassword, addUser.getPassword())
                .count();
        Assertions.assertEquals(1, count5);
    }

    @Test
    public void testWith() {

        UserInfo addUser = new UserInfo();
        addUser.setLoginName("name-1");
        addUser.setPassword("123456-a");
        addUser.setUserAge(18);
        addUser.setGmtCreate(new Date());
        jdbcDao.executeInsert(addUser);

        long count = jdbcDao.selectFrom(UserInfo.class)
                .where(UserInfo::getLoginName, "name-1")
                .and(UserInfo::getPassword, "123456-1")
                .count();
        Assertions.assertEquals(1, count);

        long count1 = jdbcDao.selectFrom(UserInfo.class)
                .where(UserInfo::getLoginName, "name-1")
                .and(UserInfo::getPassword, "123456-1").with(false)
                .count();
        Assertions.assertEquals(2, count1);

        UserInfo user = new UserInfo();
        user.setLoginName("name-1");
        long count2 = jdbcDao.selectFrom(UserInfo.class)
                .where()
                .conditionEntity(user)
                .count();
        Assertions.assertEquals(2, count2);

        long count3 = jdbcDao.selectFrom(UserInfo.class)
                .where()
                .conditionEntity(user).with(user.getPassword() != null)
                .count();
        Assertions.assertEquals(51, count3);
    }
}

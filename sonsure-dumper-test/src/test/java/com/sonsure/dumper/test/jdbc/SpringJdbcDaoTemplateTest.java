/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.jdbc;

import com.sonsure.commons.model.Page;
import com.sonsure.commons.model.Pageable;
import com.sonsure.commons.utils.FileIOUtils;
import com.sonsure.dumper.core.command.batch.ParameterizedSetter;
import com.sonsure.dumper.core.command.entity.Select;
import com.sonsure.dumper.core.command.lambda.Function;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import com.sonsure.dumper.core.persist.JdbcDao;
import com.sonsure.dumper.test.model.Account;
import com.sonsure.dumper.test.model.AnnotationUserInfo;
import com.sonsure.dumper.test.model.UserInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by liyd on 17/4/12.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class SpringJdbcDaoTemplateTest {

    @Autowired
    protected JdbcDao daoTemplate;

    @Before
    public void before() {

        daoTemplate.executeDelete(UserInfo.class);
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
        daoTemplate.executeBatchUpdate(sql, users, users.size(), new ParameterizedSetter<UserInfo>() {
            @Override
            public void setValues(PreparedStatement ps, List<String> paramNames, UserInfo argument) throws SQLException {
                ps.setLong(1, argument.getUserInfoId());
                ps.setString(2, argument.getLoginName());
                ps.setString(3, argument.getPassword());
                ps.setInt(4, argument.getUserAge());
                ps.setObject(5, argument.getGmtCreate());
            }
        });
    }

    @Test
    public void jdbcDaoGet() {
        UserInfo user = daoTemplate.get(UserInfo.class, 1L);
        Assert.assertNotNull(user);
        Assert.assertEquals(1L, (long) user.getUserInfoId());
        Assert.assertEquals("name-1", user.getLoginName());
        Assert.assertEquals("123456-1", user.getPassword());
    }

    @Test
    public void jdbcDaoInsert() {

        UserInfo user = new UserInfo();
        user.setLoginName("liyd2017");
        user.setPassword("2017");
        user.setUserAge(18);
        user.setGmtCreate(new Date());

        Long id = (Long) daoTemplate.executeInsert(user);
        Assert.assertTrue(id > 0);

        UserInfo userInfo = daoTemplate.get(UserInfo.class, id);
        Assert.assertEquals(user.getLoginName(), userInfo.getLoginName());
        Assert.assertEquals(user.getPassword(), userInfo.getPassword());
    }


    @Test
    public void jdbcDaoUpdate() {
        UserInfo user = new UserInfo();
        user.setUserInfoId(2L);
        user.setPassword("666666");
        user.setLoginName("666777");
        user.setGmtModify(new Date());
        int count = daoTemplate.executeUpdate(user);
        Assert.assertEquals(1, count);

        UserInfo user1 = daoTemplate.get(UserInfo.class, 2L);
        Assert.assertNotNull(user1);
        Assert.assertEquals(2L, (long) user1.getUserInfoId());
        Assert.assertEquals("666777", user1.getLoginName());
        Assert.assertEquals("666666", user1.getPassword());
        Assert.assertNotNull(user1.getGmtModify());
    }

    @Test
    public void jdbcDaoDelete() {
        int count = daoTemplate.executeDelete(UserInfo.class, 3L);
        Assert.assertEquals(1, count);
        UserInfo user = daoTemplate.get(UserInfo.class, 3L);
        Assert.assertNull(user);
    }

    @Test
    public void jdbcDaoNamedDelete() {
        Map<String, Object> params = new HashMap<>();
        params.put("userInfoId", 4L);
        int count = daoTemplate.deleteFrom(UserInfo.class)
                .where()
                .append("userInfoId = :userInfoId", params)
                .namedParameter()
                .execute();

        Assert.assertEquals(1, count);
        UserInfo user = daoTemplate.get(UserInfo.class, 4L);
        Assert.assertNull(user);
    }


    @Test
    public void jdbcDaoFind() {
        List<UserInfo> users = daoTemplate.find(UserInfo.class);
        Assert.assertNotNull(users);
        long count = daoTemplate.findCount(UserInfo.class);
        Assert.assertEquals(count, users.size());
    }

    @Test
    public void jdbcDaoFind1() {
        UserInfo user = new UserInfo();
        user.setUserAge(10);
        List<UserInfo> users = daoTemplate.find(user);
        Assert.assertNotNull(users);
        Assert.assertEquals(1, users.size());
    }

    @Test
    public void jdbcDaoPageResult() {
        UserInfo user = new UserInfo();
        user.setPageSize(10);
        Page<UserInfo> page = daoTemplate.pageResult(user);
        Assert.assertNotNull(page);
        Assert.assertEquals(10, page.getList().size());
    }


    @Test
    public void jdbcDaoPageResult2() {
        UserInfo user = new UserInfo();
        user.setPageSize(10);
        user.setUserAge(10);
        Page<UserInfo> page = daoTemplate.pageResult(user);
        Assert.assertNotNull(page);
        Assert.assertEquals(1, page.getList().size());
    }

    @Test
    public void jdbcDaoPageResult3() {
        Page<UserInfo> page = daoTemplate.selectFrom(UserInfo.class)
                .orderBy("userInfoId").asc()
                .limit(15, 10)
                .pageResult(UserInfo.class);
        Assert.assertEquals(11L, (long) page.getList().get(0).getUserInfoId());
        Assert.assertEquals(20L, (long) page.getList().get(9).getUserInfoId());
    }


    @Test
    public void jdbcDaoCount() {
        UserInfo user = new UserInfo();
        user.setUserAge(10);
        long count = daoTemplate.findCount(user);
        Assert.assertEquals(1, count);
    }

    @Test
    public void jdbcDaoQueryCount2() {
        long count = daoTemplate.findCount(UserInfo.class);
        Assert.assertEquals(50, count);
    }

    @Test
    public void jdbcDaoSingleResult() {
        UserInfo tmp = new UserInfo();
        tmp.setUserAge(10);
        UserInfo user = daoTemplate.singleResult(tmp);
        Assert.assertNotNull(user);
        Assert.assertEquals(10L, (long) user.getUserInfoId());
        Assert.assertEquals("name-10", user.getLoginName());
        Assert.assertEquals(10, (int) user.getUserAge());
        Assert.assertNotNull(user.getGmtCreate());
    }


    @Test
    public void jdbcDaoFirstResult() {
        UserInfo tmp = new UserInfo();
        tmp.setUserAge(10);
        UserInfo user = daoTemplate.firstResult(tmp);
        Assert.assertNotNull(user);
        Assert.assertEquals(10L, (long) user.getUserInfoId());
        Assert.assertEquals("name-10", user.getLoginName());
        Assert.assertEquals(10, (int) user.getUserAge());
        Assert.assertNotNull(user.getGmtCreate());
    }

    @Test
    public void jdbcDaoDelete2() {
        UserInfo user = new UserInfo();
        user.setLoginName("name-17");
        int count = daoTemplate.executeDelete(user);
        Assert.assertEquals(1, count);

        UserInfo tmp = new UserInfo();
        tmp.setLoginName("name-17");
        UserInfo user1 = daoTemplate.singleResult(tmp);
        Assert.assertNull(user1);
    }

    @Test
    public void jdbcDaoDelete3() {
        int count = daoTemplate.executeDelete(UserInfo.class);
        Assert.assertTrue(count > 0);
        long result = daoTemplate.findCount(UserInfo.class);
        Assert.assertEquals(0, result);
    }

    @Test
    public void insert() {

        UserInfo user = new UserInfo();
        user.setLoginName("name-60");
        user.setPassword("606060");
        user.setUserAge(60);
        user.setGmtCreate(new Date());

        Long id = (Long) daoTemplate.executeInsert(user);

        UserInfo user1 = daoTemplate.get(UserInfo.class, id);
        Assert.assertNotNull(user1);
        Assert.assertEquals(user1.getUserInfoId(), id);
        Assert.assertEquals("name-60", user1.getLoginName());
        Assert.assertEquals("606060", user1.getPassword());
    }

    @Test
    public void insert2() {

        Long id = (Long) daoTemplate.insertInto(UserInfo.class)
                .set("loginName", "name123")
                .set("password", "123321")
                .execute();

        UserInfo user1 = daoTemplate.get(UserInfo.class, id);
        Assert.assertNotNull(user1);
        Assert.assertEquals(user1.getUserInfoId(), id);
        Assert.assertEquals("name123", user1.getLoginName());
        Assert.assertEquals("123321", user1.getPassword());
    }

    @Test
    public void insertForAnnotation() {

        AnnotationUserInfo ku = new AnnotationUserInfo();
        ku.setLoginName("selfly");
        ku.setPassword("123456");
        ku.setUserAge(18);
        ku.setGmtCreate(new Date());
        ku.setGmtModify(new Date());

        Long id = (Long) daoTemplate.executeInsert(ku);

        AnnotationUserInfo annotationUserInfo = daoTemplate.get(AnnotationUserInfo.class, id);
        Assert.assertEquals("selfly", annotationUserInfo.getLoginName());
        Assert.assertEquals("123456", annotationUserInfo.getPassword());
    }

    @Test
    public void select() {

        daoTemplate.executeDelete(UserInfo.class);
        for (int i = 60; i < 70; i++) {
            UserInfo user = new UserInfo();
            user.setUserInfoId((long) i);
            user.setLoginName("name2-19");
            user.setPassword("123456-" + i);
            user.setUserAge(19);
            user.setGmtCreate(new Date());
            daoTemplate.executeInsert(user);
        }

        Select select1 = daoTemplate.selectFrom(UserInfo.class)
                .where("userAge", 19);
        long count1 = select1.count();
        Assert.assertEquals(10, count1);

        List<UserInfo> list1 = select1.list(UserInfo.class);
        Assert.assertEquals(10, list1.size());

        Page<UserInfo> page1 = select1.paginate(1, 5).pageResult(UserInfo.class);
        Assert.assertEquals(5, page1.getList().size());

        Select select2 = daoTemplate.selectFrom(UserInfo.class)
                .where("userAge", 19)
                .and("loginName", "name2-19");
        long count2 = select2.count();
        Assert.assertEquals(10, count2);

        List<UserInfo> list2 = select2.list(UserInfo.class);
        Assert.assertEquals(10, list2.size());

        Page<UserInfo> page2 = select2.paginate(1, 5).pageResult(UserInfo.class);
        Assert.assertEquals(5, page2.getList().size());
    }

    @Test
    public void select2() {

        daoTemplate.executeDelete(UserInfo.class);
        for (int i = 1; i < 3; i++) {
            UserInfo user = new UserInfo();
            user.setUserInfoId((long) i);
            user.setLoginName("name-19");
            user.setPassword("123456-" + i);
            user.setUserAge(21);
            user.setGmtCreate(new Date());
            daoTemplate.executeInsert(user);
        }
        try {
            daoTemplate.selectFrom(UserInfo.class)
                    .where()
                    .begin()
                    .condition("loginName", "name-19")
                    .or("userAge", 21)
                    .end()
                    .singleResult(UserInfo.class);
        } catch (IncorrectResultSizeDataAccessException e) {
            Assert.assertEquals("Incorrect result size: expected 1, actual 2", e.getMessage());
        }

        UserInfo user2 = daoTemplate.selectFrom(UserInfo.class)
                .where()
                .begin()
                .condition("loginName", "name-19")
                .or("userAge", 21)
                .end()
                .and("password", "123456-1")
                .singleResult(UserInfo.class);
        Assert.assertNotNull(user2);
    }

    @Test
    public void select3() {

        List<UserInfo> list = daoTemplate.select("user_Info_Id", "password").from(UserInfo.class)
                .where("user_Age", "<=", 10)
                .list(UserInfo.class);
        Assert.assertNotNull(list);
        Assert.assertEquals(10, list.size());
        for (UserInfo user : list) {
            Assert.assertNotNull(user.getUserInfoId());
            Assert.assertNotNull(user.getPassword());
            Assert.assertNull(user.getLoginName());
            Assert.assertNull(user.getUserAge());
            Assert.assertNull(user.getGmtCreate());
        }
    }

    @Test
    public void select4() {

        List<UserInfo> list = daoTemplate.selectFrom(UserInfo.class)
                .exclude("userInfoId", "password")
                .orderBy("userAge").asc()
                .list(UserInfo.class);
        Assert.assertNotNull(list);
        int preAge = 0;
        for (UserInfo user : list) {
            Assert.assertNull(user.getUserInfoId());
            Assert.assertNull(user.getPassword());
            Assert.assertNotNull(user.getLoginName());
            Assert.assertNotNull(user.getUserAge());
            Assert.assertNotNull(user.getGmtCreate());
            Assert.assertTrue(user.getUserAge() > preAge);
            preAge = user.getUserAge();
        }
    }

    @Test
    public void select5() {

        Long maxId = daoTemplate.select("max(userInfoId) maxid").from(UserInfo.class)
                .oneColResult(Long.class);
        Assert.assertEquals(50, (long) maxId);
    }

    @Test
    public void select6() {
        List<Long> ids = daoTemplate.select("userInfoId").from(UserInfo.class)
                .oneColList(Long.class);
        Assert.assertNotNull(ids);
    }


    @Test
    public void select8() {
        Object result = daoTemplate.selectFrom(UserInfo.class)
                .where("userInfoId", 15L)
                .singleResult();
        Assert.assertNotNull(result);
    }

    @SuppressWarnings("CastCanBeRemovedNarrowingVariableType")
    @Test
    public void select10() {
        Object result = daoTemplate.selectFrom(UserInfo.class)
                .list();
        Assert.assertNotNull(result);
        Assert.assertTrue(((List<?>) result).get(0) instanceof Map);
    }

    @Test
    public void select11() {
        UserInfo pageable = new UserInfo();
        pageable.setPageSize(5);
        Page<?> result = daoTemplate.selectFrom(UserInfo.class)
                .paginate(pageable)
                .pageResult();
        Assert.assertEquals(5, result.getList().size());
        Assert.assertTrue(result.getList().get(0) instanceof Map);
    }

    @Test
    public void select13() {
        List<UserInfo> users = daoTemplate.select().from(UserInfo.class, "t1")
                .where("t1.userInfoId", new Object[]{11L, 12L, 13L})
                .and("t1.loginName", new Object[]{"name-11", "name-12", "name-13"})
                .and()
                .condition("t1.userAge", new Object[]{11L, 12L, 13L})
                .list(UserInfo.class);
        Assert.assertEquals(3, users.size());
    }

    @Test
    public void select14() {
        List<UserInfo> users = daoTemplate.selectFrom(UserInfo.class)
                .where("userInfoId", ">", 10L)
                .and()
                .begin()
                .condition("userAge", 5)
                .or()
                .condition("userAge", 8)
                .or("userAge", new Object[]{7L, 9L})
                .end()
                .list(UserInfo.class);
        Assert.assertNotNull(users);
    }


    @Test
    public void select15() {


        Select select = daoTemplate.selectFrom(UserInfo.class);
        select.where("userAge", 5);

        UserInfo user = new UserInfo();
        user.setUserInfoId(10L);
        user.setLoginName("liyd");
        select.andConditionEntity(user);

        List<UserInfo> users = select.list(UserInfo.class);
        Assert.assertNotNull(users);
    }


    @Test
    public void select16() {


        Select select = daoTemplate.selectFrom(UserInfo.class);
        //自动处理where情况
        select.and("userAge", 15);

        select.and("userInfoId", 10L);

        List<UserInfo> users = select.list(UserInfo.class);
        Assert.assertNotNull(users);
    }


    @Test
    public void select17() {


        Select select = daoTemplate.selectFrom(UserInfo.class);
        select.where("userAge", 5);

        UserInfo user = new UserInfo();
        user.setUserInfoId(10L);
        user.setLoginName("liyd");
        select.conditionEntity(user, "and", "or");

        List<UserInfo> users = select.list(UserInfo.class);
        Assert.assertNotNull(users);
    }


    @Test
    public void select18() {
        try {
            daoTemplate.select()
                    .from(UserInfo.class)
                    .asc()
                    .firstResult();
        } catch (SonsureJdbcException e) {
            Assert.assertEquals("请先指定需要排序的属性", e.getMessage());
        }
    }

    @SuppressWarnings({"unchecked", "CastCanBeRemovedNarrowingVariableType"})
    @Test
    public void select19() {
        Object result = daoTemplate.select()
                .from(UserInfo.class)
                .orderBy("userInfoId").asc()
                .firstResult();
        Assert.assertNotNull(result);
        Map<String, Object> map = (Map<String, Object>) result;
        Assert.assertEquals(1L, map.get("USER_INFO_ID"));
    }

    @Test
    public void select20() {
        Long result = daoTemplate.select("userInfoId")
                .from(UserInfo.class)
                .orderBy("userInfoId").asc()
                .oneColFirstResult(Long.class);
        Assert.assertEquals(1L, (long) result);
    }

    @Test
    public void select21() {
        Page<Long> page = daoTemplate.select("userInfoId")
                .from(UserInfo.class)
                .orderBy("userInfoId").asc()
                .paginate(1, 10)
                .oneColPageResult(Long.class);
        Assert.assertEquals(10, page.getList().size());
        Assert.assertEquals(1L, (long) page.getList().get(0));
    }

    @Test
    public void selectColumn() {
        Page<UserInfo> userInfoPage = daoTemplate.select(UserInfo::getLoginName)
                .from(UserInfo.class)
                .paginate(1, 10)
                .pageResult(UserInfo.class);
        Assert.assertNotNull(userInfoPage.getList());
        for (UserInfo userInfo : userInfoPage.getList()) {
            Assert.assertNotNull(userInfo.getLoginName());
            Assert.assertNull(userInfo.getPassword());
            Assert.assertNull(userInfo.getUserInfoId());
            Assert.assertNull(userInfo.getGmtCreate());
            Assert.assertNull(userInfo.getUserAge());
        }
    }


    @Test
    public void singleResultObject() {

        Object object = daoTemplate.select("loginName", "password")
                .from(UserInfo.class)
                .where("userInfoId", 5L)
                .singleResult();

        Assert.assertNotNull(object);
    }

    @Test
    public void singleResultObject2() {

        List<Map<String, Object>> list = daoTemplate.select().from(UserInfo.class, "t1", Account.class, "t2")
                .where("{{t1.userInfoId}}", "t2.accountId")
                .list();

        Assert.assertNotNull(list);

        List<Map<String, Object>> list1 = daoTemplate.select("t1.loginName as name1", "t2.loginName as name2").from(UserInfo.class, "t1", Account.class, "t2")
                .where()
                .append("t1.userInfoId = t2.accountId")
                .list();

        Assert.assertNotNull(list1);
    }

    @Test
    public void selectForAnnotation() {
        Page<AnnotationUserInfo> page = daoTemplate.selectFrom(AnnotationUserInfo.class)
                .paginate(1, 10)
                .pageResult(AnnotationUserInfo.class);
        Assert.assertTrue(page.getList().size() > 0);
        Assert.assertNotNull(page.getList().get(0).getLoginName());
    }


    @Test
    public void groupByAndOrderBy() {
        UserInfo user = new UserInfo();
        user.setLoginName("name2-19");
        user.setPassword("123456-");
        user.setUserAge(19);
        user.setGmtCreate(new Date());
        daoTemplate.executeInsert(user);

        Select select = daoTemplate.select("count(*) Num").from(UserInfo.class)
                .groupBy("userAge")
                .orderBy("Num").desc();
        Page<Map<String, Object>> page = select.paginate(1, 5).pageResult();

        Assert.assertEquals(5, page.getList().size());

        List<Map<String, Object>> objects = select.list();
        Assert.assertEquals(50, objects.size());
    }

    @Test
    public void append() {
        UserInfo userInfo = daoTemplate.selectFrom(UserInfo.class)
                .where("userAge", ">", 5)
                .append("and userInfoId = (select max(t2.userInfoId) from UserInfo t2 where t2.userInfoId < ?)", 40)
                .singleResult(UserInfo.class);
        Assert.assertNotNull(userInfo);
    }

    @Test
    public void append2() {
        Map<String, Object> params = new HashMap<>();
        params.put("userInfoId", 40L);
        UserInfo userInfo = daoTemplate.selectFrom(UserInfo.class)
                .namedParameter()
                .where("userAge", ">", 5)
                .append("and userInfoId = (select max(t2.userInfoId) from UserInfo t2 where t2.userInfoId < :userInfoId)", params)
                .singleResult(UserInfo.class);
        Assert.assertNotNull(userInfo);
    }

    @Test
    public void updateSet() {

        daoTemplate.update(UserInfo.class)
                .set("loginName", "newName")
                .where("userInfoId", 15L)
                .execute();

        UserInfo user1 = daoTemplate.get(UserInfo.class, 15L);
        Assert.assertEquals("newName", user1.getLoginName());
    }

    @Test
    public void updateForEntity() {

        UserInfo user = new UserInfo();
        user.setUserInfoId(17L);
        user.setLoginName("newName22");
        user.setPassword("abc");
        //没有设置where条件，将更新所有
        daoTemplate.update(UserInfo.class)
                .setForEntity(user)
                .execute();

        UserInfo user1 = daoTemplate.get(UserInfo.class, 17L);
        Assert.assertEquals("newName22", user1.getLoginName());
        Assert.assertEquals("abc", user1.getPassword());
    }

    @Test
    public void updateNull() {

        UserInfo user = new UserInfo();
        user.setUserInfoId(17L);
        daoTemplate.update(UserInfo.class)
                .setForEntityWhereId(user)
                .updateNull()
                .execute();

        UserInfo user1 = daoTemplate.get(UserInfo.class, 17L);
        Assert.assertNull(user1.getLoginName());
        Assert.assertNull(user1.getPassword());
        Assert.assertNull(user1.getUserAge());
        Assert.assertNull(user1.getGmtCreate());
    }


    @Test
    public void updatePkNull() {

        try {
            UserInfo user = new UserInfo();
            user.setLoginName("newName22");
            user.setPassword("abc");
            //没有设置where条件，将更新所有
            daoTemplate.update(UserInfo.class)
                    .setForEntityWhereId(user)
                    .execute();
        } catch (Exception e) {
            Assert.assertEquals("主键属性值不能为空:userInfoId", e.getMessage());
        }
    }

    @Test
    public void insertAnnotation() {

        AnnotationUserInfo ku = new AnnotationUserInfo();
        ku.setLoginName("insertName");
        ku.setPassword("123456");
        ku.setUserAge(18);
        ku.setGmtCreate(new Date());
        Long id = (Long) daoTemplate.executeInsert(ku);

        AnnotationUserInfo annotationUserInfo = daoTemplate.get(AnnotationUserInfo.class, id);
        Assert.assertEquals(ku.getLoginName(), annotationUserInfo.getLoginName());
        Assert.assertEquals(ku.getPassword(), annotationUserInfo.getPassword());
    }

    @Test
    public void updateAnnotation() {

        AnnotationUserInfo ku = new AnnotationUserInfo();
        ku.setRowId(36L);
        ku.setLoginName("777777");
        ku.setPassword("787878");
        ku.setGmtModify(new Date());
        int count = daoTemplate.executeUpdate(ku);
        Assert.assertEquals(1, count);
    }

    @Test
    public void deleteAnnotation() {

        AnnotationUserInfo ku = new AnnotationUserInfo();
        ku.setLoginName("liyd");
        Serializable id = (Serializable) daoTemplate.executeInsert(ku);

        int count = daoTemplate.executeDelete(AnnotationUserInfo.class, id);
        Assert.assertEquals(1, count);
    }


    @Test
    public void updateNative() {

        UserInfo user = new UserInfo();
        user.setLoginName("name-native");
        user.setPassword("123456-native");
        user.setUserAge(17);
        user.setGmtCreate(new Date());

        Long id = (Long) daoTemplate.executeInsert(user);
        daoTemplate.update(UserInfo.class)
                .set("{{userAge}}", "userAge+1")
                .where("userInfoId", id)
                .execute();

        UserInfo user2 = daoTemplate.get(UserInfo.class, id);
        Assert.assertEquals(18, (int) user2.getUserAge());
    }

    @Test
    public void namedParamHandler() {
        String sql = "insert into UserInfo(userInfoId,loginName,password,userAge) values(:userInfoId,:loginName,:password,:random_z10_z30)";

        Set<Integer> ages = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            daoTemplate.nativeExecutor()
                    .namedParameter()
                    .command(sql)
                    .parameter("userInfoId", 99L + i)
                    .parameter("loginName", "newName")
                    .parameter("password", "123456")
                    .parameter("random_z10_z30", new JdbcRandomNamedParamHandler())
                    .insert();

            UserInfo user = daoTemplate.get(UserInfo.class, 99L + i);
            ages.add(user.getUserAge());
            Assert.assertEquals(user.getLoginName(), "newName");
            Assert.assertTrue(user.getUserAge() >= 10 && user.getUserAge() <= 30);
        }
        Assert.assertTrue(ages.size() > 1);
    }


    @Test
    public void nativeExecutor() {
        int count = daoTemplate.nativeExecutor()
                .command("update UserInfo set loginName = ? where userInfoId = ?")
                .parameters(new Object[]{"newName", 39L})
                .update();
        Assert.assertEquals(1, count);
        UserInfo user = daoTemplate.get(UserInfo.class, 39L);
        Assert.assertEquals(user.getLoginName(), "newName");
    }

    @Test
    public void nativeExecutor1() {
        int count = daoTemplate.nativeExecutor()
                .namedParameter()
                .nativeCommand()
                .command("update User_Info set login_Name = :loginName where user_Info_Id = :userInfoId")
                .parameter("loginName", "newName")
                .parameter("userInfoId", 39L)
                .update();
        Assert.assertEquals(1, count);
        UserInfo user = daoTemplate.get(UserInfo.class, 39L);
        Assert.assertEquals(user.getLoginName(), "newName");
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

        String sql = "update User_Info set login_Name = :loginName where user_Info_Id in (:userInfoId) or user_info_id in (:userInfoId2)";

        int count = daoTemplate.nativeExecutor()
                .namedParameter()
                .nativeCommand()
                .command(sql)
                .parameters(params)
                .update();
        Assert.assertEquals(4, count);
        UserInfo user = daoTemplate.get(UserInfo.class, 39L);
        Assert.assertEquals(user.getLoginName(), "newName");
    }


    @Test
    public void nativeExecutor4() {
        int count = daoTemplate.nativeExecutor()
                .command("update UserInfo set loginName = ? where userInfoId = ?")
                .parameters("newName", 39L)
                .update();
        Assert.assertEquals(1, count);
        UserInfo user = daoTemplate.get(UserInfo.class, 39L);
        Assert.assertEquals(user.getLoginName(), "newName");
    }


    @Test
    public void nativeExecutor5() {

        long result = daoTemplate.nativeExecutor()
                .command("select count(*) from UserInfo")
                .count();
        Assert.assertEquals(50, result);
    }

    @Test
    public void nativeExecutor7() {

        daoTemplate.nativeExecutor()
                .command("update user_info set user_age = 18 where user_age < 18")
                .nativeCommand()
                .execute();

        long count = daoTemplate.selectFrom(UserInfo.class)
                .where("userAge", "<", 18)
                .count();
        Assert.assertEquals(0, count);
    }

    @Test
    public void nativeExecutor8() {

        daoTemplate.nativeExecutor()
                .command("update UserInfo set userAge = 18 where userAge < 18")
                .execute();

        long count = daoTemplate.selectFrom(UserInfo.class)
                .where("userAge", "<", 18)
                .count();
        Assert.assertEquals(0, count);
    }

    @Test
    public void nativeOneColResult() {
        Integer integer = daoTemplate.nativeExecutor()
                .command("select sum(userAge) from UserInfo")
                .oneColResult(Integer.class);
        Assert.assertTrue(integer > 0);
    }

    @Test
    public void nativeFirstResult() {
        UserInfo userInfo = daoTemplate.nativeExecutor()
                .command("select * from UserInfo order by userInfoId asc")
                .firstResult(UserInfo.class);

        Assert.assertEquals(1L, (long) userInfo.getUserInfoId());
    }

    @Test
    public void nativeOneColFirstResult() {
        Long id = daoTemplate.nativeExecutor()
                .command("select userInfoId from UserInfo order by userInfoId asc")
                .oneColFirstResult(Long.class);

        Assert.assertEquals(1L, (long) id);
    }

    @Test
    public void nativeInsert() {
        daoTemplate.nativeExecutor()
                .command("insert into UserInfo(userInfoId,loginName,password,userAge) values(?,?,?,?)")
                .parameters(100L, "100user", "123321", 18)
                .insert();

        UserInfo userInfo = daoTemplate.get(UserInfo.class, 100L);
        Assert.assertEquals(100L, (long) userInfo.getUserInfoId());
        Assert.assertEquals("100user", userInfo.getLoginName());
        Assert.assertEquals("123321", userInfo.getPassword());
        Assert.assertEquals(18, (int) userInfo.getUserAge());
    }

    @Test
    public void nativeInsert2() {
        Serializable id = daoTemplate.nativeExecutor()
                .command("insert into UserInfo(loginName,password,userAge) values(?,?,?)")
                .parameters("100user", "123321", 18)
                .insert(UserInfo.class);

        UserInfo userInfo = daoTemplate.get(UserInfo.class, id);
        Assert.assertEquals("100user", userInfo.getLoginName());
        Assert.assertEquals("123321", userInfo.getPassword());
        Assert.assertEquals(18, (int) userInfo.getUserAge());
    }

    @Test
    public void nativeList() {
        List<UserInfo> list = daoTemplate.nativeExecutor()
                .command("select * from UserInfo")
                .list(UserInfo.class);

        Assert.assertTrue(list != null && !list.isEmpty());
    }

    @Test
    public void nativeOneColList() {
        List<Long> list = daoTemplate.nativeExecutor()
                .command("select userInfoId from UserInfo")
                .oneColList(Long.class);

        Assert.assertTrue(list != null && !list.isEmpty());
    }

    @SuppressWarnings("CastCanBeRemovedNarrowingVariableType")
    @Test
    public void nativeFirstList() {
        Object result = daoTemplate.nativeExecutor()
                .command("select userInfoId from UserInfo")
                .firstResult();
        Assert.assertNotNull(result);
        Assert.assertEquals(1, ((Map<?, ?>) result).size());
    }

    @Test
    public void nativePage() {
        Pageable pageable = new UserInfo();
        pageable.setPageNum(2);
        pageable.setPageSize(10);
        Page<Map<String, Object>> page = daoTemplate.nativeExecutor()
                .command("select userInfoId from UserInfo order by userInfoId asc")
                .paginate(pageable)
                .pageResult();
        Assert.assertEquals(10, page.getList().size());
        Assert.assertNotNull(page.getList().get(0));
        Map<String, Object> map = page.getList().get(0);
        Assert.assertEquals(11L, map.get("USER_INFO_ID"));
        Map<String, Object> map2 = page.getList().get(9);
        Assert.assertEquals(20L, map2.get("USER_INFO_ID"));
    }

    @Test
    public void nativeResultHandler() {
        //这里只演示把结果转为UidUser类
        Page<Account> page = daoTemplate.nativeExecutor()
                .command("select * from UserInfo order by userInfoId asc")
                .paginate(2, 5)
                .resultHandler(new CustomResultHandler())
                .pageResult(Account.class);
        Assert.assertEquals(5, page.getList().size());
        Assert.assertEquals("name-6", page.getList().get(0).getLoginName());
        Assert.assertEquals("name-10", page.getList().get(4).getLoginName());
    }

    @Test
    public void nativeLimitPage() {
        Page<Map<String, Object>> page = daoTemplate.nativeExecutor()
                .command("select userInfoId from UserInfo order by userInfoId asc")
                .limit(15, 10)
                .pageResult();
        Assert.assertEquals(10, page.getList().size());
        Assert.assertNotNull(page.getList());
        Assert.assertNotNull(page.getList().get(0));
        Map<String, Object> map = page.getList().get(0);
        Assert.assertEquals(11L, map.get("USER_INFO_ID"));
        Map<String, Object> map2 = page.getList().get(9);
        Assert.assertEquals(20L, map2.get("USER_INFO_ID"));
    }

    @Test
    public void mybatisExecutor1() {
        UserInfo user = daoTemplate.myBatisExecutor()
                .command("getUser")
                .parameter("id", 9L)
                .parameter("loginName", "name-9")
                .singleResult(UserInfo.class);

        Assert.assertNotNull(user);
    }

    @Test
    public void mybatisExecutor2() {
        Map<String, Object> params = new HashMap<>();
        params.put("id", 9L);
        params.put("loginName", "name-9");

        UserInfo user = daoTemplate.myBatisExecutor()
                .command("getUserSql")
                .parameters(params)
                .nativeCommand()
                .singleResult(UserInfo.class);

        Assert.assertNotNull(user);
    }

    @Test
    public void mybatisExecutor3() {

        UserInfo userInfo = new UserInfo();
        userInfo.setUserInfoId(10L);
        userInfo.setLoginName("name-10");

        Object user = daoTemplate.myBatisExecutor()
                .command("getUser2")
                .parameter("user", userInfo)
                .nativeCommand()
                .singleResult();

        Assert.assertNotNull(user);
    }

    @Test
    public void mybatisExecutor4() {

        List<String> names = new ArrayList<>();
        names.add("name-8");
        names.add("name-9");
        names.add("name-10");

        List<?> list = daoTemplate.myBatisExecutor()
                .command("queryUserList")
                .parameter("names", names)
                .nativeCommand()
                .list();

        Assert.assertEquals(3, list.size());
    }

    @Test
    public void mybatisExecutor5() {

        int update = daoTemplate.myBatisExecutor()
                .command("updateUser")
                .parameter("loginName", "newName")
                .parameter("userInfoId", 9L)
                .nativeCommand()
                .update();

        Assert.assertEquals(1, update);
        UserInfo userInfo = daoTemplate.get(UserInfo.class, 9L);
        Assert.assertEquals("newName", userInfo.getLoginName());
    }


    @Test
    public void mybatisExecutor6() {
        Object user = daoTemplate.myBatisExecutor()
                .command("getUser3")
                .parameter("userInfoId", 9L)
                .parameter("loginName", "name-9")
                .singleResult();

        Assert.assertNotNull(user);
    }


    @Test
    public void mybatisExecutor7() {
        UserInfo userInfo = daoTemplate.myBatisExecutor()
                .command("getUser3")
                .parameter("userInfoId", 9L)
                .parameter("loginName", "name-9")
                .singleResult(UserInfo.class);

        Assert.assertEquals("name-9", userInfo.getLoginName());
    }

    @Test
    public void mybatisExecutor8() {
        Page<UserInfo> page = daoTemplate.myBatisExecutor()
                .command("queryUserList2")
                .paginate(1, 10)
                .pageResult(UserInfo.class);

        Assert.assertEquals(10, page.getList().size());
    }

    @Test
    public void lambdaSelect() {
        UserInfo userInfo = new UserInfo();
        userInfo.setLoginName("name-6");
        userInfo.setPassword("123456-6");
        Function<UserInfo, String> getLoginName = UserInfo::getLoginName;
        List<UserInfo> list = daoTemplate.selectFrom(UserInfo.class)
                .and(getLoginName, userInfo.getLoginName())
                .and(UserInfo::getPassword, userInfo.getPassword())
                .list(UserInfo.class);

        Assert.assertTrue(list.size() > 0);

    }

    @Test
    public void executeScript() throws Exception {

        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("test-script.sql");

        assert resourceAsStream != null;
        byte[] bytes = FileIOUtils.toByteArray(resourceAsStream);
        daoTemplate.nativeExecutor()
                .command(new String(bytes))
                .nativeCommand()
                .executeScript();
        resourceAsStream.close();

        long count = this.daoTemplate.nativeExecutor()
                .command("select count(*) from ss_user_message")
                .nativeCommand()
                .count();
        Assert.assertEquals(0, count);
    }

    @Test
    public void batchUpdate() {

        daoTemplate.executeDelete(UserInfo.class);
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

        String sql = "INSERT INTO USER_INFO (PASSWORD, LOGIN_NAME, GMT_CREATE, USER_AGE, USER_INFO_ID) VALUES (?, ?, ?, ?, ?)";
        final long begin = System.currentTimeMillis();
        daoTemplate.batchUpdate()
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
        Assert.assertEquals(userInfoList.size(), daoTemplate.findCount(UserInfo.class));

    }

    @Test
    public void batchUpdate1() {

        daoTemplate.executeDelete(UserInfo.class);
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

        daoTemplate.executeBatchUpdate(sql1, userInfoList, userInfoList.size(), (ps, names, userInfo) -> {
            ps.setString(1, userInfo.getPassword());
            ps.setString(2, userInfo.getLoginName());
            ps.setDate(3, new java.sql.Date(userInfo.getGmtCreate().getTime()));
            ps.setInt(4, userInfo.getUserAge());
            ps.setLong(5, userInfo.getUserInfoId());
        });

        final long end1 = System.currentTimeMillis();
        System.out.println("daoTemplate插入耗时:" + (end1 - begin1));
        Assert.assertEquals(userInfoList.size(), daoTemplate.findCount(UserInfo.class));

    }

    @Test
    public void batchUpdate2() {

        daoTemplate.executeDelete(UserInfo.class);
        String sql = "INSERT INTO USER_INFO (PASSWORD, LOGIN_NAME, GMT_CREATE, USER_AGE, USER_INFO_ID) VALUES (:PASSWORD, :LOGIN_NAME, :GMT_CREATE, :USER_AGE, :USER_INFO_ID)";
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
        daoTemplate.batchUpdate()
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

        long count = this.daoTemplate.findCount(UserInfo.class);
        Assert.assertTrue(count >= 10000);

        long count1 = this.daoTemplate.selectFrom(UserInfo.class)
                .where(UserInfo::getLoginName, "name-9999")
                .count();
        Assert.assertTrue(count1 > 0);

    }
}

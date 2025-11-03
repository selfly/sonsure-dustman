/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.common.bean;

import com.sonsure.dumper.common.bean.BeanKit;
import com.sonsure.dumper.common.model.Page;
import com.sonsure.dumper.common.model.Pagination;
import com.sonsure.dumper.test.basic.BaseTest;
import com.sonsure.dumper.test.model.UserInfo;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BeanKit 测试用例
 *
 * @author selfly
 */
public class BeanKitTest extends BaseTest {

    /**
     * 测试 Map 转 Bean 功能
     */
    @Test
    public void testMapToBean() {
        // 准备测试数据
        Map<String, Object> map = new HashMap<>();
        map.put("userInfoId", 1001L);
        map.put("loginName", "testuser");
        map.put("password", "password123");
        map.put("userAge", 25);
        map.put("userType", "ADMIN");
        map.put("status", "ACTIVE");
        map.put("gender", "MALE");
        map.put("realName", "测试用户");
        map.put("email", "test@example.com");
        map.put("mobile", "13800138000");
        map.put("avatar", "avatar.jpg");
        map.put("description", "测试用户描述");

        // 执行转换
        UserInfo userInfo = BeanKit.mapToBean(map, UserInfo.class);

        // 验证结果
        assertNotNull(userInfo, "转换结果不应为空");
        assertEquals(Long.valueOf(1001L), userInfo.getUserInfoId(), "用户ID应该匹配");
        assertEquals("testuser", userInfo.getLoginName(), "登录名应该匹配");
        assertEquals("password123", userInfo.getPassword(), "密码应该匹配");
        assertEquals(Integer.valueOf(25), userInfo.getUserAge(), "年龄应该匹配");
        assertEquals("ADMIN", userInfo.getUserType(), "用户类型应该匹配");
        assertEquals("ACTIVE", userInfo.getStatus(), "状态应该匹配");
        assertEquals("MALE", userInfo.getGender(), "性别应该匹配");
        assertEquals("测试用户", userInfo.getRealName(), "真实姓名应该匹配");
        assertEquals("test@example.com", userInfo.getEmail(), "邮箱应该匹配");
        assertEquals("13800138000", userInfo.getMobile(), "手机号应该匹配");
        assertEquals("avatar.jpg", userInfo.getAvatar(), "头像应该匹配");
        assertEquals("测试用户描述", userInfo.getDescription(), "描述应该匹配");
    }

    /**
     * 测试下划线命名 Map 转 Bean 功能
     */
    @Test
    public void testUnderlineKeyMapToBean() {
        // 准备测试数据（下划线命名）
        Map<String, Object> map = new HashMap<>();
        map.put("user_info_id", 1002L);
        map.put("login_name", "underlineuser");
        map.put("user_age", 30);
        map.put("user_type", "USER");
        map.put("real_name", "下划线用户");
        map.put("gmt_create", new Date());
        map.put("gmt_modify", new Date());

        // 执行转换
        UserInfo userInfo = BeanKit.underlineKeyMapToBean(map, UserInfo.class);

        // 验证结果
        assertNotNull(userInfo, "转换结果不应为空");
        assertEquals(Long.valueOf(1002L), userInfo.getUserInfoId(), "用户ID应该匹配");
        assertEquals("underlineuser", userInfo.getLoginName(), "登录名应该匹配");
        assertEquals(Integer.valueOf(30), userInfo.getUserAge(), "年龄应该匹配");
        assertEquals("USER", userInfo.getUserType(), "用户类型应该匹配");
        assertEquals("下划线用户", userInfo.getRealName(), "真实姓名应该匹配");
        assertNotNull(userInfo.getGmtCreate(), "创建时间不应为空");
        assertNotNull(userInfo.getGmtModify(), "修改时间不应为空");
    }

    /**
     * 测试 Map 列表转 Bean 列表功能
     */
    @Test
    public void testMapListToBeanList() {
        // 准备测试数据
        List<Map<String, Object>> mapList = new ArrayList<>();

        Map<String, Object> map1 = new HashMap<>();
        map1.put("userInfoId", 1003L);
        map1.put("loginName", "user1");
        map1.put("userAge", 20);
        mapList.add(map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("userInfoId", 1004L);
        map2.put("loginName", "user2");
        map2.put("userAge", 35);
        mapList.add(map2);

        // 执行转换
        List<UserInfo> userInfoList = BeanKit.mapToBean(mapList, UserInfo.class);

        // 验证结果
        assertNotNull(userInfoList, "转换结果不应为空");
        assertEquals(2, userInfoList.size(), "列表大小应该匹配");

        UserInfo user1 = userInfoList.get(0);
        assertEquals(Long.valueOf(1003L), user1.getUserInfoId(), "第一个用户ID应该匹配");
        assertEquals("user1", user1.getLoginName(), "第一个用户登录名应该匹配");
        assertEquals(Integer.valueOf(20), user1.getUserAge(), "第一个用户年龄应该匹配");

        UserInfo user2 = userInfoList.get(1);
        assertEquals(Long.valueOf(1004L), user2.getUserInfoId(), "第二个用户ID应该匹配");
        assertEquals("user2", user2.getLoginName(), "第二个用户登录名应该匹配");
        assertEquals(Integer.valueOf(35), user2.getUserAge(), "第二个用户年龄应该匹配");
    }

    /**
     * 测试 Bean 转 Map 功能
     */
    @Test
    public void testBeanToMap() {
        // 准备测试数据
        UserInfo userInfo = new UserInfo();
        userInfo.setUserInfoId(1005L);
        userInfo.setLoginName("maptest");
        userInfo.setPassword("password456");
        userInfo.setUserAge(28);
        userInfo.setUserType("MANAGER");
        userInfo.setStatus("INACTIVE");
        userInfo.setGender("FEMALE");
        userInfo.setRealName("Map测试用户");
        userInfo.setEmail("maptest@example.com");
        userInfo.setMobile("13900139000");
        userInfo.setAvatar("mapavatar.jpg");
        userInfo.setDescription("Map测试用户描述");
        userInfo.setGmtCreate(new Date());
        userInfo.setGmtModify(new Date());

        // 执行转换
        Map<String, Object> map = BeanKit.beanToMap(userInfo);

        // 验证结果
        assertNotNull(map, "转换结果不应为空");
        assertEquals(1005L, map.get("userInfoId"), "用户ID应该匹配");
        assertEquals("maptest", map.get("loginName"), "登录名应该匹配");
        assertEquals("password456", map.get("password"), "密码应该匹配");
        assertEquals(28, map.get("userAge"), "年龄应该匹配");
        assertEquals("MANAGER", map.get("userType"), "用户类型应该匹配");
        assertEquals("INACTIVE", map.get("status"), "状态应该匹配");
        assertEquals("FEMALE", map.get("gender"), "性别应该匹配");
        assertEquals("Map测试用户", map.get("realName"), "真实姓名应该匹配");
        assertEquals("maptest@example.com", map.get("email"), "邮箱应该匹配");
        assertEquals("13900139000", map.get("mobile"), "手机号应该匹配");
        assertEquals("mapavatar.jpg", map.get("avatar"), "头像应该匹配");
        assertEquals("Map测试用户描述", map.get("description"), "描述应该匹配");
        assertNotNull(map.get("gmtCreate"), "创建时间不应为空");
        assertNotNull(map.get("gmtModify"), "修改时间不应为空");
    }

    /**
     * 测试对象属性拷贝功能
     */
    @Test
    public void testCopyProperties() {
        // 准备源对象
        UserInfo source = new UserInfo();
        source.setUserInfoId(1006L);
        source.setLoginName("copytest");
        source.setPassword("password789");
        source.setUserAge(32);
        source.setUserType("SUPER_ADMIN");
        source.setStatus("ACTIVE");
        source.setGender("MALE");
        source.setRealName("拷贝测试用户");
        source.setEmail("copytest@example.com");
        source.setMobile("13700137000");
        source.setAvatar("copyavatar.jpg");
        source.setDescription("拷贝测试用户描述");
        source.setGmtCreate(new Date());
        source.setGmtModify(new Date());

        // 准备目标对象
        UserInfo target = new UserInfo();

        // 执行拷贝
        UserInfo result = BeanKit.copyProperties(target, source);

        // 验证结果
        assertNotNull(result, "拷贝结果不应为空");
        assertEquals(Long.valueOf(1006L), result.getUserInfoId(), "用户ID应该匹配");
        assertEquals("copytest", result.getLoginName(), "登录名应该匹配");
        assertEquals("password789", result.getPassword(), "密码应该匹配");
        assertEquals(Integer.valueOf(32), result.getUserAge(), "年龄应该匹配");
        assertEquals("SUPER_ADMIN", result.getUserType(), "用户类型应该匹配");
        assertEquals("ACTIVE", result.getStatus(), "状态应该匹配");
        assertEquals("MALE", result.getGender(), "性别应该匹配");
        assertEquals("拷贝测试用户", result.getRealName(), "真实姓名应该匹配");
        assertEquals("copytest@example.com", result.getEmail(), "邮箱应该匹配");
        assertEquals("13700137000", result.getMobile(), "手机号应该匹配");
        assertEquals("copyavatar.jpg", result.getAvatar(), "头像应该匹配");
        assertEquals("拷贝测试用户描述", result.getDescription(), "描述应该匹配");
        assertNotNull(result.getGmtCreate(), "创建时间不应为空");
        assertNotNull(result.getGmtModify(), "修改时间不应为空");
    }

    /**
     * 测试带忽略属性的对象拷贝功能
     */
    @Test
    public void testCopyPropertiesWithIgnoreProperties() {
        // 准备源对象
        UserInfo source = new UserInfo();
        source.setUserInfoId(1007L);
        source.setLoginName("ignoretest");
        source.setPassword("password999");
        source.setUserAge(40);
        source.setUserType("GUEST");
        source.setStatus("PENDING");
        source.setGender("FEMALE");
        source.setRealName("忽略测试用户");
        source.setEmail("ignoretest@example.com");
        source.setMobile("13600136000");
        source.setAvatar("ignoreavatar.jpg");
        source.setDescription("忽略测试用户描述");

        // 准备目标对象
        UserInfo target = new UserInfo();

        // 执行拷贝（忽略密码和邮箱）
        String[] ignoreProperties = {"password", "email"};
        UserInfo result = BeanKit.copyProperties(target, source, ignoreProperties);

        // 验证结果
        assertNotNull(result, "拷贝结果不应为空");
        assertEquals(Long.valueOf(1007L), result.getUserInfoId(), "用户ID应该匹配");
        assertEquals("ignoretest", result.getLoginName(), "登录名应该匹配");
        assertNull(result.getPassword(), "密码应该被忽略");
        assertEquals(Integer.valueOf(40), result.getUserAge(), "年龄应该匹配");
        assertEquals("GUEST", result.getUserType(), "用户类型应该匹配");
        assertEquals("PENDING", result.getStatus(), "状态应该匹配");
        assertEquals("FEMALE", result.getGender(), "性别应该匹配");
        assertEquals("忽略测试用户", result.getRealName(), "真实姓名应该匹配");
        assertNull(result.getEmail(), "邮箱应该被忽略");
        assertEquals("13600136000", result.getMobile(), "手机号应该匹配");
        assertEquals("ignoreavatar.jpg", result.getAvatar(), "头像应该匹配");
        assertEquals("忽略测试用户描述", result.getDescription(), "描述应该匹配");
    }

    /**
     * 测试对象列表属性拷贝功能
     */
    @Test
    public void testCopyPropertiesList() {
        // 准备源对象列表
        List<UserInfo> sourceList = new ArrayList<>();

        UserInfo user1 = new UserInfo();
        user1.setUserInfoId(1008L);
        user1.setLoginName("listuser1");
        user1.setUserAge(22);
        user1.setUserType("USER");
        sourceList.add(user1);

        UserInfo user2 = new UserInfo();
        user2.setUserInfoId(1009L);
        user2.setLoginName("listuser2");
        user2.setUserAge(33);
        user2.setUserType("ADMIN");
        sourceList.add(user2);

        // 执行拷贝
        List<UserInfo> resultList = BeanKit.copyProperties(UserInfo.class, sourceList);

        // 验证结果
        assertNotNull(resultList, "拷贝结果不应为空");
        assertEquals(2, resultList.size(), "列表大小应该匹配");

        UserInfo result1 = resultList.get(0);
        assertEquals(Long.valueOf(1008L), result1.getUserInfoId(), "第一个用户ID应该匹配");
        assertEquals("listuser1", result1.getLoginName(), "第一个用户登录名应该匹配");
        assertEquals(Integer.valueOf(22), result1.getUserAge(), "第一个用户年龄应该匹配");
        assertEquals("USER", result1.getUserType(), "第一个用户类型应该匹配");

        UserInfo result2 = resultList.get(1);
        assertEquals(Long.valueOf(1009L), result2.getUserInfoId(), "第二个用户ID应该匹配");
        assertEquals("listuser2", result2.getLoginName(), "第二个用户登录名应该匹配");
        assertEquals(Integer.valueOf(33), result2.getUserAge(), "第二个用户年龄应该匹配");
        assertEquals("ADMIN", result2.getUserType(), "第二个用户类型应该匹配");
    }

    /**
     * 测试分页对象拷贝功能
     */
    @Test
    public void testCopyPage() {
        // 准备源分页对象
        List<UserInfo> sourceList = new ArrayList<>();

        UserInfo user1 = new UserInfo();
        user1.setUserInfoId(1010L);
        user1.setLoginName("pageuser1");
        user1.setUserAge(25);
        user1.setUserType("USER");
        sourceList.add(user1);

        UserInfo user2 = new UserInfo();
        user2.setUserInfoId(1011L);
        user2.setLoginName("pageuser2");
        user2.setUserAge(35);
        user2.setUserType("ADMIN");
        sourceList.add(user2);

        Pagination pagination = new Pagination(20, 100);
        Page<UserInfo> sourcePage = new Page<>(sourceList, pagination);

        // 执行拷贝
        Page<UserInfo> resultPage = BeanKit.copyPage(UserInfo.class, sourcePage);

        // 验证结果
        assertNotNull(resultPage, "拷贝结果不应为空");
        assertNotNull(resultPage.getPagination(), "分页信息不应为空");
        assertNotNull(resultPage.getList(), "数据列表不应为空");
        assertEquals(2, resultPage.getList().size(), "列表大小应该匹配");
        assertEquals(20, resultPage.getPagination().getPageSize(), "分页大小应该匹配");
        assertEquals(100, resultPage.getPagination().getTotalItems(), "总记录数应该匹配");

        UserInfo result1 = resultPage.getList().get(0);
        assertEquals(Long.valueOf(1010L), result1.getUserInfoId(), "第一个用户ID应该匹配");
        assertEquals("pageuser1", result1.getLoginName(), "第一个用户登录名应该匹配");
        assertEquals(Integer.valueOf(25), result1.getUserAge(), "第一个用户年龄应该匹配");
        assertEquals("USER", result1.getUserType(), "第一个用户类型应该匹配");

        UserInfo result2 = resultPage.getList().get(1);
        assertEquals(Long.valueOf(1011L), result2.getUserInfoId(), "第二个用户ID应该匹配");
        assertEquals("pageuser2", result2.getLoginName(), "第二个用户登录名应该匹配");
        assertEquals(Integer.valueOf(35), result2.getUserAge(), "第二个用户年龄应该匹配");
        assertEquals("ADMIN", result2.getUserType(), "第二个用户类型应该匹配");
    }

    /**
     * 测试字段拷贝功能
     */
    @Test
    public void testCopyFields() {
        // 准备源对象
        UserInfo source = new UserInfo();
        source.setUserInfoId(1012L);
        source.setLoginName("fieldtest");
        source.setPassword("password123");
        source.setUserAge(45);
        source.setUserType("MANAGER");
        source.setStatus("ACTIVE");
        source.setGender("MALE");
        source.setRealName("字段测试用户");
        source.setEmail("fieldtest@example.com");
        source.setMobile("13500135000");
        source.setAvatar("fieldavatar.jpg");
        source.setDescription("字段测试用户描述");

        // 准备目标对象
        UserInfo target = new UserInfo();

        // 执行字段拷贝
        BeanKit.copyFields(source, target);

        // 验证结果
        assertEquals(Long.valueOf(1012L), target.getUserInfoId(), "用户ID应该匹配");
        assertEquals("fieldtest", target.getLoginName(), "登录名应该匹配");
        assertEquals("password123", target.getPassword(), "密码应该匹配");
        assertEquals(Integer.valueOf(45), target.getUserAge(), "年龄应该匹配");
        assertEquals("MANAGER", target.getUserType(), "用户类型应该匹配");
        assertEquals("ACTIVE", target.getStatus(), "状态应该匹配");
        assertEquals("MALE", target.getGender(), "性别应该匹配");
        assertEquals("字段测试用户", target.getRealName(), "真实姓名应该匹配");
        assertEquals("fieldtest@example.com", target.getEmail(), "邮箱应该匹配");
        assertEquals("13500135000", target.getMobile(), "手机号应该匹配");
        assertEquals("fieldavatar.jpg", target.getAvatar(), "头像应该匹配");
        assertEquals("字段测试用户描述", target.getDescription(), "描述应该匹配");
    }

    /**
     * 测试空值处理
     */
    @Test
    public void testNullHandling() {
        // 测试空 Map 转 Bean
        Map<String, Object> emptyMap = new HashMap<>();
        UserInfo userInfo1 = BeanKit.mapToBean(emptyMap, UserInfo.class);
        assertNotNull(userInfo1, "空Map转换结果不应为空");

        // 测试空列表转 Bean 列表
        List<Map<String, Object>> emptyList = new ArrayList<>();
        List<UserInfo> userInfoList = BeanKit.mapToBean(emptyList, UserInfo.class);
        assertNotNull(userInfoList, "空列表转换结果不应为空");
        assertEquals(0, userInfoList.size(), "空列表大小应该为0");

        // 测试空对象转 Map
        UserInfo nullUserInfo = null;
        Map<String, Object> map = BeanKit.beanToMap(nullUserInfo);
        assertTrue(map.isEmpty());
    }

    /**
     * 测试带 Consumer 的转换功能
     */
    @Test
    public void testMapToBeanWithConsumer() {
        // 准备测试数据
        Map<String, Object> map = new HashMap<>();
        map.put("userInfoId", 1013L);
        map.put("loginName", "consumertest");
        map.put("userAge", 50);
        map.put("userType", "USER");

        // 执行转换并添加额外处理
        UserInfo userInfo = BeanKit.mapToBean(map, UserInfo.class, (sourceMap, targetBean) -> {
            // 在转换后添加额外处理
            targetBean.setStatus("PROCESSED");
            targetBean.setDescription("通过Consumer处理的用户");
        });

        // 验证结果
        assertNotNull(userInfo, "转换结果不应为空");
        assertEquals(Long.valueOf(1013L), userInfo.getUserInfoId(), "用户ID应该匹配");
        assertEquals("consumertest", userInfo.getLoginName(), "登录名应该匹配");
        assertEquals(Integer.valueOf(50), userInfo.getUserAge(), "年龄应该匹配");
        assertEquals("USER", userInfo.getUserType(), "用户类型应该匹配");
        assertEquals("PROCESSED", userInfo.getStatus(), "状态应该被Consumer设置");
        assertEquals("通过Consumer处理的用户", userInfo.getDescription(), "描述应该被Consumer设置");
    }

    /**
     * 测试带分隔符的 Map 转 Bean 功能
     */
    @Test
    public void testMapToBeanWithDelimiter() {
        // 准备测试数据（使用自定义分隔符）
        Map<String, Object> map = new HashMap<>();
        map.put("user-info-id", 1014L);
        map.put("login-name", "delimitertest");
        map.put("user-age", 27);
        map.put("user-type", "MANAGER");

        // 执行转换（使用连字符作为分隔符）
        UserInfo userInfo = BeanKit.mapToBean(map, UserInfo.class, '-');

        // 验证结果
        assertNotNull(userInfo, "转换结果不应为空");
        assertEquals(Long.valueOf(1014L), userInfo.getUserInfoId(), "用户ID应该匹配");
        assertEquals("delimitertest", userInfo.getLoginName(), "登录名应该匹配");
        assertEquals(Integer.valueOf(27), userInfo.getUserAge(), "年龄应该匹配");
        assertEquals("MANAGER", userInfo.getUserType(), "用户类型应该匹配");
    }

    /**
     * 测试带分隔符和 Consumer 的 Map 转 Bean 功能
     */
    @Test
    public void testMapToBeanWithDelimiterAndConsumer() {
        // 准备测试数据（使用自定义分隔符）
        Map<String, Object> map = new HashMap<>();
        map.put("user-info-id", 1015L);
        map.put("login-name", "delimiterconsumertest");
        map.put("user-age", 29);
        map.put("user-type", "ADMIN");

        // 执行转换（使用连字符作为分隔符，并添加Consumer处理）
        UserInfo userInfo = BeanKit.mapToBean(map, UserInfo.class, '-', (sourceMap, targetBean) -> {
            targetBean.setStatus("DELIMITER_PROCESSED");
            targetBean.setDescription("通过分隔符和Consumer处理的用户");
        });

        // 验证结果
        assertNotNull(userInfo, "转换结果不应为空");
        assertEquals(Long.valueOf(1015L), userInfo.getUserInfoId(), "用户ID应该匹配");
        assertEquals("delimiterconsumertest", userInfo.getLoginName(), "登录名应该匹配");
        assertEquals(Integer.valueOf(29), userInfo.getUserAge(), "年龄应该匹配");
        assertEquals("ADMIN", userInfo.getUserType(), "用户类型应该匹配");
        assertEquals("DELIMITER_PROCESSED", userInfo.getStatus(), "状态应该被Consumer设置");
        assertEquals("通过分隔符和Consumer处理的用户", userInfo.getDescription(), "描述应该被Consumer设置");
    }

    /**
     * 测试带 Consumer 的下划线命名 Map 转 Bean 功能
     */
    @Test
    public void testUnderlineKeyMapToBeanWithConsumer() {
        // 准备测试数据（下划线命名）
        Map<String, Object> map = new HashMap<>();
        map.put("user_info_id", 1016L);
        map.put("login_name", "underlineconsumertest");
        map.put("user_age", 31);
        map.put("user_type", "SUPER_ADMIN");

        // 执行转换并添加额外处理
        UserInfo userInfo = BeanKit.underlineKeyMapToBean(map, UserInfo.class, (sourceMap, targetBean) -> {
            targetBean.setStatus("UNDERLINE_PROCESSED");
            targetBean.setDescription("通过下划线命名和Consumer处理的用户");
        });

        // 验证结果
        assertNotNull(userInfo, "转换结果不应为空");
        assertEquals(Long.valueOf(1016L), userInfo.getUserInfoId(), "用户ID应该匹配");
        assertEquals("underlineconsumertest", userInfo.getLoginName(), "登录名应该匹配");
        assertEquals(Integer.valueOf(31), userInfo.getUserAge(), "年龄应该匹配");
        assertEquals("SUPER_ADMIN", userInfo.getUserType(), "用户类型应该匹配");
        assertEquals("UNDERLINE_PROCESSED", userInfo.getStatus(), "状态应该被Consumer设置");
        assertEquals("通过下划线命名和Consumer处理的用户", userInfo.getDescription(), "描述应该被Consumer设置");
    }

    /**
     * 测试带 Consumer 的下划线命名 Map 列表转 Bean 列表功能
     */
    @Test
    public void testUnderlineKeyMapListToBeanListWithConsumer() {
        // 准备测试数据（下划线命名）
        List<Map<String, Object>> mapList = new ArrayList<>();
        
        Map<String, Object> map1 = new HashMap<>();
        map1.put("user_info_id", 1017L);
        map1.put("login_name", "underlineuser1");
        map1.put("user_age", 23);
        mapList.add(map1);
        
        Map<String, Object> map2 = new HashMap<>();
        map2.put("user_info_id", 1018L);
        map2.put("login_name", "underlineuser2");
        map2.put("user_age", 34);
        mapList.add(map2);

        // 执行转换并添加额外处理
        List<UserInfo> userInfoList = BeanKit.underlineKeyMapToBean(mapList, UserInfo.class, (sourceMap, targetBean) -> {
            targetBean.setStatus("UNDERLINE_LIST_PROCESSED");
            targetBean.setDescription("通过下划线命名列表和Consumer处理的用户");
        });

        // 验证结果
        assertNotNull(userInfoList, "转换结果不应为空");
        assertEquals(2, userInfoList.size(), "列表大小应该匹配");
        
        UserInfo user1 = userInfoList.get(0);
        assertEquals(Long.valueOf(1017L), user1.getUserInfoId(), "第一个用户ID应该匹配");
        assertEquals("underlineuser1", user1.getLoginName(), "第一个用户登录名应该匹配");
        assertEquals(Integer.valueOf(23), user1.getUserAge(), "第一个用户年龄应该匹配");
        assertEquals("UNDERLINE_LIST_PROCESSED", user1.getStatus(), "第一个用户状态应该被Consumer设置");
        assertEquals("通过下划线命名列表和Consumer处理的用户", user1.getDescription(), "第一个用户描述应该被Consumer设置");
        
        UserInfo user2 = userInfoList.get(1);
        assertEquals(Long.valueOf(1018L), user2.getUserInfoId(), "第二个用户ID应该匹配");
        assertEquals("underlineuser2", user2.getLoginName(), "第二个用户登录名应该匹配");
        assertEquals(Integer.valueOf(34), user2.getUserAge(), "第二个用户年龄应该匹配");
        assertEquals("UNDERLINE_LIST_PROCESSED", user2.getStatus(), "第二个用户状态应该被Consumer设置");
        assertEquals("通过下划线命名列表和Consumer处理的用户", user2.getDescription(), "第二个用户描述应该被Consumer设置");
    }

    /**
     * 测试带 Consumer 的 Map 列表转 Bean 列表功能
     */
    @Test
    public void testMapListToBeanListWithConsumer() {
        // 准备测试数据
        List<Map<String, Object>> mapList = new ArrayList<>();
        
        Map<String, Object> map1 = new HashMap<>();
        map1.put("userInfoId", 1019L);
        map1.put("loginName", "consumeruser1");
        map1.put("userAge", 26);
        mapList.add(map1);
        
        Map<String, Object> map2 = new HashMap<>();
        map2.put("userInfoId", 1020L);
        map2.put("loginName", "consumeruser2");
        map2.put("userAge", 37);
        mapList.add(map2);

        // 执行转换并添加额外处理
        List<UserInfo> userInfoList = BeanKit.mapToBean(mapList, UserInfo.class, (sourceMap, targetBean) -> {
            targetBean.setStatus("LIST_PROCESSED");
            targetBean.setDescription("通过列表和Consumer处理的用户");
        });

        // 验证结果
        assertNotNull(userInfoList, "转换结果不应为空");
        assertEquals(2, userInfoList.size(), "列表大小应该匹配");
        
        UserInfo user1 = userInfoList.get(0);
        assertEquals(Long.valueOf(1019L), user1.getUserInfoId(), "第一个用户ID应该匹配");
        assertEquals("consumeruser1", user1.getLoginName(), "第一个用户登录名应该匹配");
        assertEquals(Integer.valueOf(26), user1.getUserAge(), "第一个用户年龄应该匹配");
        assertEquals("LIST_PROCESSED", user1.getStatus(), "第一个用户状态应该被Consumer设置");
        assertEquals("通过列表和Consumer处理的用户", user1.getDescription(), "第一个用户描述应该被Consumer设置");
        
        UserInfo user2 = userInfoList.get(1);
        assertEquals(Long.valueOf(1020L), user2.getUserInfoId(), "第二个用户ID应该匹配");
        assertEquals("consumeruser2", user2.getLoginName(), "第二个用户登录名应该匹配");
        assertEquals(Integer.valueOf(37), user2.getUserAge(), "第二个用户年龄应该匹配");
        assertEquals("LIST_PROCESSED", user2.getStatus(), "第二个用户状态应该被Consumer设置");
        assertEquals("通过列表和Consumer处理的用户", user2.getDescription(), "第二个用户描述应该被Consumer设置");
    }

    /**
     * 测试带分隔符的 Map 列表转 Bean 列表功能
     */
    @Test
    public void testMapListToBeanListWithDelimiter() {
        // 准备测试数据（使用自定义分隔符）
        List<Map<String, Object>> mapList = new ArrayList<>();
        
        Map<String, Object> map1 = new HashMap<>();
        map1.put("user-info-id", 1021L);
        map1.put("login-name", "delimiteruser1");
        map1.put("user-age", 28);
        mapList.add(map1);
        
        Map<String, Object> map2 = new HashMap<>();
        map2.put("user-info-id", 1022L);
        map2.put("login-name", "delimiteruser2");
        map2.put("user-age", 39);
        mapList.add(map2);

        // 执行转换（使用连字符作为分隔符）
        List<UserInfo> userInfoList = BeanKit.mapToBean(mapList, UserInfo.class, '-');

        // 验证结果
        assertNotNull(userInfoList, "转换结果不应为空");
        assertEquals(2, userInfoList.size(), "列表大小应该匹配");
        
        UserInfo user1 = userInfoList.get(0);
        assertEquals(Long.valueOf(1021L), user1.getUserInfoId(), "第一个用户ID应该匹配");
        assertEquals("delimiteruser1", user1.getLoginName(), "第一个用户登录名应该匹配");
        assertEquals(Integer.valueOf(28), user1.getUserAge(), "第一个用户年龄应该匹配");
        
        UserInfo user2 = userInfoList.get(1);
        assertEquals(Long.valueOf(1022L), user2.getUserInfoId(), "第二个用户ID应该匹配");
        assertEquals("delimiteruser2", user2.getLoginName(), "第二个用户登录名应该匹配");
        assertEquals(Integer.valueOf(39), user2.getUserAge(), "第二个用户年龄应该匹配");
    }

    /**
     * 测试带分隔符和 Consumer 的 Map 列表转 Bean 列表功能
     */
    @Test
    public void testMapListToBeanListWithDelimiterAndConsumer() {
        // 准备测试数据（使用自定义分隔符）
        List<Map<String, Object>> mapList = new ArrayList<>();
        
        Map<String, Object> map1 = new HashMap<>();
        map1.put("user-info-id", 1023L);
        map1.put("login-name", "delimiterconsumeruser1");
        map1.put("user-age", 30);
        mapList.add(map1);
        
        Map<String, Object> map2 = new HashMap<>();
        map2.put("user-info-id", 1024L);
        map2.put("login-name", "delimiterconsumeruser2");
        map2.put("user-age", 41);
        mapList.add(map2);

        // 执行转换（使用连字符作为分隔符，并添加Consumer处理）
        List<UserInfo> userInfoList = BeanKit.mapToBean(mapList, UserInfo.class, '-', (sourceMap, targetBean) -> {
            targetBean.setStatus("DELIMITER_LIST_PROCESSED");
            targetBean.setDescription("通过分隔符列表和Consumer处理的用户");
        });

        // 验证结果
        assertNotNull(userInfoList, "转换结果不应为空");
        assertEquals(2, userInfoList.size(), "列表大小应该匹配");
        
        UserInfo user1 = userInfoList.get(0);
        assertEquals(Long.valueOf(1023L), user1.getUserInfoId(), "第一个用户ID应该匹配");
        assertEquals("delimiterconsumeruser1", user1.getLoginName(), "第一个用户登录名应该匹配");
        assertEquals(Integer.valueOf(30), user1.getUserAge(), "第一个用户年龄应该匹配");
        assertEquals("DELIMITER_LIST_PROCESSED", user1.getStatus(), "第一个用户状态应该被Consumer设置");
        assertEquals("通过分隔符列表和Consumer处理的用户", user1.getDescription(), "第一个用户描述应该被Consumer设置");
        
        UserInfo user2 = userInfoList.get(1);
        assertEquals(Long.valueOf(1024L), user2.getUserInfoId(), "第二个用户ID应该匹配");
        assertEquals("delimiterconsumeruser2", user2.getLoginName(), "第二个用户登录名应该匹配");
        assertEquals(Integer.valueOf(41), user2.getUserAge(), "第二个用户年龄应该匹配");
        assertEquals("DELIMITER_LIST_PROCESSED", user2.getStatus(), "第二个用户状态应该被Consumer设置");
        assertEquals("通过分隔符列表和Consumer处理的用户", user2.getDescription(), "第二个用户描述应该被Consumer设置");
    }

    /**
     * 测试带 Consumer 的对象属性拷贝功能
     */
    @Test
    public void testCopyPropertiesWithConsumer() {
        // 准备源对象
        UserInfo source = new UserInfo();
        source.setUserInfoId(1025L);
        source.setLoginName("consumerproptest");
        source.setPassword("password123");
        source.setUserAge(42);
        source.setUserType("MANAGER");
        source.setStatus("ACTIVE");
        source.setGender("FEMALE");
        source.setRealName("Consumer属性测试用户");
        source.setEmail("consumerproptest@example.com");
        source.setMobile("13400134000");
        source.setAvatar("consumerpropavatar.jpg");
        source.setDescription("Consumer属性测试用户描述");

        // 准备目标对象
        UserInfo target = new UserInfo();

        // 执行拷贝并添加额外处理
        UserInfo result = BeanKit.copyProperties(target, source, (sourceObj, targetObj) -> {
            targetObj.setStatus("CONSUMER_PROCESSED");
            targetObj.setDescription("通过Consumer处理的属性拷贝");
        });

        // 验证结果
        assertNotNull(result, "拷贝结果不应为空");
        assertEquals(Long.valueOf(1025L), result.getUserInfoId(), "用户ID应该匹配");
        assertEquals("consumerproptest", result.getLoginName(), "登录名应该匹配");
        assertEquals("password123", result.getPassword(), "密码应该匹配");
        assertEquals(Integer.valueOf(42), result.getUserAge(), "年龄应该匹配");
        assertEquals("MANAGER", result.getUserType(), "用户类型应该匹配");
        assertEquals("CONSUMER_PROCESSED", result.getStatus(), "状态应该被Consumer设置");
        assertEquals("FEMALE", result.getGender(), "性别应该匹配");
        assertEquals("Consumer属性测试用户", result.getRealName(), "真实姓名应该匹配");
        assertEquals("consumerproptest@example.com", result.getEmail(), "邮箱应该匹配");
        assertEquals("13400134000", result.getMobile(), "手机号应该匹配");
        assertEquals("consumerpropavatar.jpg", result.getAvatar(), "头像应该匹配");
        assertEquals("通过Consumer处理的属性拷贝", result.getDescription(), "描述应该被Consumer设置");
    }

    /**
     * 测试带忽略属性和 Consumer 的对象属性拷贝功能
     */
    @Test
    public void testCopyPropertiesWithIgnorePropertiesAndConsumer() {
        // 准备源对象
        UserInfo source = new UserInfo();
        source.setUserInfoId(1026L);
        source.setLoginName("ignoreconsumerproptest");
        source.setPassword("password456");
        source.setUserAge(43);
        source.setUserType("ADMIN");
        source.setStatus("INACTIVE");
        source.setGender("MALE");
        source.setRealName("忽略Consumer属性测试用户");
        source.setEmail("ignoreconsumerproptest@example.com");
        source.setMobile("13300133000");
        source.setAvatar("ignoreconsumerpropavatar.jpg");
        source.setDescription("忽略Consumer属性测试用户描述");

        // 准备目标对象
        UserInfo target = new UserInfo();

        // 执行拷贝（忽略密码和邮箱，并添加Consumer处理）
        String[] ignoreProperties = {"password", "email"};
        UserInfo result = BeanKit.copyProperties(target, source, ignoreProperties, (sourceObj, targetObj) -> {
            targetObj.setStatus("IGNORE_CONSUMER_PROCESSED");
            targetObj.setDescription("通过忽略属性和Consumer处理的属性拷贝");
        });

        // 验证结果
        assertNotNull(result, "拷贝结果不应为空");
        assertEquals(Long.valueOf(1026L), result.getUserInfoId(), "用户ID应该匹配");
        assertEquals("ignoreconsumerproptest", result.getLoginName(), "登录名应该匹配");
        assertNull(result.getPassword(), "密码应该被忽略");
        assertEquals(Integer.valueOf(43), result.getUserAge(), "年龄应该匹配");
        assertEquals("ADMIN", result.getUserType(), "用户类型应该匹配");
        assertEquals("IGNORE_CONSUMER_PROCESSED", result.getStatus(), "状态应该被Consumer设置");
        assertEquals("MALE", result.getGender(), "性别应该匹配");
        assertEquals("忽略Consumer属性测试用户", result.getRealName(), "真实姓名应该匹配");
        assertNull(result.getEmail(), "邮箱应该被忽略");
        assertEquals("13300133000", result.getMobile(), "手机号应该匹配");
        assertEquals("ignoreconsumerpropavatar.jpg", result.getAvatar(), "头像应该匹配");
        assertEquals("通过忽略属性和Consumer处理的属性拷贝", result.getDescription(), "描述应该被Consumer设置");
    }

    /**
     * 测试带 Consumer 的对象列表属性拷贝功能
     */
    @Test
    public void testCopyPropertiesListWithConsumer() {
        // 准备源对象列表
        List<UserInfo> sourceList = new ArrayList<>();
        
        UserInfo user1 = new UserInfo();
        user1.setUserInfoId(1027L);
        user1.setLoginName("consumerlistuser1");
        user1.setUserAge(24);
        user1.setUserType("USER");
        sourceList.add(user1);
        
        UserInfo user2 = new UserInfo();
        user2.setUserInfoId(1028L);
        user2.setLoginName("consumerlistuser2");
        user2.setUserAge(35);
        user2.setUserType("MANAGER");
        sourceList.add(user2);

        // 执行拷贝并添加额外处理
        List<UserInfo> resultList = BeanKit.<UserInfo, UserInfo>copyProperties(UserInfo.class, sourceList, (sourceObj, targetObj) -> {
            targetObj.setStatus("CONSUMER_LIST_PROCESSED");
            targetObj.setDescription("通过Consumer处理的列表拷贝");
        });

        // 验证结果
        assertNotNull(resultList, "拷贝结果不应为空");
        assertEquals(2, resultList.size(), "列表大小应该匹配");
        
        UserInfo result1 = resultList.get(0);
        assertEquals(Long.valueOf(1027L), result1.getUserInfoId(), "第一个用户ID应该匹配");
        assertEquals("consumerlistuser1", result1.getLoginName(), "第一个用户登录名应该匹配");
        assertEquals(Integer.valueOf(24), result1.getUserAge(), "第一个用户年龄应该匹配");
        assertEquals("USER", result1.getUserType(), "第一个用户类型应该匹配");
        assertEquals("CONSUMER_LIST_PROCESSED", result1.getStatus(), "第一个用户状态应该被Consumer设置");
        assertEquals("通过Consumer处理的列表拷贝", result1.getDescription(), "第一个用户描述应该被Consumer设置");
        
        UserInfo result2 = resultList.get(1);
        assertEquals(Long.valueOf(1028L), result2.getUserInfoId(), "第二个用户ID应该匹配");
        assertEquals("consumerlistuser2", result2.getLoginName(), "第二个用户登录名应该匹配");
        assertEquals(Integer.valueOf(35), result2.getUserAge(), "第二个用户年龄应该匹配");
        assertEquals("MANAGER", result2.getUserType(), "第二个用户类型应该匹配");
        assertEquals("CONSUMER_LIST_PROCESSED", result2.getStatus(), "第二个用户状态应该被Consumer设置");
        assertEquals("通过Consumer处理的列表拷贝", result2.getDescription(), "第二个用户描述应该被Consumer设置");
    }

    /**
     * 测试带忽略属性的对象列表属性拷贝功能
     */
    @Test
    public void testCopyPropertiesListWithIgnoreProperties() {
        // 准备源对象列表
        List<UserInfo> sourceList = new ArrayList<>();
        
        UserInfo user1 = new UserInfo();
        user1.setUserInfoId(1029L);
        user1.setLoginName("ignorelistuser1");
        user1.setPassword("password789");
        user1.setUserAge(26);
        user1.setUserType("USER");
        user1.setEmail("ignorelistuser1@example.com");
        sourceList.add(user1);
        
        UserInfo user2 = new UserInfo();
        user2.setUserInfoId(1030L);
        user2.setLoginName("ignorelistuser2");
        user2.setPassword("password999");
        user2.setUserAge(37);
        user2.setUserType("ADMIN");
        user2.setEmail("ignorelistuser2@example.com");
        sourceList.add(user2);

        // 执行拷贝（忽略密码和邮箱）
        String[] ignoreProperties = {"password", "email"};
        List<UserInfo> resultList = BeanKit.copyProperties(UserInfo.class, sourceList, ignoreProperties);

        // 验证结果
        assertNotNull(resultList, "拷贝结果不应为空");
        assertEquals(2, resultList.size(), "列表大小应该匹配");
        
        UserInfo result1 = resultList.get(0);
        assertEquals(Long.valueOf(1029L), result1.getUserInfoId(), "第一个用户ID应该匹配");
        assertEquals("ignorelistuser1", result1.getLoginName(), "第一个用户登录名应该匹配");
        assertNull(result1.getPassword(), "第一个用户密码应该被忽略");
        assertEquals(Integer.valueOf(26), result1.getUserAge(), "第一个用户年龄应该匹配");
        assertEquals("USER", result1.getUserType(), "第一个用户类型应该匹配");
        assertNull(result1.getEmail(), "第一个用户邮箱应该被忽略");
        
        UserInfo result2 = resultList.get(1);
        assertEquals(Long.valueOf(1030L), result2.getUserInfoId(), "第二个用户ID应该匹配");
        assertEquals("ignorelistuser2", result2.getLoginName(), "第二个用户登录名应该匹配");
        assertNull(result2.getPassword(), "第二个用户密码应该被忽略");
        assertEquals(Integer.valueOf(37), result2.getUserAge(), "第二个用户年龄应该匹配");
        assertEquals("ADMIN", result2.getUserType(), "第二个用户类型应该匹配");
        assertNull(result2.getEmail(), "第二个用户邮箱应该被忽略");
    }

    /**
     * 测试带忽略属性和 Consumer 的对象列表属性拷贝功能
     */
    @Test
    public void testCopyPropertiesListWithIgnorePropertiesAndConsumer() {
        // 准备源对象列表
        List<UserInfo> sourceList = new ArrayList<>();
        
        UserInfo user1 = new UserInfo();
        user1.setUserInfoId(1031L);
        user1.setLoginName("ignoreconsumerlistuser1");
        user1.setPassword("password111");
        user1.setUserAge(28);
        user1.setUserType("USER");
        user1.setEmail("ignoreconsumerlistuser1@example.com");
        sourceList.add(user1);
        
        UserInfo user2 = new UserInfo();
        user2.setUserInfoId(1032L);
        user2.setLoginName("ignoreconsumerlistuser2");
        user2.setPassword("password222");
        user2.setUserAge(39);
        user2.setUserType("MANAGER");
        user2.setEmail("ignoreconsumerlistuser2@example.com");
        sourceList.add(user2);

        // 执行拷贝（忽略密码和邮箱，并添加Consumer处理）
        String[] ignoreProperties = {"password", "email"};
        List<UserInfo> resultList = BeanKit.<UserInfo, UserInfo>copyProperties(UserInfo.class, sourceList, ignoreProperties, (sourceObj, targetObj) -> {
            targetObj.setStatus("IGNORE_CONSUMER_LIST_PROCESSED");
            targetObj.setDescription("通过忽略属性和Consumer处理的列表拷贝");
        });

        // 验证结果
        assertNotNull(resultList, "拷贝结果不应为空");
        assertEquals(2, resultList.size(), "列表大小应该匹配");
        
        UserInfo result1 = resultList.get(0);
        assertEquals(Long.valueOf(1031L), result1.getUserInfoId(), "第一个用户ID应该匹配");
        assertEquals("ignoreconsumerlistuser1", result1.getLoginName(), "第一个用户登录名应该匹配");
        assertNull(result1.getPassword(), "第一个用户密码应该被忽略");
        assertEquals(Integer.valueOf(28), result1.getUserAge(), "第一个用户年龄应该匹配");
        assertEquals("USER", result1.getUserType(), "第一个用户类型应该匹配");
        assertNull(result1.getEmail(), "第一个用户邮箱应该被忽略");
        assertEquals("IGNORE_CONSUMER_LIST_PROCESSED", result1.getStatus(), "第一个用户状态应该被Consumer设置");
        assertEquals("通过忽略属性和Consumer处理的列表拷贝", result1.getDescription(), "第一个用户描述应该被Consumer设置");
        
        UserInfo result2 = resultList.get(1);
        assertEquals(Long.valueOf(1032L), result2.getUserInfoId(), "第二个用户ID应该匹配");
        assertEquals("ignoreconsumerlistuser2", result2.getLoginName(), "第二个用户登录名应该匹配");
        assertNull(result2.getPassword(), "第二个用户密码应该被忽略");
        assertEquals(Integer.valueOf(39), result2.getUserAge(), "第二个用户年龄应该匹配");
        assertEquals("MANAGER", result2.getUserType(), "第二个用户类型应该匹配");
        assertNull(result2.getEmail(), "第二个用户邮箱应该被忽略");
        assertEquals("IGNORE_CONSUMER_LIST_PROCESSED", result2.getStatus(), "第二个用户状态应该被Consumer设置");
        assertEquals("通过忽略属性和Consumer处理的列表拷贝", result2.getDescription(), "第二个用户描述应该被Consumer设置");
    }

    /**
     * 测试带忽略属性的分页对象拷贝功能
     */
    @Test
    public void testCopyPageWithIgnoreProperties() {
        // 准备源分页对象
        List<UserInfo> sourceList = new ArrayList<>();
        
        UserInfo user1 = new UserInfo();
        user1.setUserInfoId(1033L);
        user1.setLoginName("ignorepageuser1");
        user1.setPassword("password333");
        user1.setUserAge(30);
        user1.setUserType("USER");
        user1.setEmail("ignorepageuser1@example.com");
        sourceList.add(user1);
        
        UserInfo user2 = new UserInfo();
        user2.setUserInfoId(1034L);
        user2.setLoginName("ignorepageuser2");
        user2.setPassword("password444");
        user2.setUserAge(41);
        user2.setUserType("ADMIN");
        user2.setEmail("ignorepageuser2@example.com");
        sourceList.add(user2);

        Pagination pagination = new Pagination(15, 200);
        Page<UserInfo> sourcePage = new Page<>(sourceList, pagination);

        // 执行拷贝（忽略密码和邮箱）
        String[] ignoreProperties = {"password", "email"};
        Page<UserInfo> resultPage = BeanKit.copyPage(UserInfo.class, sourcePage, ignoreProperties);

        // 验证结果
        assertNotNull(resultPage, "拷贝结果不应为空");
        assertNotNull(resultPage.getPagination(), "分页信息不应为空");
        assertNotNull(resultPage.getList(), "数据列表不应为空");
        assertEquals(2, resultPage.getList().size(), "列表大小应该匹配");
        assertEquals(15, resultPage.getPagination().getPageSize(), "分页大小应该匹配");
        assertEquals(200, resultPage.getPagination().getTotalItems(), "总记录数应该匹配");
        
        UserInfo result1 = resultPage.getList().get(0);
        assertEquals(Long.valueOf(1033L), result1.getUserInfoId(), "第一个用户ID应该匹配");
        assertEquals("ignorepageuser1", result1.getLoginName(), "第一个用户登录名应该匹配");
        assertNull(result1.getPassword(), "第一个用户密码应该被忽略");
        assertEquals(Integer.valueOf(30), result1.getUserAge(), "第一个用户年龄应该匹配");
        assertEquals("USER", result1.getUserType(), "第一个用户类型应该匹配");
        assertNull(result1.getEmail(), "第一个用户邮箱应该被忽略");
        
        UserInfo result2 = resultPage.getList().get(1);
        assertEquals(Long.valueOf(1034L), result2.getUserInfoId(), "第二个用户ID应该匹配");
        assertEquals("ignorepageuser2", result2.getLoginName(), "第二个用户登录名应该匹配");
        assertNull(result2.getPassword(), "第二个用户密码应该被忽略");
        assertEquals(Integer.valueOf(41), result2.getUserAge(), "第二个用户年龄应该匹配");
        assertEquals("ADMIN", result2.getUserType(), "第二个用户类型应该匹配");
        assertNull(result2.getEmail(), "第二个用户邮箱应该被忽略");
    }

    /**
     * 测试带 Consumer 的分页对象拷贝功能
     */
    @Test
    public void testCopyPageWithConsumer() {
        // 准备源分页对象
        List<UserInfo> sourceList = new ArrayList<>();
        
        UserInfo user1 = new UserInfo();
        user1.setUserInfoId(1035L);
        user1.setLoginName("consumerpageuser1");
        user1.setUserAge(32);
        user1.setUserType("USER");
        sourceList.add(user1);
        
        UserInfo user2 = new UserInfo();
        user2.setUserInfoId(1036L);
        user2.setLoginName("consumerpageuser2");
        user2.setUserAge(43);
        user2.setUserType("MANAGER");
        sourceList.add(user2);

        Pagination pagination = new Pagination(25, 300);
        Page<UserInfo> sourcePage = new Page<>(sourceList, pagination);

        // 执行拷贝并添加额外处理
        Page<UserInfo> resultPage = BeanKit.copyPage(UserInfo.class, sourcePage, (sourceObj, targetObj) -> {
            targetObj.setStatus("CONSUMER_PAGE_PROCESSED");
            targetObj.setDescription("通过Consumer处理的分页拷贝");
        });

        // 验证结果
        assertNotNull(resultPage, "拷贝结果不应为空");
        assertNotNull(resultPage.getPagination(), "分页信息不应为空");
        assertNotNull(resultPage.getList(), "数据列表不应为空");
        assertEquals(2, resultPage.getList().size(), "列表大小应该匹配");
        assertEquals(25, resultPage.getPagination().getPageSize(), "分页大小应该匹配");
        assertEquals(300, resultPage.getPagination().getTotalItems(), "总记录数应该匹配");
        
        UserInfo result1 = resultPage.getList().get(0);
        assertEquals(Long.valueOf(1035L), result1.getUserInfoId(), "第一个用户ID应该匹配");
        assertEquals("consumerpageuser1", result1.getLoginName(), "第一个用户登录名应该匹配");
        assertEquals(Integer.valueOf(32), result1.getUserAge(), "第一个用户年龄应该匹配");
        assertEquals("USER", result1.getUserType(), "第一个用户类型应该匹配");
        assertEquals("CONSUMER_PAGE_PROCESSED", result1.getStatus(), "第一个用户状态应该被Consumer设置");
        assertEquals("通过Consumer处理的分页拷贝", result1.getDescription(), "第一个用户描述应该被Consumer设置");
        
        UserInfo result2 = resultPage.getList().get(1);
        assertEquals(Long.valueOf(1036L), result2.getUserInfoId(), "第二个用户ID应该匹配");
        assertEquals("consumerpageuser2", result2.getLoginName(), "第二个用户登录名应该匹配");
        assertEquals(Integer.valueOf(43), result2.getUserAge(), "第二个用户年龄应该匹配");
        assertEquals("MANAGER", result2.getUserType(), "第二个用户类型应该匹配");
        assertEquals("CONSUMER_PAGE_PROCESSED", result2.getStatus(), "第二个用户状态应该被Consumer设置");
        assertEquals("通过Consumer处理的分页拷贝", result2.getDescription(), "第二个用户描述应该被Consumer设置");
    }

    /**
     * 测试带忽略属性和 Consumer 的分页对象拷贝功能
     */
    @Test
    public void testCopyPageWithIgnorePropertiesAndConsumer() {
        // 准备源分页对象
        List<UserInfo> sourceList = new ArrayList<>();
        
        UserInfo user1 = new UserInfo();
        user1.setUserInfoId(1037L);
        user1.setLoginName("ignoreconsumerpageuser1");
        user1.setPassword("password555");
        user1.setUserAge(34);
        user1.setUserType("USER");
        user1.setEmail("ignoreconsumerpageuser1@example.com");
        sourceList.add(user1);
        
        UserInfo user2 = new UserInfo();
        user2.setUserInfoId(1038L);
        user2.setLoginName("ignoreconsumerpageuser2");
        user2.setPassword("password666");
        user2.setUserAge(45);
        user2.setUserType("ADMIN");
        user2.setEmail("ignoreconsumerpageuser2@example.com");
        sourceList.add(user2);

        Pagination pagination = new Pagination(30, 400);
        Page<UserInfo> sourcePage = new Page<>(sourceList, pagination);

        // 执行拷贝（忽略密码和邮箱，并添加Consumer处理）
        String[] ignoreProperties = {"password", "email"};
        Page<UserInfo> resultPage = BeanKit.copyPage(UserInfo.class, sourcePage, ignoreProperties, (sourceObj, targetObj) -> {
            targetObj.setStatus("IGNORE_CONSUMER_PAGE_PROCESSED");
            targetObj.setDescription("通过忽略属性和Consumer处理的分页拷贝");
        });

        // 验证结果
        assertNotNull(resultPage, "拷贝结果不应为空");
        assertNotNull(resultPage.getPagination(), "分页信息不应为空");
        assertNotNull(resultPage.getList(), "数据列表不应为空");
        assertEquals(2, resultPage.getList().size(), "列表大小应该匹配");
        assertEquals(30, resultPage.getPagination().getPageSize(), "分页大小应该匹配");
        assertEquals(400, resultPage.getPagination().getTotalItems(), "总记录数应该匹配");
        
        UserInfo result1 = resultPage.getList().get(0);
        assertEquals(Long.valueOf(1037L), result1.getUserInfoId(), "第一个用户ID应该匹配");
        assertEquals("ignoreconsumerpageuser1", result1.getLoginName(), "第一个用户登录名应该匹配");
        assertNull(result1.getPassword(), "第一个用户密码应该被忽略");
        assertEquals(Integer.valueOf(34), result1.getUserAge(), "第一个用户年龄应该匹配");
        assertEquals("USER", result1.getUserType(), "第一个用户类型应该匹配");
        assertNull(result1.getEmail(), "第一个用户邮箱应该被忽略");
        assertEquals("IGNORE_CONSUMER_PAGE_PROCESSED", result1.getStatus(), "第一个用户状态应该被Consumer设置");
        assertEquals("通过忽略属性和Consumer处理的分页拷贝", result1.getDescription(), "第一个用户描述应该被Consumer设置");
        
        UserInfo result2 = resultPage.getList().get(1);
        assertEquals(Long.valueOf(1038L), result2.getUserInfoId(), "第二个用户ID应该匹配");
        assertEquals("ignoreconsumerpageuser2", result2.getLoginName(), "第二个用户登录名应该匹配");
        assertNull(result2.getPassword(), "第二个用户密码应该被忽略");
        assertEquals(Integer.valueOf(45), result2.getUserAge(), "第二个用户年龄应该匹配");
        assertEquals("ADMIN", result2.getUserType(), "第二个用户类型应该匹配");
        assertNull(result2.getEmail(), "第二个用户邮箱应该被忽略");
        assertEquals("IGNORE_CONSUMER_PAGE_PROCESSED", result2.getStatus(), "第二个用户状态应该被Consumer设置");
        assertEquals("通过忽略属性和Consumer处理的分页拷贝", result2.getDescription(), "第二个用户描述应该被Consumer设置");
    }
}

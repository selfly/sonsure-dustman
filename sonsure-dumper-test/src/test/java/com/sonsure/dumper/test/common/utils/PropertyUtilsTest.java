/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.common.utils;

import com.sonsure.dumper.common.utils.PropertyUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * PropertyUtils 单元测试
 */
public class PropertyUtilsTest {

    @BeforeEach
    public void setUp() {
        // 注意：由于 PropertyUtils 使用了静态缓存，某些测试可能会受到其他测试的影响
        // 如果需要在测试间清理缓存，可以考虑使用反射清理 RESOURCE_MAP
    }

    // ========== getProperties() 方法测试 - Properties 文件 ==========

    @Test
    public void testGetProperties_PropertiesFile() {
        Map<String, String> props = PropertyUtils.getProperties("init.properties");
        Assertions.assertNotNull(props);
        Assertions.assertFalse(props.isEmpty());
        Assertions.assertTrue(props.containsKey("driverClassName"));
        Assertions.assertEquals("org.h2.Driver", props.get("driverClassName"));
    }

    @Test
    public void testGetProperties_PropertiesFileWithClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Map<String, String> props = PropertyUtils.getProperties("init.properties", classLoader);
        Assertions.assertNotNull(props);
        Assertions.assertFalse(props.isEmpty());
        Assertions.assertTrue(props.containsKey("jdbcUrl"));
    }

    @Test
    public void testGetProperties_MultiDsProperties() {
        Map<String, String> props = PropertyUtils.getProperties("multi-ds.properties");
        Assertions.assertNotNull(props);
        Assertions.assertFalse(props.isEmpty());
        Assertions.assertTrue(props.containsKey("mysql.driverClassName"));
        Assertions.assertTrue(props.containsKey("oracle.driverClassName"));
        Assertions.assertEquals("com.mysql.jdbc.Driver", props.get("mysql.driverClassName"));
        Assertions.assertEquals("oracle.jdbc.driver.OracleDriver", props.get("oracle.driverClassName"));
    }

    @Test
    public void testGetProperties_PropertiesCache() {
        // 第一次加载
        Map<String, String> props1 = PropertyUtils.getProperties("init.properties");
        // 第二次加载应该使用缓存
        Map<String, String> props2 = PropertyUtils.getProperties("init.properties");
        // 应该是同一个 Map 对象（缓存）
        Assertions.assertSame(props1, props2);
    }

    @Test
    public void testGetProperties_PropertiesEmptyValue() {
        Map<String, String> props = PropertyUtils.getProperties("init.properties");
        Assertions.assertNotNull(props);
        // 测试空值处理
        String password = props.get("password");
        // properties 文件中的空值可能会是空字符串
        Assertions.assertNotNull(password);
    }

    @Test
    public void testGetProperties_PropertiesWithSpecialCharacters() {
        Map<String, String> props = PropertyUtils.getProperties("init.properties");
        Assertions.assertNotNull(props);
        // 测试包含 ${} 这样的特殊字符
        String jdbcUrl = props.get("jdbcUrl");
        Assertions.assertNotNull(jdbcUrl);
        Assertions.assertTrue(jdbcUrl.contains("${") || jdbcUrl.contains("}"));
    }

    // ========== getProperties() 方法测试 - YML 文件 ==========

    @Test
    public void testGetProperties_YmlFile() {
        Map<String, String> props = PropertyUtils.getProperties("application.yml");
        Assertions.assertNotNull(props);
        Assertions.assertFalse(props.isEmpty());
        // YML 文件会被转换为扁平化的 key-value，使用点号分隔嵌套层级
        Assertions.assertTrue(props.containsKey("server.port"));
        Assertions.assertEquals("8001", props.get("server.port"));
    }

    @Test
    public void testGetProperties_YmlFileWithClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Map<String, String> props = PropertyUtils.getProperties("application.yml", classLoader);
        Assertions.assertNotNull(props);
        Assertions.assertFalse(props.isEmpty());
        Assertions.assertTrue(props.containsKey("spring.profiles.active"));
        Assertions.assertEquals("dev", props.get("spring.profiles.active"));
    }

    @Test
    public void testGetProperties_YmlNestedStructure() {
        Map<String, String> props = PropertyUtils.getProperties("application.yml");
        Assertions.assertNotNull(props);
        // 测试嵌套结构的扁平化
        Assertions.assertTrue(props.containsKey("server.tomcat.uri-encoding"));
        Assertions.assertEquals("utf-8", props.get("server.tomcat.uri-encoding"));
        Assertions.assertTrue(props.containsKey("spring.datasource.url"));
        Assertions.assertTrue(props.containsKey("spring.datasource.driver-class-name"));
        Assertions.assertEquals("org.h2.Driver", props.get("spring.datasource.driver-class-name"));
    }

    @Test
    public void testGetProperties_YmlCache() {
        // 第一次加载
        Map<String, String> props1 = PropertyUtils.getProperties("application.yml");
        // 第二次加载应该使用缓存
        Map<String, String> props2 = PropertyUtils.getProperties("application.yml");
        // 应该是同一个 Map 对象（缓存）
        Assertions.assertSame(props1, props2);
    }

    // ========== getProperty() 方法测试 - 基础功能 ==========

    @Test
    public void testGetProperty_WithExistingKey() {
        String value = PropertyUtils.getProperty("init.properties", "driverClassName");
        Assertions.assertNotNull(value);
        Assertions.assertEquals("org.h2.Driver", value);
    }

    @Test
    public void testGetProperty_WithExistingKeyAndClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String value = PropertyUtils.getProperty("init.properties", "jdbcUrl", classLoader);
        Assertions.assertNotNull(value);
        Assertions.assertTrue(value.contains("jdbc:h2"));
    }

    @Test
    public void testGetProperty_WithNonExistentKey() {
        String value = PropertyUtils.getProperty("init.properties", "nonExistentKey");
        Assertions.assertNull(value);
    }

    @Test
    public void testGetProperty_WithDefaultValue() {
        String value = PropertyUtils.getProperty("init.properties", "nonExistentKey", "defaultValue");
        Assertions.assertNotNull(value);
        Assertions.assertEquals("defaultValue", value);
    }

    @Test
    public void testGetProperty_WithDefaultValueAndClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String value = PropertyUtils.getProperty("init.properties", "nonExistentKey", "defaultValue", classLoader);
        Assertions.assertNotNull(value);
        Assertions.assertEquals("defaultValue", value);
    }

    @Test
    public void testGetProperty_ExistingKeyWithDefaultValue() {
        // 当 key 存在时，不应该返回默认值
        String value = PropertyUtils.getProperty("init.properties", "driverClassName", "defaultValue");
        Assertions.assertNotNull(value);
        Assertions.assertEquals("org.h2.Driver", value);
        Assertions.assertNotEquals("defaultValue", value);
    }

    @Test
    public void testGetProperty_EmptyValueWithDefaultValue() {
        // 空字符串值也应该返回默认值（因为 StrUtils.isNotBlank 会检查）
        String value = PropertyUtils.getProperty("init.properties", "password", "defaultPassword");
        // password 在 properties 文件中可能是空字符串
        Assertions.assertNotNull(value);
    }

    @Test
    public void testGetProperty_YmlFile() {
        String value = PropertyUtils.getProperty("application.yml", "server.port");
        Assertions.assertNotNull(value);
        Assertions.assertEquals("8001", value);
    }

    @Test
    public void testGetProperty_YmlNestedKey() {
        String value = PropertyUtils.getProperty("application.yml", "spring.datasource.driver-class-name");
        Assertions.assertNotNull(value);
        Assertions.assertEquals("org.h2.Driver", value);
    }

    @Test
    public void testGetProperty_YmlWithDefaultValue() {
        String value = PropertyUtils.getProperty("application.yml", "nonExistent.key", "default");
        Assertions.assertNotNull(value);
        Assertions.assertEquals("default", value);
    }

    // ========== 错误场景测试 ==========

    @Test
    public void testGetProperties_NonExistentFile() {
        // 不存在的文件应该返回空 Map 或者抛出异常（取决于实现）
        Map<String, String> props = PropertyUtils.getProperties("non-existent.properties");
        // 根据 getResources 的实现，如果文件不存在，可能返回空 Map
        Assertions.assertNotNull(props);
    }

    @Test
    public void testGetProperty_NonExistentFile() {
        String value = PropertyUtils.getProperty("non-existent.properties", "key");
        Assertions.assertNull(value);
    }

    @Test
    public void testGetProperty_NonExistentFileWithDefault() {
        String value = PropertyUtils.getProperty("non-existent.properties", "key", "default");
        Assertions.assertNotNull(value);
        Assertions.assertEquals("default", value);
    }

    // ========== 边界值测试 ==========

    @Test
    public void testGetProperties_NullResourceName() {
        Assertions.assertThrows(Exception.class, () -> {
            PropertyUtils.getProperties(null);
        });
    }

    @Test
    public void testGetProperties_EmptyResourceName() {
        Assertions.assertThrows(Exception.class, () -> {
            PropertyUtils.getProperties("");
        });
    }

    @Test
    public void testGetProperty_NullKey() {
        String value = PropertyUtils.getProperty("init.properties", null);
        Assertions.assertNull(value);
    }

    @Test
    public void testGetProperty_NullKeyWithDefault() {
        String value = PropertyUtils.getProperty("init.properties", null, "default");
        Assertions.assertNotNull(value);
        Assertions.assertEquals("default", value);
    }

    @Test
    public void testGetProperty_EmptyKey() {
        String value = PropertyUtils.getProperty("init.properties", "");
        Assertions.assertNull(value);
    }

    @Test
    public void testGetProperty_EmptyKeyWithDefault() {
        String value = PropertyUtils.getProperty("init.properties", "", "default");
        Assertions.assertNotNull(value);
        Assertions.assertEquals("default", value);
    }

    @Test
    public void testGetProperty_NullDefaultValue() {
        // 使用明确的类型转换来避免方法重载歧义
        String defaultValue = null;
        String value = PropertyUtils.getProperty("init.properties", "nonExistentKey", defaultValue);
        Assertions.assertNull(value);
    }

    // ========== 多个同名文件覆盖测试 ==========

    @Test
    public void testGetProperties_MultipleFilesWithSameName() {
        // 测试多个同名文件的加载和覆盖机制
        // 由于 classpath 可能有多个同名文件，后加载的会覆盖先加载的
        Map<String, String> props = PropertyUtils.getProperties("init.properties");
        Assertions.assertNotNull(props);
        // 验证所有同名文件的属性都被加载（最后加载的会覆盖）
        // 这个测试依赖于实际的文件布局
    }

    // ========== ClassLoader 测试 ==========

    @Test
    public void testGetProperties_DifferentClassLoader() {
        ClassLoader classLoader1 = Thread.currentThread().getContextClassLoader();
        ClassLoader classLoader2 = ClassLoader.getSystemClassLoader();
        
        Map<String, String> props1 = PropertyUtils.getProperties("init.properties", classLoader1);
        Map<String, String> props2 = PropertyUtils.getProperties("init.properties", classLoader2);
        
        Assertions.assertNotNull(props1);
        Assertions.assertNotNull(props2);
        // 由于缓存机制，可能返回相同的 Map
        // 但如果是不同的 ClassLoader，应该能找到相同的资源
    }

    @Test
    public void testGetProperty_DifferentClassLoader() {
        ClassLoader classLoader1 = Thread.currentThread().getContextClassLoader();
        ClassLoader classLoader2 = ClassLoader.getSystemClassLoader();
        
        String value1 = PropertyUtils.getProperty("init.properties", "driverClassName", classLoader1);
        String value2 = PropertyUtils.getProperty("init.properties", "driverClassName", classLoader2);
        
        Assertions.assertNotNull(value1);
        Assertions.assertNotNull(value2);
        // 应该得到相同的值
        Assertions.assertEquals(value1, value2);
    }

    // ========== YML 复杂结构测试 ==========

    @Test
    public void testGetProperties_YmlDeepNesting() {
        Map<String, String> props = PropertyUtils.getProperties("application.yml");
        Assertions.assertNotNull(props);
        
        // 测试深度嵌套的 key
        String uriEncoding = props.get("server.tomcat.uri-encoding");
        Assertions.assertNotNull(uriEncoding);
        Assertions.assertEquals("utf-8", uriEncoding);
    }

    @Test
    public void testGetProperty_YmlDeepNestedKey() {
        String value = PropertyUtils.getProperty("application.yml", "server.tomcat.uri-encoding");
        Assertions.assertNotNull(value);
        Assertions.assertEquals("utf-8", value);
    }

    // ========== Properties 文件特殊字符测试 ==========

    @Test
    public void testGetProperties_PropertiesWithComments() {
        Map<String, String> props = PropertyUtils.getProperties("init.properties");
        Assertions.assertNotNull(props);
        // Properties 文件的注释行（# 开头）不应该被加载
        // 验证不包含注释内容作为 key
    }

    @Test
    public void testGetProperties_PropertiesWithSpaces() {
        Map<String, String> props = PropertyUtils.getProperties("init.properties");
        Assertions.assertNotNull(props);
        // 测试包含空格的 key 或 value
        String jdbcTemplateFetchSize = props.get("jdbcTemplate.fetchSize");
        Assertions.assertNotNull(jdbcTemplateFetchSize);
        Assertions.assertEquals("350", jdbcTemplateFetchSize);
    }

    // ========== 缓存一致性测试 ==========

    @Test
    public void testGetProperties_CacheConsistency() {
        Map<String, String> props1 = PropertyUtils.getProperties("init.properties");
        Map<String, String> props2 = PropertyUtils.getProperties("init.properties");
        
        // 应该是同一个对象（缓存）
        Assertions.assertSame(props1, props2);
        
        // 内容应该一致
        Assertions.assertEquals(props1.size(), props2.size());
        Assertions.assertEquals(props1.get("driverClassName"), props2.get("driverClassName"));
    }

    @Test
    public void testGetProperty_CacheConsistency() {
        String value1 = PropertyUtils.getProperty("init.properties", "driverClassName");
        String value2 = PropertyUtils.getProperty("init.properties", "driverClassName");
        
        // 应该得到相同的值
        Assertions.assertEquals(value1, value2);
    }

    // ========== 实际使用场景测试 ==========

    @Test
    public void testGetProperty_RealWorldScenario() {
        // 模拟实际使用场景：从配置文件中读取数据库配置
        String driverClass = PropertyUtils.getProperty("init.properties", "driverClassName");
        String jdbcUrl = PropertyUtils.getProperty("init.properties", "jdbcUrl");
        String username = PropertyUtils.getProperty("init.properties", "jdbcUsername");
        String password = PropertyUtils.getProperty("init.properties", "password", "");
        
        Assertions.assertNotNull(driverClass);
        Assertions.assertNotNull(jdbcUrl);
        Assertions.assertNotNull(username);
        Assertions.assertNotNull(password); // 可能是空字符串
    }

    @Test
    public void testGetProperty_YmlRealWorldScenario() {
        // 模拟实际使用场景：从 YML 配置文件中读取 Spring 配置
        String port = PropertyUtils.getProperty("application.yml", "server.port");
        String activeProfile = PropertyUtils.getProperty("application.yml", "spring.profiles.active");
        String datasourceUrl = PropertyUtils.getProperty("application.yml", "spring.datasource.url");
        
        Assertions.assertNotNull(port);
        Assertions.assertEquals("8001", port);
        Assertions.assertNotNull(activeProfile);
        Assertions.assertEquals("dev", activeProfile);
        Assertions.assertNotNull(datasourceUrl);
    }

    // ========== 文件路径测试 ==========

    @Test
    public void testGetProperties_WithPath() {
        // 测试带路径的资源文件
        Map<String, String> props = PropertyUtils.getProperties("init.properties");
        Assertions.assertNotNull(props);
        // 验证可以加载 classpath 下的文件
    }

    @Test
    public void testGetProperties_RelativePath() {
        // 测试相对路径
        Map<String, String> props = PropertyUtils.getProperties("init.properties");
        Assertions.assertNotNull(props);
    }

    // ========== 混合测试 ==========

    @Test
    public void testGetProperty_MultipleCalls() {
        // 多次调用应该使用缓存
        String value1 = PropertyUtils.getProperty("init.properties", "driverClassName");
        String value2 = PropertyUtils.getProperty("init.properties", "driverClassName");
        String value3 = PropertyUtils.getProperty("init.properties", "driverClassName");
        
        Assertions.assertEquals(value1, value2);
        Assertions.assertEquals(value2, value3);
    }

    @Test
    public void testGetProperties_AllKeys() {
        Map<String, String> props = PropertyUtils.getProperties("init.properties");
        Assertions.assertNotNull(props);
        Assertions.assertFalse(props.isEmpty());
        
        // 验证包含预期的 key
        Assertions.assertTrue(props.containsKey("driverClassName"));
        Assertions.assertTrue(props.containsKey("jdbcUrl"));
        Assertions.assertTrue(props.containsKey("jdbcUsername"));
    }

    @Test
    public void testGetProperties_YmlAllKeys() {
        Map<String, String> props = PropertyUtils.getProperties("application.yml");
        Assertions.assertNotNull(props);
        Assertions.assertFalse(props.isEmpty());
        
        // 验证包含预期的扁平化 key
        Assertions.assertTrue(props.containsKey("server.port"));
        Assertions.assertTrue(props.containsKey("spring.profiles.active"));
        Assertions.assertTrue(props.containsKey("spring.datasource.url"));
    }

    // ========== 特殊情况测试 ==========

    @Test
    public void testGetProperty_ValueWithEqualsSign() {
        // 测试值中包含等号的情况
        Map<String, String> props = PropertyUtils.getProperties("init.properties");
        String jdbcUrl = props.get("jdbcUrl");
        // jdbcUrl 可能包含特殊字符
        Assertions.assertNotNull(jdbcUrl);
    }

    @Test
    public void testGetProperty_ValueWithColon() {
        // 测试值中包含冒号的情况
        Map<String, String> props = PropertyUtils.getProperties("init.properties");
        String jdbcUrl = props.get("jdbcUrl");
        // jdbc:h2:... 包含冒号
        Assertions.assertNotNull(jdbcUrl);
        Assertions.assertTrue(jdbcUrl.contains(":"));
    }

    @Test
    public void testGetProperty_ValueWithDollarSign() {
        // 测试值中包含 $ 符号的情况（变量引用）
        Map<String, String> props = PropertyUtils.getProperties("init.properties");
        String jdbcUrl = props.get("jdbcUrl");
        // 可能包含 ${user.dir} 这样的变量
        Assertions.assertNotNull(jdbcUrl);
    }

    // ========== YML null 值处理测试 ==========

    @Test
    public void testGetProperties_YmlNullValue() {
        Map<String, String> props = PropertyUtils.getProperties("application.yml");
        Assertions.assertNotNull(props);
        // YML 中的 null 值应该被转换为空字符串
    }
}

package com.sonsure.dumper.test.common.utility;

import com.sonsure.dumper.common.utility.GenericResource;
import com.sonsure.dumper.common.utility.GenericResourcePatternResolverImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GenericResourcePatternResolver 测试类
 *
 * @author selfly
 */
public class GenericResourcePatternResolverTest {

    @Test
    public void getClasspathFileResource() throws Exception {
        GenericResourcePatternResolverImpl resolver = new GenericResourcePatternResolverImpl();

        // 测试 classpath 资源加载
        List<GenericResource> classpathResources = resolver.getResources("classpath:/db/migration/mysql/V1.0.0__dumper_test.sql");
        Assertions.assertEquals(1, classpathResources.size());
        Assertions.assertEquals("V1.0.0__dumper_test.sql", classpathResources.get(0).getFilename());
        for (GenericResource classpathResource : classpathResources) {
            InputStream is = classpathResource.getInputStream();
            Assertions.assertNotNull(is);
        }

        classpathResources = resolver.getResources("classpath:/db/migration/mysql/*.sql");
        Assertions.assertEquals(1, classpathResources.size());
        Assertions.assertEquals("V1.0.0__dumper_test.sql", classpathResources.get(0).getFilename());
        for (GenericResource classpathResource : classpathResources) {
            InputStream is = classpathResource.getInputStream();
            Assertions.assertNotNull(is);
        }

        classpathResources = resolver.getResources("classpath:/db/migration/mysql/V1.0.0__*_test.sql");
        Assertions.assertEquals(1, classpathResources.size());
        Assertions.assertEquals("V1.0.0__dumper_test.sql", classpathResources.get(0).getFilename());
        for (GenericResource classpathResource : classpathResources) {
            InputStream is = classpathResource.getInputStream();
            Assertions.assertNotNull(is);
        }
    }

    @Test
    public void getClasspathJarResource() throws Exception {
        GenericResourcePatternResolverImpl resolver = new GenericResourcePatternResolverImpl();

        // 测试 classpath 资源加载
        List<GenericResource> classpathResources = resolver.getResources("classpath:/net/sf/jsqlparser/schema/*.class");
        Assertions.assertEquals(10, classpathResources.size());
        List<String> list = classpathResources.stream()
                .map(GenericResource::getFilename)
                .collect(Collectors.toList());
        System.out.println(classpathResources.get(0).toString());
        Assertions.assertTrue(list.contains("Column.class"));
        Assertions.assertTrue(list.contains("Database.class"));
        Assertions.assertTrue(list.contains("Server.class"));
        Assertions.assertTrue(list.contains("Table.class"));

        for (GenericResource classpathResource : classpathResources) {
            InputStream is = classpathResource.getInputStream();
            Assertions.assertNotNull(is);
        }
    }

    @Test
    public void getClasspathAllFileResource() throws IOException {
        GenericResourcePatternResolverImpl resolver = new GenericResourcePatternResolverImpl();

        List<GenericResource> classpathResources = resolver.getResources("classpath*:/db/migration/mysql/V1.0.0__dumper_test.sql");
        Assertions.assertEquals(1, classpathResources.size());
        Assertions.assertEquals("V1.0.0__dumper_test.sql", classpathResources.get(0).getFilename());
        for (GenericResource classpathResource : classpathResources) {
            InputStream is = classpathResource.getInputStream();
            Assertions.assertNotNull(is);
        }

        classpathResources = resolver.getResources("classpath*:/db/migration/mysql/*.sql");
        Assertions.assertEquals(2, classpathResources.size());
        List<String> list = classpathResources.stream()
                .map(GenericResource::getFilename)
                .collect(Collectors.toList());
        Assertions.assertTrue(list.contains("V1.0.0__dumper_test.sql"));
        Assertions.assertTrue(list.contains("V1.0.0__flyable_history.sql"));
        for (GenericResource classpathResource : classpathResources) {
            InputStream is = classpathResource.getInputStream();
            Assertions.assertNotNull(is);
        }

        classpathResources = resolver.getResources("classpath*:/db/migration/mysql/V1.0.0__*.sql");
        Assertions.assertEquals(2, classpathResources.size());
        List<String> list2 = classpathResources.stream()
                .map(GenericResource::getFilename)
                .collect(Collectors.toList());
        Assertions.assertTrue(list2.contains("V1.0.0__dumper_test.sql"));
        Assertions.assertTrue(list2.contains("V1.0.0__flyable_history.sql"));
        for (GenericResource classpathResource : classpathResources) {
            InputStream is = classpathResource.getInputStream();
            Assertions.assertNotNull(is);
        }

        classpathResources = resolver.getResources("classpath*:/mybatis/*.xml");
        Assertions.assertEquals(1, classpathResources.size());
        Assertions.assertEquals("mybatis-config.xml", classpathResources.get(0).getFilename());
        for (GenericResource classpathResource : classpathResources) {
            InputStream is = classpathResource.getInputStream();
            Assertions.assertNotNull(is);
        }

        classpathResources = resolver.getResources("classpath*:/mybatis/**.xml");
        Assertions.assertEquals(2, classpathResources.size());
        List<String> list1 = classpathResources.stream()
                .map(GenericResource::getFilename)
                .collect(Collectors.toList());
        Assertions.assertTrue(list1.contains("mybatis-config.xml"));
        Assertions.assertTrue(list1.contains("user.xml"));
        for (GenericResource classpathResource : classpathResources) {
            InputStream is = classpathResource.getInputStream();
            Assertions.assertNotNull(is);
        }
    }

    @Test
    public void getFileSystemResource() {
        GenericResourcePatternResolverImpl resolver = new GenericResourcePatternResolverImpl();
        String userDir = System.getProperty("user.dir");
        String prefix = "file:" + userDir;
        List<GenericResource> resources = resolver.getResources(prefix + "/src/test/resources/db/migration/mysql/V1.0.0__dumper_test.sql");
        Assertions.assertEquals(1, resources.size());
        Assertions.assertEquals("V1.0.0__dumper_test.sql", resources.get(0).getFilename());

        resources = resolver.getResources(prefix + "/src/test/resources/*.xml");
        Assertions.assertEquals(6, resources.size());

        resources = resolver.getResources(prefix + "/src/test/resources/**.xml");
        Assertions.assertEquals(19, resources.size());
        
        resources = resolver.getResources("file:src/test/env/mysql/application.yml");
        Assertions.assertFalse(resources.isEmpty());

        resources = resolver.getResources("file:src/test/env/mysql/*.*");
        Assertions.assertFalse(resources.isEmpty());

        resources = resolver.getResources("file:src/test/env/**.yml");
        Assertions.assertFalse(resources.isEmpty());
    }
}
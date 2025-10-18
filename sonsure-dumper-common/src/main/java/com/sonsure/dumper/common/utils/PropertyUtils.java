/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.common.utils;

import com.sonsure.dumper.common.exception.SonsureException;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * 属性文件操作辅助类
 * <p/>
 *
 * @author liyd
 */
public final class PropertyUtils {

    /**
     * 属性文件后缀
     */
    private static final String PRO_SUFFIX = ".properties";

    /**
     * yml文件后缀
     */
    private static final String YML_SUFFIX = ".yml";

    /**
     * 配置文件保存map
     */
    private final static Map<String, Map<String, String>> RESOURCE_MAP = new HashMap<String, Map<String, String>>();

    /**
     * 获取resource文件所有属性
     * 当classpath下有多个相同的resource文件时，每个文件都会加载，但如果存在同名key会被覆盖
     *
     * @param resourceName the resource name
     * @return properties
     */
    public static Map<String, String> getProperties(String resourceName) {
        return getProperties(resourceName, ClassUtils.getDefaultClassLoader());
    }

    /**
     * 获取resource文件所有属性
     * 当classpath下有多个相同的resource文件时，每个文件都会加载，但如果存在同名key会被覆盖
     *
     * @param resourceName the resource name
     * @param classLoader  the class loader
     * @return properties
     */
    public static Map<String, String> getProperties(String resourceName, ClassLoader classLoader) {

        Map<String, String> propMap = RESOURCE_MAP.get(resourceName);
        if (propMap != null) {
            return propMap;
        }

        URL[] resources = getResources(resourceName, classLoader);
        propMap = new HashMap<>();
        for (URL resource : resources) {
            Map<String, String> map = null;
            if (resource.getFile().toLowerCase().endsWith(PRO_SUFFIX)) {
                map = getPropertiesResourceMap(resource);
            } else if (resource.getFile().toLowerCase().endsWith(YML_SUFFIX)) {
                map = getYmlResourceMap(resource);
            } else {
                throw new SonsureException("不支持的配置资源文件加载:" + resourceName);
            }
            propMap.putAll(map);
        }
        //加入配置文件属性
        RESOURCE_MAP.put(resourceName, propMap);
        return propMap;
    }

    /**
     * 根据key获取properties文件的value值
     * 当classpath下有多个相同的properties文件时，每个文件都会加载，但如果存在同名key会被覆盖
     *
     * @param resourceName properties文件名
     * @param key          the key
     * @return property
     */
    public static String getProperty(String resourceName, String key) {
        return getProperty(resourceName, key, null, ClassUtils.getDefaultClassLoader());
    }

    /**
     * 根据key获取properties文件的value值
     * 当classpath下有多个相同的properties文件时，每个文件都会加载，但如果存在同名key会被覆盖
     *
     * @param resourceName properties文件名
     * @param key          the key
     * @param classLoader  the class loader
     * @return property
     */
    public static String getProperty(String resourceName, String key, ClassLoader classLoader) {
        return getProperty(resourceName, key, null, classLoader);
    }

    /**
     * 根据key获取properties文件的value值
     *
     * @param resourceName properties文件名
     * @param key          the key
     * @param defaultValue 不存在时返回的默认值
     * @return property
     */
    public static String getProperty(String resourceName, String key, String defaultValue) {
        return getProperty(resourceName, key, defaultValue, ClassUtils.getDefaultClassLoader());
    }

    /**
     * 根据key获取properties文件的value值
     *
     * @param resourceName properties文件名
     * @param key          the key
     * @param defaultValue 不存在时返回的默认值
     * @return property
     */
    public static String getProperty(String resourceName, String key, String defaultValue, ClassLoader classLoader) {
        Map<String, String> map = getProperties(resourceName, classLoader);
        String value = map.get(key);
        return StrUtils.isNotBlank(value) ? value : defaultValue;
    }

    /**
     * 加载yml文件属性map
     *
     * @param url the url
     * @return yml resource map
     */
    private static Map<String, String> getYmlResourceMap(URL url) {
        try {
            Map<String, String> propMap = new HashMap<>();
            Yaml yaml = new Yaml();
            Map<String, Object> yamlMap = yaml.load(url.openStream());
            yml2propMap(yamlMap, "", propMap);

            return propMap;
        } catch (IOException e) {
            throw new SonsureException("加载yml资源文件失败:" + url.getFile(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private static void yml2propMap(Map<String, Object> ymlMap, String parentKey, Map<String, String> resultMap) {

        for (Map.Entry<String, Object> entry : ymlMap.entrySet()) {
            StringBuilder sb = new StringBuilder();
            if (StrUtils.isNotBlank(parentKey)) {
                sb.append(parentKey).append(".");
            }
            sb.append(entry.getKey());

            Object value = entry.getValue();

            if (value instanceof Map) {
                //noinspection unchecked
                yml2propMap(((Map<String, Object>) value), sb.toString(), resultMap);
            } else {
                resultMap.put(sb.toString(), value == null ? "" : value.toString());
            }
        }
    }

    /**
     * 加载properties文件属性map
     *
     * @param url the url
     * @return properties resource map
     */
    private static Map<String, String> getPropertiesResourceMap(URL url) {
        try (InputStream is = url.openStream()) {
            Map<String, String> propMap = new HashMap<>();
            Properties prop = new Properties();
            prop.load(is);

            for (Map.Entry<Object, Object> entry : prop.entrySet()) {
                propMap.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }
            return propMap;
        } catch (IOException e) {
            throw new SonsureException("加载properties文件失败:" + url.getFile(), e);
        }

    }

    /**
     * 获取web容器的配置目录
     *
     * @param resource    the resource
     * @param classLoader the class loader
     * @return url [ ]
     */
    private static URL[] getResources(String resource, ClassLoader classLoader) {

        List<URL> urls = new ArrayList<>();
        try {
            //tomcat
            String resourcePath = System.getProperty("catalina.home") + "/conf";
            File file = new File(resourcePath, resource);
            if (!file.exists()) {
                //project
                resourcePath = System.getProperty("user.dir");
                file = new File(resourcePath, resource);
            }

            if (file.exists()) {
                URL url = file.toURI().toURL();
                urls.add(url);
            } else {
                Enumeration<URL> resources = classLoader.getResources(resource);
                while (resources.hasMoreElements()) {
                    URL url = resources.nextElement();
                    urls.add(url);
                }
            }
            return urls.toArray(new URL[]{});
        } catch (Exception e) {
            throw new SonsureException("加载resource文件失败", e);
        }
    }
}

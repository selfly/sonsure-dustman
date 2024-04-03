/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.common.spring.scan;

import com.sonsure.dumper.common.exception.SonsureException;
import com.sonsure.dumper.common.spring.BundlePathMatchingResourcePatternResolver;
import com.sonsure.dumper.common.spring.PathMatchingResourcePatternResolver;
import com.sonsure.dumper.common.spring.Resource;
import com.sonsure.dumper.common.spring.ResourcePatternResolver;
import com.sonsure.dumper.common.utils.FileIOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liyd
 */
public class ClassPathBeanScanner {

    public static final String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

    public static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    public static final String PATH_SEPARATOR = "/";

    public static final String PACKAGE_SEPARATOR = ".";

    protected static final ResourcePatternResolver RESOURCE_PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();

    /**
     * 扫描包下的所有class
     *
     * @param basePackage the base package
     * @return list
     */
    public static List<String> scanClasses(String basePackage) {
        return scanClasses(basePackage, RESOURCE_PATTERN_RESOLVER);
    }

    /**
     * 扫描包下的所有class
     *
     * @param basePackage the base package
     * @param classLoader the class loader
     * @return list
     */
    public static List<String> scanClasses(String basePackage, ClassLoader classLoader) {
        return scanClasses(basePackage, new BundlePathMatchingResourcePatternResolver(classLoader));
    }

    /**
     * 扫描包下的所有class
     *
     * @param basePackage             the base package
     * @param resourcePatternResolver the resource pattern resolver
     * @return list
     */
    public static List<String> scanClasses(String basePackage, ResourcePatternResolver resourcePatternResolver) {

        String basePackagePath = StringUtils.replace(basePackage, PACKAGE_SEPARATOR, PATH_SEPARATOR);
        String packageSearchPath = CLASSPATH_ALL_URL_PREFIX + basePackagePath + '/' + DEFAULT_RESOURCE_PATTERN;

        List<String> classes = new ArrayList<>();
        try {
            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
            for (Resource resource : resources) {
                try (InputStream is = resource.getInputStream()) {
                    ClassReader classReader = new ClassReader(FileIOUtils.toByteArray(is));
                    String className = StringUtils.replace(classReader.getClassName(), PATH_SEPARATOR, PACKAGE_SEPARATOR);
                    classes.add(className);
                }
            }
        } catch (IOException e) {
            throw new SonsureException("扫描class失败,package:" + basePackage, e);
        }
        return classes;
    }

}

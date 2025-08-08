/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.common.bean;

import com.sonsure.dumper.common.model.Page;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Java Bean 对象转换器
 *
 * @author liyd
 */
public class BeanKit {

    /**
     * 默认实例
     */
    private static final BeanKitInst INSTANCE = new BeanKitInst();

    public static BeanKitInst getInstance() {
        return new BeanKitInst();
    }

    /**
     * 拷贝属性值
     *
     * @param orig the orig
     * @param dest the dest
     */
    public static void copyFields(Object orig, Object dest) {
        INSTANCE.copyFields(orig, dest);
    }


    /**
     * map转为bean
     *
     * @param <T>       the type parameter
     * @param <R>       the type parameter
     * @param map       the map
     * @param beanClass the bean class
     * @return t r
     */
    public static <T extends Map<String, ?>, R> R underlineKeyMapToBean(T map, Class<R> beanClass) {
        return INSTANCE.mapToBean(map, beanClass, '_', null);
    }

    /**
     * map转为bean
     *
     * @param <T>       the type parameter
     * @param <R>       the type parameter
     * @param map       the map
     * @param beanClass the bean class
     * @param consumer  the consumer
     * @return t r
     */
    public static <T extends Map<String, ?>, R> R underlineKeyMapToBean(T map, Class<R> beanClass, BiConsumer<T, R> consumer) {
        return INSTANCE.mapToBean(map, beanClass, '_', consumer);
    }

    /**
     * map转为bean，key名为下划线命名方式
     *
     * @param <T>       the type parameter
     * @param <R>       the type parameter
     * @param mapList   the map list
     * @param beanClass the bean class
     * @return t list
     */
    public static <T extends Map<String, ?>, R> List<R> underlineKeyMapToBean(Collection<T> mapList, Class<R> beanClass) {
        return INSTANCE.mapToBean(mapList, beanClass, '_', null);
    }

    /**
     * map转为bean，key名为下划线命名方式
     *
     * @param <T>       the type parameter
     * @param <R>       the type parameter
     * @param mapList   the map list
     * @param beanClass the bean class
     * @param consumer  the consumer
     * @return t list
     */
    public static <T extends Map<String, ?>, R> List<R> underlineKeyMapToBean(Collection<T> mapList, Class<R> beanClass, BiConsumer<T, R> consumer) {
        return INSTANCE.mapToBean(mapList, beanClass, '_', consumer);
    }

    /**
     * map转为bean
     *
     * @param <T>       the type parameter
     * @param <R>       the type parameter
     * @param mapList   the map list
     * @param beanClass the bean class
     * @return t list
     */
    public static <T extends Map<String, ?>, R> List<R> mapToBean(Collection<T> mapList, Class<R> beanClass) {
        return INSTANCE.mapToBean(mapList, beanClass);
    }

    /**
     * map转为bean
     *
     * @param <T>       the type parameter
     * @param <R>       the type parameter
     * @param mapList   the map list
     * @param beanClass the bean class
     * @param delimiter the delimiter
     * @return t list
     */
    public static <T extends Map<String, ?>, R> List<R> mapToBean(Collection<T> mapList, Class<R> beanClass, Character delimiter) {
        return INSTANCE.mapToBean(mapList, beanClass, delimiter);
    }

    /**
     * map转为bean，key名为bean属性名
     *
     * @param <T>       the type parameter
     * @param <R>       the type parameter
     * @param mapList   the map list
     * @param beanClass the bean class
     * @param consumer  the consumer
     * @return t list
     */
    public static <T extends Map<String, ?>, R> List<R> mapToBean(Collection<T> mapList, Class<R> beanClass, BiConsumer<T, R> consumer) {
        return INSTANCE.mapToBean(mapList, beanClass, null, consumer);
    }

    /**
     * map转为bean，最后一个参数指定map中的key转换成骆驼命名法(JavaBean中惯用的属性命名)的分隔符,例如login_name转换成loginName,分隔符为下划线_
     * 指定了分隔符进行转换时如果属性不带分隔符会统一转成小写,毕竟JavaBean中除了常量外应该不会定义有大写的属性
     * 为空则不进行任何转换
     *
     * @param <T>       the type parameter
     * @param <R>       the type parameter
     * @param mapList   the map list
     * @param beanClass the bean class
     * @param delimiter the delimiter
     * @param consumer  the consumer
     * @return t list
     */
    public static <T extends Map<String, ?>, R> List<R> mapToBean(Collection<T> mapList, Class<R> beanClass, Character delimiter, BiConsumer<T, R> consumer) {
        return INSTANCE.mapToBean(mapList, beanClass, delimiter, consumer);
    }


    /**
     * map转为bean
     *
     * @param <T>       the type parameter
     * @param <R>       the type parameter
     * @param srcMap    the src map
     * @param beanClass the bean class
     * @return t r
     */
    public static <T extends Map<String, ?>, R> R mapToBean(T srcMap, Class<R> beanClass) {
        return INSTANCE.mapToBean(srcMap, beanClass);
    }

    /**
     * map转为bean
     *
     * @param <T>       the type parameter
     * @param <R>       the type parameter
     * @param srcMap    the src map
     * @param beanClass the bean class
     * @param consumer  the consumer
     * @return t r
     */
    public static <T extends Map<String, ?>, R> R mapToBean(T srcMap, Class<R> beanClass, BiConsumer<T, R> consumer) {
        return INSTANCE.mapToBean(srcMap, beanClass, consumer);
    }


    /**
     * map转为bean
     *
     * @param <T>       the type parameter
     * @param <R>       the type parameter
     * @param srcMap    the src map
     * @param beanClass the bean class
     * @param delimiter the delimiter¡¡¡¡¡
     * @return t r
     */
    public static <T extends Map<String, ?>, R> R mapToBean(T srcMap, Class<R> beanClass, Character delimiter) {
        return INSTANCE.mapToBean(srcMap, beanClass, delimiter);
    }

    /**
     * map转为bean
     *
     * @param <T>       the type parameter
     * @param <R>       the type parameter
     * @param srcMap    the src map
     * @param beanClass the bean class
     * @param delimiter the delimiter¡¡¡¡¡
     * @param consumer  the consumer
     * @return t r
     */
    public static <T extends Map<String, ?>, R> R mapToBean(T srcMap, Class<R> beanClass, Character delimiter, BiConsumer<T, R> consumer) {
        return INSTANCE.mapToBean(srcMap, beanClass, delimiter, consumer);
    }

    /**
     * bean转换成map
     *
     * @param obj the obj
     * @return map
     */
    public static Map<String, Object> beanToMap(Object obj) {
        return INSTANCE.beanToMap(obj);
    }

    /**
     * 拷贝分页对象
     *
     * @param <T>   the type parameter
     * @param <R>   the type parameter
     * @param clazz the clazz
     * @param page  the page
     * @return page page
     */
    public static <T, R> Page<R> copyPage(Class<R> clazz, Page<T> page) {
        return INSTANCE.copyPage(clazz, page);
    }

    /**
     * 拷贝分页对象
     *
     * @param <T>              the type parameter
     * @param <R>              the type parameter
     * @param clazz            the clazz
     * @param page             the page
     * @param ignoreProperties the ignore properties
     * @return page page
     */
    public static <T, R> Page<R> copyPage(Class<R> clazz, Page<T> page, String[] ignoreProperties) {
        return INSTANCE.copyPage(clazz, page, ignoreProperties);
    }

    /**
     * 拷贝分页对象
     *
     * @param <T>      the type parameter
     * @param <R>      the type parameter
     * @param clazz    the clazz
     * @param page     the page
     * @param consumer the consumer
     * @return page page
     */
    public static <T, R> Page<R> copyPage(Class<R> clazz, Page<T> page, BiConsumer<T, R> consumer) {
        return INSTANCE.copyPage(clazz, page, consumer);
    }

    /**
     * 拷贝分页对象
     *
     * @param <T>              the type parameter
     * @param <R>              the type parameter
     * @param clazz            the clazz
     * @param page             the page
     * @param ignoreProperties the ignore properties
     * @param consumer         the consumer
     * @return page page
     */
    public static <T, R> Page<R> copyPage(Class<R> clazz, Page<T> page, String[] ignoreProperties, BiConsumer<T, R> consumer) {
        return INSTANCE.copyPage(clazz, page, ignoreProperties, consumer);
    }

    /**
     * 拷贝属性
     *
     * @param <T>   the type parameter
     * @param <R>   the type parameter
     * @param clazz the clazz
     * @param list  the list
     * @return the page list
     */
    public static <T, R> List<R> copyProperties(Class<R> clazz, Collection<T> list) {
        return INSTANCE.copyProperties(clazz, list);
    }

    /**
     * 拷贝属性
     *
     * @param <T>              the type parameter
     * @param <R>              the type parameter
     * @param clazz            the clazz
     * @param list             the list
     * @param ignoreProperties the ignore properties
     * @return the page list
     */
    public static <T, R> List<R> copyProperties(Class<R> clazz, Collection<T> list, String[] ignoreProperties) {
        return INSTANCE.copyProperties(clazz, list, ignoreProperties);
    }

    /**
     * 拷贝属性
     *
     * @param <T>      the type parameter
     * @param <R>      the type parameter
     * @param clazz    the clazz
     * @param list     the list
     * @param consumer the consumer
     * @return the page list
     */
    public static <T, R> List<R> copyProperties(Class<R> clazz, Collection<T> list, BiConsumer<T, R> consumer) {
        return INSTANCE.copyProperties(clazz, list, consumer);
    }

    /**
     * 拷贝属性
     *
     * @param <T>              the type parameter
     * @param <R>              the type parameter
     * @param clazz            the clazz
     * @param list             the list
     * @param ignoreProperties the ignore properties
     * @param consumer         the consumer
     * @return the page list
     */
    public static <T, R> List<R> copyProperties(Class<R> clazz, Collection<T> list, String[] ignoreProperties, BiConsumer<T, R> consumer) {
        return INSTANCE.copyProperties(clazz, list, ignoreProperties, consumer);
    }

    /**
     * 单个对象拷贝
     *
     * @param <T>    the type parameter
     * @param <R>    the type parameter
     * @param target 目标对象
     * @param source 源对象
     * @return 转换后的目标对象 r
     */
    public static <T, R> R copyProperties(R target, T source) {
        return INSTANCE.copyProperties(target, source);
    }

    /**
     * 单个对象拷贝
     *
     * @param <T>              the type parameter
     * @param <R>              the type parameter
     * @param target           目标对象
     * @param source           源对象
     * @param ignoreProperties the ignore properties
     * @return 转换后的目标对象 r
     */
    public static <T, R> R copyProperties(R target, T source, String[] ignoreProperties) {
        return INSTANCE.copyProperties(target, source, ignoreProperties);
    }

    /**
     * 单个对象拷贝
     *
     * @param <T>      the type parameter
     * @param <R>      the type parameter
     * @param target   目标对象
     * @param source   源对象
     * @param consumer the consumer
     * @return 转换后的目标对象 r
     */
    public static <T, R> R copyProperties(R target, T source, BiConsumer<T, R> consumer) {
        return INSTANCE.copyProperties(target, source, consumer);
    }

    /**
     * 单个对象转换
     *
     * @param <T>              the type parameter
     * @param <R>              the type parameter
     * @param target           目标对象
     * @param source           源对象
     * @param ignoreProperties 需要过滤的属性
     * @param consumer         the consumer
     * @return 转换后的目标对象 r
     */
    public static <T, R> R copyProperties(R target, T source, String[] ignoreProperties, BiConsumer<T, R> consumer) {
        return INSTANCE.copyProperties(target, source, ignoreProperties, consumer);
    }

}

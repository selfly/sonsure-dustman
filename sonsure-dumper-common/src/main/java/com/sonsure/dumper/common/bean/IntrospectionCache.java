/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.common.bean;

import com.sonsure.dumper.common.exception.SonsureBeanException;
import com.sonsure.dumper.common.utils.StrUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.ref.Reference;
import java.util.*;

/**
 * JavaBean信息缓存
 * <p/>
 * User: liyd
 * Date: 13-5-8 下午6:36
 * version $Id: IntrospectionCache.java, v 0.1 Exp $
 */
public class IntrospectionCache {

    /**
     * 缓存的BeanInfo信息，一个Bean根据stopClass的不同可能存有多个BeanInfo
     * 子map根据stopClass进行缓存
     */
    public static final Map<Class<?>, Map<Class<?>, Object>> classCache = Collections
            .synchronizedMap(new WeakHashMap<Class<?>, Map<Class<?>, Object>>());

    /**
     * 类的属性信息，key为属性名
     */
    private final Map<String, PropertyDescriptor> propertyDescriptorCache;

    /**
     * Instantiates a new Introspection cache.
     *
     * @param beanClass the bean class
     * @param stopClass the stop class
     */
    private IntrospectionCache(Class<?> beanClass, Class<?> stopClass) {

        try {
            final BeanInfo beanInfo = stopClass == null ? Introspector.getBeanInfo(beanClass) : Introspector.getBeanInfo(beanClass, stopClass);
            // 从Introspector缓存立即移除，允许适当的垃圾收集
            // 我们在这里自行进行缓存,这是一个GC友好的方式，对比于IntrospectionCache，
            // Introspector没有使用弱引用的WeakHashMap
            Class<?> classToFlush = beanClass;
            do {
                Introspector.flushFromCaches(classToFlush);
                classToFlush = classToFlush.getSuperclass();
            } while (classToFlush != null);

            this.propertyDescriptorCache = new LinkedHashMap<>();
            // 此调用较慢，所以进行缓存
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor pd : pds) {
                if (Class.class.equals(beanClass) && "classLoader".equals(pd.getName())) {
                    // 忽略 Class.getClassLoader() 方法 - 没有人会需要绑定到那
                    continue;
                }
                this.propertyDescriptorCache.put(pd.getName(), pd);
            }

        } catch (IntrospectionException ex) {
            throw new SonsureBeanException("使用Introspector获取BeanInfo时出现异常", ex);
        }
    }


    /**
     * For class.
     *
     * @param beanClass the bean class
     * @return the introspection cache
     */
    public static IntrospectionCache forClass(Class<?> beanClass) {
        return forClass(beanClass, null);
    }

    /**
     * For class.
     *
     * @param beanClass the bean class
     * @param stopClass the stop class
     * @return the introspection cache
     */
    public static IntrospectionCache forClass(Class<?> beanClass, Class<?> stopClass) {

        IntrospectionCache introspectionCache;

        Map<Class<?>, Object> map = classCache.computeIfAbsent(beanClass, k -> new HashMap<>());
        //未指定stopClass时使用自身做为key，使用Object.class会和stopClass=Object.class冲突
        //例如第一次未指定stopClass默认到了Object.class，第二次到自身如果该类未继承任何类就是stopClass=Object.class，
        //命中了第一次的缓存从而会有object类中的属性而出错
        Class<?> stopCls = stopClass == null ? beanClass : stopClass;
        Object value = map.get(stopCls);
        if (value instanceof Reference) {
            @SuppressWarnings("rawtypes")
            Reference ref = (Reference) value;
            introspectionCache = (IntrospectionCache) ref.get();
        } else {
            introspectionCache = (IntrospectionCache) value;
        }
        if (introspectionCache == null) {
            introspectionCache = new IntrospectionCache(beanClass, stopClass);
            map.put(stopCls, introspectionCache);
        }
        return introspectionCache;
    }

    /**
     * Get property descriptors.
     *
     * @return the property descriptor [ ]
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        PropertyDescriptor[] pds = new PropertyDescriptor[this.propertyDescriptorCache.size()];
        int i = 0;
        for (PropertyDescriptor pd : this.propertyDescriptorCache.values()) {
            pds[i] = pd;
            i++;
        }
        return pds;
    }

    /**
     * Get property descriptor.
     *
     * @param name the name
     * @return the property descriptor
     */
    public PropertyDescriptor getPropertyDescriptor(String name) {

        PropertyDescriptor pd = this.propertyDescriptorCache.get(name);

        if (pd == null && StrUtils.isNotBlank(name)) {
            // Same lenient fallback checking as in PropertyTypeDescriptor...
            pd = this.propertyDescriptorCache.get(name.substring(0, 1).toLowerCase() + name.substring(1));
            if (pd == null) {
                pd = this.propertyDescriptorCache.get(name.substring(0, 1).toUpperCase() + name.substring(1));
            }
        }
        return pd;
    }

}

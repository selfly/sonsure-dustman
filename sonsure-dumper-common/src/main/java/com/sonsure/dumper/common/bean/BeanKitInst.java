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
import com.sonsure.dumper.common.model.Page;
import com.sonsure.dumper.common.utils.ClassUtils;
import com.sonsure.dumper.common.utils.NameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * @author liyd
 */
public class BeanKitInst {

    private static final Logger logger = LoggerFactory.getLogger(BeanKitInst.class);

    private static final String[] EMPTY_ARRAY = new String[0];

    private final List<TypeConverter> typeConverters = new ArrayList<>();

    public BeanKitInst() {
        typeConverters.add(new EnumConverter.BaseEnum2StringConverter());
        typeConverters.add(new EnumConverter.String2DynamicEnumConverter());
        typeConverters.add(new DateTimeConverter.Date2LocalDateTimeConverter());
        typeConverters.add(new DateTimeConverter.LocalDateTime2DateConverter());
        typeConverters.add(new NumberConverter());
    }

    /**
     * 拷贝属性值
     *
     * @param orig the orig
     * @param dest the dest
     */
    public void copyFields(Object orig, Object dest) {

        if (orig == null || dest == null) {
            return;
        }

        Field[] destBeanFields = ClassUtils.getBeanFields(dest.getClass());

        for (Field destField : destBeanFields) {
            try {
                if (Modifier.isFinal(destField.getModifiers())) {
                    continue;
                }
                Field origField = ClassUtils.getBeanField(orig.getClass(), destField.getName());
                if (origField == null) {
                    continue;
                }
                origField.setAccessible(true);
                Object value = origField.get(orig);
                destField.setAccessible(true);
                destField.set(dest, value);
            } catch (IllegalAccessException e) {
                throw new SonsureBeanException("Field拷贝失败:" + destField.getName(), e);
            }
        }
    }

    /**
     * bean转换成map
     *
     * @param obj the obj
     * @return map
     */
    public Map<String, Object> beanToMap(Object obj) {
        if (obj == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> map = new HashMap<>();
        PropertyDescriptor[] propertyDescriptors = ClassUtils.getPropertyDescriptors(obj.getClass());
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {

            if (propertyDescriptor.getReadMethod() == null) {
                continue;
            }

            Method readMethod = propertyDescriptor.getReadMethod();
            Object value = ClassUtils.invokeMethod(readMethod, obj);
            if (value == null) {
                continue;
            }
            map.put(propertyDescriptor.getName(), value);
        }
        return map;
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
    public <T extends Map<String, ?>, R> R underlineKeyMapToBean(T map, Class<R> beanClass) {
        return mapToBean(map, beanClass, '_', null);
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
    public <T extends Map<String, ?>, R> R underlineKeyMapToBean(T map, Class<R> beanClass, BiConsumer<T, R> consumer) {
        return mapToBean(map, beanClass, '_', consumer);
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
    public <T extends Map<String, ?>, R> List<R> underlineKeyMapToBean(Collection<T> mapList, Class<R> beanClass) {
        return mapToBean(mapList, beanClass, '_', null);
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
    public <T extends Map<String, ?>, R> List<R> underlineKeyMapToBean(Collection<T> mapList, Class<R> beanClass, BiConsumer<T, R> consumer) {
        return mapToBean(mapList, beanClass, '_', consumer);
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
    public <T extends Map<String, ?>, R> List<R> mapToBean(Collection<T> mapList, Class<R> beanClass) {

        if (mapList == null) {
            return Collections.emptyList();
        }
        List<R> beanList = new ArrayList<>(mapList.size());
        for (T t : mapList) {
            R r = mapToBean(t, beanClass, null, null);
            beanList.add(r);
        }
        return beanList;
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
    public <T extends Map<String, ?>, R> List<R> mapToBean(Collection<T> mapList, Class<R> beanClass, Character delimiter) {

        if (mapList == null) {
            return Collections.emptyList();
        }
        List<R> beanList = new ArrayList<>(mapList.size());
        for (T t : mapList) {
            R r = mapToBean(t, beanClass, delimiter, null);
            beanList.add(r);
        }
        return beanList;
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
    public <T extends Map<String, ?>, R> List<R> mapToBean(Collection<T> mapList, Class<R> beanClass, BiConsumer<T, R> consumer) {
        return mapToBean(mapList, beanClass, null, consumer);
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
    public <T extends Map<String, ?>, R> List<R> mapToBean(Collection<T> mapList, Class<R> beanClass, Character delimiter, BiConsumer<T, R> consumer) {

        if (mapList == null) {
            return Collections.emptyList();
        }
        List<R> beanList = new ArrayList<>(mapList.size());
        for (T t : mapList) {
            R r = mapToBean(t, beanClass, delimiter, consumer);
            beanList.add(r);
        }
        return beanList;
    }

    /**
     * map转为bean
     *
     * @param <T>       the type parameter
     * @param <R>       the type parameter
     * @param map       the map
     * @param beanClass the bean class
     * @return t t
     */
    public <T extends Map<String, ?>, R> R mapToBean(T map, Class<R> beanClass) {
        return mapToBean(map, beanClass, null, null);
    }

    /**
     * map转为bean
     *
     * @param <T>       the type parameter
     * @param <R>       the type parameter
     * @param map       the map
     * @param beanClass the bean class
     * @param delimiter the delimiter
     * @return t t
     */
    public <T extends Map<String, ?>, R> R mapToBean(T map, Class<R> beanClass, Character delimiter) {
        return mapToBean(map, beanClass, delimiter, null);
    }

    /**
     * map转为bean
     *
     * @param <T>       the type parameter
     * @param <R>       the type parameter
     * @param map       the map
     * @param beanClass the bean class
     * @param consumer  the consumer
     * @return t t
     */
    public <T extends Map<String, ?>, R> R mapToBean(T map, Class<R> beanClass, BiConsumer<T, R> consumer) {
        return mapToBean(map, beanClass, null, consumer);
    }

    /**
     * map转为bean，delimiter参数指定map中的key转换成骆驼命名法(JavaBean中惯用的属性命名)的分隔符,例如login_name转换成loginName,分隔符为下划线_
     * 指定了分隔符进行转换时如果属性不带分隔符会统一转成小写,毕竟JavaBean中除了常量外应该不会定义有大写的属性
     * 为空则不进行任何转换
     *
     * @param <T>       the type parameter
     * @param <R>       the type parameter
     * @param srcMap    the src map
     * @param beanClass the bean class
     * @param delimiter the delimiter
     * @param consumer  the consumer
     * @return t r
     */
    public <T extends Map<String, ?>, R> R mapToBean(T srcMap, Class<R> beanClass, Character delimiter, BiConsumer<T, R> consumer) {

        R bean = ClassUtils.newInstance(beanClass);
        for (Map.Entry<String, ?> entry : srcMap.entrySet()) {

            Object value = entry.getValue();

            if (value == null) {
                continue;
            }

            String name = entry.getKey();
            if (delimiter != null) {
                name = StringUtils.indexOf(name, delimiter) != -1 ? NameUtils.getCamelName(name, delimiter) : name
                        .toLowerCase();
            }
            PropertyDescriptor targetPd = ClassUtils.getPropertyDescriptor(beanClass, name);

            Method writeMethod;
            if (targetPd == null || (writeMethod = targetPd.getWriteMethod()) == null) {
                continue;
            }
            Class<?> targetGenericClass = ClassUtils.getBasicTypeClass(targetPd.getPropertyType());
            if (!targetGenericClass.isInstance(value)) {
                //自定义转换
                value = typeConvert(targetPd, value);
            }
            if (targetGenericClass.isInstance(value)) {
                ClassUtils.invokeMethod(writeMethod, bean, value);
            }
            if (consumer != null) {
                consumer.accept(srcMap, bean);
            }
        }
        return bean;
    }

    /**
     * Copy page page.
     *
     * @param <T>   the type parameter
     * @param <R>   the type parameter
     * @param clazz the clazz
     * @param page  the page
     * @return the page
     */
    public <T, R> Page<R> copyPage(Class<R> clazz, Page<T> page) {
        Page<R> resultPage = new Page<>(page.getPagination());
        List<R> list = this.copyProperties(clazz, page.getList());
        resultPage.setList(list);
        return resultPage;
    }

    /**
     * Copy page page.
     *
     * @param <T>              the type parameter
     * @param <R>              the type parameter
     * @param clazz            the clazz
     * @param page             the page
     * @param ignoreProperties the ignore properties
     * @return the page
     */
    public <T, R> Page<R> copyPage(Class<R> clazz, Page<T> page, String[] ignoreProperties) {
        Page<R> resultPage = new Page<>(page.getPagination());
        List<R> list = this.copyProperties(clazz, page.getList(), ignoreProperties);
        resultPage.setList(list);
        return resultPage;
    }

    /**
     * Copy page page.
     *
     * @param <T>      the type parameter
     * @param <R>      the type parameter
     * @param clazz    the clazz
     * @param page     the page
     * @param consumer the consumer
     * @return the page
     */
    public <T, R> Page<R> copyPage(Class<R> clazz, Page<T> page, BiConsumer<T, R> consumer) {
        Page<R> resultPage = new Page<>(page.getPagination());
        List<R> list = this.copyProperties(clazz, page.getList(), consumer);
        resultPage.setList(list);
        return resultPage;
    }

    /**
     * Copy page page.
     *
     * @param <T>              the type parameter
     * @param <R>              the type parameter
     * @param clazz            the clazz
     * @param page             the page
     * @param ignoreProperties the ignore properties
     * @param consumer         the consumer
     * @return the page
     */
    public <T, R> Page<R> copyPage(Class<R> clazz, Page<T> page, String[] ignoreProperties, BiConsumer<T, R> consumer) {
        Page<R> resultPage = new Page<>(page.getPagination());
        List<R> list = this.copyProperties(clazz, page.getList(), ignoreProperties, consumer);
        resultPage.setList(list);
        return resultPage;
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
    public <T, R> List<R> copyProperties(Class<R> clazz, Collection<T> list) {
        return copyProperties(clazz, list, EMPTY_ARRAY);
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
    public <T, R> List<R> copyProperties(Class<R> clazz, Collection<T> list, String[] ignoreProperties) {
        return copyProperties(clazz, list, ignoreProperties, (BiConsumer<T, R>) null);
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
    public <T, R> List<R> copyProperties(Class<R> clazz, Collection<T> list, BiConsumer<T, R> consumer) {
        return copyProperties(clazz, list, EMPTY_ARRAY, consumer);
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
    public <T, R> List<R> copyProperties(Class<R> clazz, Collection<T> list, String[] ignoreProperties, BiConsumer<T, R> consumer) {

        //返回的list列表
        List<R> resultList = new ArrayList<>();

        if (list == null || list.isEmpty()) {
            return resultList;
        }

        //循环调用转换单个对象
        for (T obj : list) {
            try {
                R r = clazz.newInstance();
                r = copyProperties(r, obj, ignoreProperties, consumer);
                resultList.add(r);
            } catch (Exception e) {
                throw new SonsureBeanException("列表转换失败", e);
            }
        }

        return resultList;
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
    public <T, R> R copyProperties(R target, T source) {
        return copyProperties(target, source, EMPTY_ARRAY, null);
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
    public <T, R> R copyProperties(R target, T source, BiConsumer<T, R> consumer) {
        return copyProperties(target, source, EMPTY_ARRAY, consumer);
    }

    /**
     * 单个对象转换
     *
     * @param <T>              the type parameter
     * @param <R>              the type parameter
     * @param target           目标对象
     * @param source           源对象
     * @param ignoreProperties 需要过滤的属性
     * @return 转换后的目标对象 r
     */
    public <T, R> R copyProperties(R target, T source, String[] ignoreProperties) {
        return copyProperties(target, source, ignoreProperties, null);
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
    public <T, R> R copyProperties(R target, T source, String[] ignoreProperties, BiConsumer<T, R> consumer) {

        //过滤的属性
        List<String> ignoreList = (ignoreProperties != null && ignoreProperties.length > 0) ? Arrays.asList(ignoreProperties) : Collections.emptyList();

        //拷贝相同的属性
        copySameProperties(target, source, ignoreList);

        if (consumer != null) {
            consumer.accept(source, target);
        }

        return target;
    }

    /**
     * 拷贝相同的属性
     *
     * @param target     the target
     * @param source     the source
     * @param ignoreList the ignore list
     */
    private void copySameProperties(Object target, Object source, List<String> ignoreList) {

        //获取目标对象属性信息
        PropertyDescriptor[] targetPds = ClassUtils.getPropertyDescriptors(target.getClass());

        for (PropertyDescriptor targetPd : targetPds) {

            final Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod == null || ignoreList.contains(targetPd.getName())) {
                continue;
            }
            PropertyDescriptor sourcePd = ClassUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
            if (sourcePd != null && sourcePd.getReadMethod() != null) {
                Method readMethod = sourcePd.getReadMethod();
                Object value = ClassUtils.invokeMethod(readMethod, source);
                if (value == null) {
                    continue;
                }
                //自定义转换
                Class<?> targetGenericClass = ClassUtils.getBasicTypeClass(targetPd.getPropertyType());
                if (!targetGenericClass.isInstance(value)) {
                    value = typeConvert(targetPd, value);
                }
                if (targetGenericClass.isInstance(value)) {
                    ClassUtils.invokeMethod(writeMethod, target, value);
                } else {
                    logger.debug("属性类型不一致,忽略 name:{}", targetPd.getName());
                }
            }
        }
    }

    /**
     * 用户自定义转换
     *
     * @param value the value
     * @return the object
     */
    private Object typeConvert(PropertyDescriptor targetPd, Object value) {

        if (value == null) {
            return null;
        }
        for (TypeConverter typeConverter : this.typeConverters) {
            if (typeConverter.supportSourceType(value.getClass()) && typeConverter.supportTargetType(targetPd.getPropertyType())) {
                return typeConverter.convert(targetPd, value);
            }
        }
        return value;
    }

    /**
     * 注册转换器
     *
     * @param converter the converter
     * @return the bean kit inst
     */
    public BeanKitInst registerConverter(TypeConverter converter) {
        this.typeConverters.add(converter);
        return this;
    }

    /**
     * 移除注册的转换器
     *
     * @param converter the converter
     * @return the bean kit inst
     */
    public BeanKitInst unregisterConverter(TypeConverter converter) {
        this.typeConverters.remove(converter);
        return this;
    }

    /**
     * 清空注册的转换器
     *
     * @return the bean kit inst
     */
    public BeanKitInst clearConverter() {
        this.typeConverters.clear();
        return this;
    }

    /**
     * 获取注册的转换器
     *
     * @return the converters
     */
    public List<TypeConverter> getConverters() {
        return this.typeConverters;
    }

}

/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.common.utils;

import com.sonsure.dumper.common.bean.BeanFieldCache;
import com.sonsure.dumper.common.bean.IntrospectionCache;
import com.sonsure.dumper.common.exception.SonsureBeanException;
import com.sonsure.dumper.common.exception.SonsureException;
import com.sonsure.dumper.common.model.BaseProperties;
import org.apache.commons.lang3.BooleanUtils;

import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.*;
import java.time.chrono.Chronology;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 类辅助
 * <p/>
 *
 * @author liyd
 * @date : 2/12/14
 */
public class ClassUtils {


    /**
     * 返回JavaBean所有属性的<code>PropertyDescriptor</code>
     *
     * @param beanClass the bean class
     * @return the property descriptor [ ]
     */
    public static PropertyDescriptor[] getSelfPropertyDescriptors(Class<?> beanClass) {

        return getPropertyDescriptors(beanClass, beanClass.getSuperclass());
    }

    /**
     * 返回JavaBean所有属性的<code>PropertyDescriptor</code>
     *
     * @param beanClass the bean class
     * @return the property descriptor [ ]
     */
    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> beanClass) {

        return getPropertyDescriptors(beanClass, null);
    }


    /**
     * 返回JavaBean所有属性的<code>PropertyDescriptor</code>
     *
     * @param beanClass the bean class
     * @param stopClass the stop class
     * @return the property descriptor [ ]
     */
    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> beanClass, Class<?> stopClass) {

        IntrospectionCache introspectionCache = IntrospectionCache.forClass(beanClass, stopClass);
        return introspectionCache.getPropertyDescriptors();
    }

    /**
     * 返回JavaBean给定JavaBean给定属性的 <code>PropertyDescriptors</code>
     *
     * @param beanClass    the bean class
     * @param propertyName the name of the property
     * @return the corresponding PropertyDescriptor, or <code>null</code> if none
     */
    public static PropertyDescriptor getPropertyDescriptor(Class<?> beanClass, String propertyName) {

        IntrospectionCache introspectionCache = IntrospectionCache.forClass(beanClass);
        return introspectionCache.getPropertyDescriptor(propertyName);
    }


    /**
     * 返回JavaBean的所有field
     *
     * @param beanClass the bean class
     * @return the fields
     */
    public static Field[] getSelfOrBaseFields(Class<?> beanClass) {
        Class<?> stopBaseClass = getStopBaseClass(beanClass);
        return getBeanFields(beanClass, stopBaseClass);
    }

    /**
     * 返回JavaBean的所有field
     *
     * @param beanClass the bean class
     * @return the fields
     */
    public static Field[] getBeanFields(Class<?> beanClass) {

        return getBeanFields(beanClass, null);
    }

    /**
     * 返回JavaBean的所有field
     *
     * @param beanClass the bean class
     * @return the fields
     */
    public static Map<String, Field> getBeanFieldMap(Class<?> beanClass) {

        Field[] beanFields = getBeanFields(beanClass, null);
        Map<String, Field> fieldMap = new HashMap<>();
        for (Field beanField : beanFields) {
            fieldMap.put(beanField.getName(), beanField);
        }
        return fieldMap;
    }


    /**
     * 返回JavaBean的所有field
     *
     * @param beanClass the bean class
     * @param stopClass the stop class
     * @return the fields
     */
    public static Field[] getBeanFields(Class<?> beanClass, Class<?> stopClass) {
        BeanFieldCache beanFieldCache = BeanFieldCache.forClass(beanClass, stopClass);
        return beanFieldCache.getBeanFields();
    }

    /**
     * 返回JavaBean给定名称的field
     *
     * @param beanClass the bean class
     * @param fieldName the name
     * @return the field, or <code>null</code> if none
     */
    public static Field getBeanField(Class<?> beanClass, String fieldName) {

        BeanFieldCache beanFieldCache = BeanFieldCache.forClass(beanClass);
        return beanFieldCache.getBeanField(fieldName);
    }

    /**
     * Sets bean field value.
     *
     * @param bean      the bean
     * @param fieldName the field name
     * @param value     the value
     */
    public static void setFieldValue(Object bean, String fieldName, Object value) {
        try {
            final Field beanField = getBeanField(bean.getClass(), fieldName);
            beanField.setAccessible(true);
            beanField.set(bean, value);
        } catch (IllegalAccessException e) {
            throw new SonsureBeanException("设置属性值失败", e);
        }
    }

    /**
     * 获取对象指定属性值
     *
     * @param obj       the obj
     * @param fieldName the field name
     * @return field value
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        if (obj == null) {
            return null;
        }
        try {
            final Field beanField = getBeanField(obj.getClass(), fieldName);
            beanField.setAccessible(true);
            return beanField.get(obj);
        } catch (IllegalAccessException e) {
            throw new SonsureBeanException("获取属性值失败", e);
        }
    }

    /**
     * 获取对象指定属性值
     *
     * @param obj       the obj
     * @param fieldName the field name
     * @return property value
     */
    public static Object getPropertyValue(Object obj, String fieldName) {
        if (obj == null) {
            return null;
        }
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(obj.getClass(), fieldName);
        Method readMethod = propertyDescriptor.getReadMethod();
        return invokeMethod(readMethod, obj);
    }

    /**
     * Sets property value.
     *
     * @param obj       the obj
     * @param fieldName the field name
     * @param value     the value
     */
    public static void setPropertyValue(Object obj, String fieldName, Object value) {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(obj.getClass(), fieldName);
        Method writeMethod = propertyDescriptor.getWriteMethod();
        invokeMethod(writeMethod, obj, value);
    }

    /**
     * Gets stop base class.
     *
     * @param cls the cls
     * @return the stop base class
     */
    public static Class<?> getStopBaseClass(Class<?> cls) {
        Class<?> stopClass = cls.getSuperclass();
        while (stopClass.getAnnotation(BaseProperties.class) != null) {
            stopClass = stopClass.getSuperclass();
        }
        return stopClass;
    }

    /**
     * Gets bean prop map.
     *
     * @param object           the object
     * @param ignoreAnnotation the ignore annotation
     * @param onlySelfOrBase   the only self or base
     * @return the bean prop map
     */
    public static Map<String, Object> getBeanPropMap(Object object, Class<? extends Annotation> ignoreAnnotation, boolean onlySelfOrBase) {
        Class<?> stopClass = null;
        if (onlySelfOrBase) {
            stopClass = getStopBaseClass(object.getClass());
        }
        Map<String, Object> propMap = new HashMap<>();
        PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(object.getClass(), stopClass);
        if (propertyDescriptors == null) {
            return propMap;
        }
        for (PropertyDescriptor pd : propertyDescriptors) {
            Method readMethod = pd.getReadMethod();
            if (readMethod == null || (ignoreAnnotation != null && readMethod.getAnnotation(ignoreAnnotation) != null)) {
                continue;
            }

            Object value = invokeMethod(readMethod, object);
            propMap.put(pd.getName(), value);
        }
        return propMap;
    }

    /**
     * Gets bean prop map.
     *
     * @param object the object
     * @return the bean prop map
     */
    public static Map<String, Object> getBeanPropMap(Object object) {
        return getBeanPropMap(object, null, false);
    }

    /**
     * Gets bean prop map.
     *
     * @param object           the object
     * @param ignoreAnnotation the ignore annotation
     * @return the bean prop map
     */
    public static Map<String, Object> getBeanPropMap(Object object, Class<? extends Annotation> ignoreAnnotation) {
        return getBeanPropMap(object, ignoreAnnotation, false);
    }

    /**
     * bean属性转换为map
     *
     * @param object           the object
     * @param ignoreAnnotation the ignore annotation
     * @return self bean prop map
     */
    public static Map<String, Object> getSelfBeanPropMap(Object object, Class<? extends Annotation> ignoreAnnotation) {
        return getBeanPropMap(object, ignoreAnnotation, true);
    }

    /**
     * Gets method.
     *
     * @param beanClazz  the bean clazz
     * @param methodName the method name
     * @param paramTypes the param types
     * @return the method
     */
    public static Method getMethod(Class<?> beanClazz, String methodName, Class<?>[] paramTypes) {
        try {
            return beanClazz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            throw new SonsureException("获取Method失败:" + methodName, e);
        }
    }

    /**
     * invokeMethod
     *
     * @param method the method
     * @param bean   the bean
     * @param value  the value
     * @return the object
     */
    public static Object invokeMethod(Method method, Object bean, Object... value) {
        try {
            methodAccessible(method);
            Object[] parameters = new Object[value.length];
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < value.length; i++) {
                if (parameterTypes[i] == value[i].getClass()) {
                    parameters[i] = value[i];
                } else if (parameterTypes[i] == Boolean.class || parameterTypes[i] == boolean.class) {
                    parameters[i] = BooleanUtils.toBoolean(String.valueOf(value[i]));
                } else if (parameterTypes[i] == Integer.class || parameterTypes[i] == int.class) {
                    parameters[i] = Integer.valueOf(String.valueOf(value[i]));
                } else if (parameterTypes[i] == Long.class || parameterTypes[i] == long.class) {
                    parameters[i] = Long.valueOf(String.valueOf(value[i]));
                } else {
                    parameters[i] = value[i];
                }
            }
            return method.invoke(bean, parameters);
        } catch (Exception e) {
            throw new SonsureException("执行invokeMethod失败:" + (method == null ? "null" : method.getName()), e);
        }
    }

    /**
     * invokeMethod
     *
     * @param method the method
     * @param bean   the bean
     * @return the object
     */
    public static Object invokeMethod(Method method, Object bean) {
        try {
            methodAccessible(method);
            return method.invoke(bean);
        } catch (Exception e) {
            throw new SonsureException("执行invokeMethod失败:" + (method == null ? "null" : method.getName()), e);
        }
    }

    /**
     * 设置method访问权限
     *
     * @param method the method
     */
    public static void methodAccessible(Method method) {
        if (!Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
            method.setAccessible(true);
        }
    }

    /**
     * 初始化实例
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @return object t
     */
    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new SonsureException("根据class创建实例失败:" + (clazz == null ? "null" : clazz.getName()), e);
        }
    }

    /**
     * 初始化实例
     *
     * @param clazz the clazz
     * @return object
     */
    public static Object newInstance(String clazz) {

        try {
            Class<?> loadClass = getDefaultClassLoader().loadClass(clazz);
            return loadClass.newInstance();
        } catch (Exception e) {
            throw new SonsureException("根据class创建实例失败:" + clazz, e);
        }
    }

    /**
     * 加载类
     *
     * @param clazz the clazz
     * @return class
     */
    public static Class<?> loadClass(String clazz) {
        try {
            return getDefaultClassLoader().loadClass(clazz);
        } catch (Exception e) {
            throw new SonsureException("根据class名称加载class失败:" + clazz, e);
        }
    }

    public static Class<?> getBasicTypeClass(Class<?> cls) {
        if (byte.class.equals(cls)) {
            return Byte.class;
        } else if (short.class.equals(cls)) {
            return Short.class;
        } else if (int.class.equals(cls)) {
            return Integer.class;
        } else if (long.class.equals(cls)) {
            return Long.class;
        } else if (float.class.equals(cls)) {
            return Float.class;
        } else if (double.class.equals(cls)) {
            return Double.class;
        } else if (char.class.equals(cls)) {
            return Character.class;
        } else if (boolean.class.equals(cls)) {
            return Boolean.class;
        } else {
            return cls;
        }
    }

    public static boolean isJavaBeanType(Class<?> clazz) {
        return !isPrimitiveOrWrapper(clazz) && !isCommonNonBeanType(clazz);
    }

    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz.equals(Integer.class) ||
                clazz.equals(Double.class) ||
                clazz.equals(Long.class) ||
                clazz.equals(Float.class) ||
                clazz.equals(Boolean.class) ||
                clazz.equals(Character.class) ||
                clazz.equals(Short.class) ||
                clazz.equals(Byte.class);
    }

    public static boolean isCommonNonBeanType(Class<?> clazz) {
        return clazz.equals(String.class) ||
                clazz.equals(LocalDateTime.class) ||
                clazz.equals(LocalDate.class) ||
                clazz.equals(LocalTime.class) ||
                clazz.equals(Timestamp.class) ||
                clazz.equals(Date.class) ||
                clazz.equals(UUID.class) ||
                clazz.equals(BigDecimal.class) ||
                clazz.equals(BigInteger.class) ||
                clazz.equals(Path.class) ||
                clazz.equals(File.class) ||
                clazz.equals(InputStream.class) ||
                clazz.equals(OutputStream.class) ||
                clazz.equals(Reader.class) ||
                clazz.equals(Writer.class) ||
                clazz.equals(URL.class) ||
                clazz.equals(URI.class) ||
                clazz.equals(Currency.class) ||
                clazz.equals(Locale.class) ||
                clazz.equals(TimeZone.class) ||
                clazz.equals(Duration.class) ||
                clazz.equals(Period.class) ||
                clazz.equals(ZoneId.class) ||
                clazz.equals(ZoneOffset.class) ||
                clazz.equals(ChronoUnit.class) ||
                clazz.equals(Chronology.class) ||
                clazz.equals(Month.class) ||
                clazz.equals(MonthDay.class) ||
                clazz.equals(Year.class) ||
                clazz.equals(YearMonth.class) ||
                clazz.equals(DayOfWeek.class);
    }

    private static void getSuperTypes(Class<?> cls, List<Class<?>> superTypes) {
        getInterfaces(cls, superTypes);
        getSuperClasses(cls, superTypes);
    }

    private static void getSuperClasses(Class<?> cls, List<Class<?>> superClasses) {
        final Class<?> superclass = cls.getSuperclass();
        if (superclass != null) {
            getSuperClasses(superclass, superClasses);
            superClasses.add(superclass);
        }
    }

    public static void getInterfaces(Class<?> cls, List<Class<?>> interfaces) {
        final Class<?>[] ifs = cls.getInterfaces();
        for (Class<?> anIf : ifs) {
            getInterfaces(anIf, interfaces);
            interfaces.add(anIf);
        }
    }

    /**
     * 当前线程的classLoader
     *
     * @return default class loader
     */
    public static ClassLoader getDefaultClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}

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
import com.sonsure.dumper.common.exception.SonsureCommonsException;
import com.sonsure.dumper.common.exception.SonsureException;
import com.sonsure.dumper.common.model.BaseProperties;
import lombok.extern.slf4j.Slf4j;

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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.*;
import java.time.chrono.Chronology;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * 类辅助
 * <p/>
 *
 * @author liyd
 * @since : 2/12/14
 */
@Slf4j
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
    public static Map<String, Object> getBeanPropMap(Object object, Class<? extends Annotation> ignoreAnnotation,
                                                     boolean onlySelfOrBase) {
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
            if (readMethod == null
                    || (ignoreAnnotation != null && readMethod.getAnnotation(ignoreAnnotation) != null)) {
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
    public static Method getMethod(Class<?> beanClazz, String methodName, Class<?>... paramTypes) {
        try {
            return beanClazz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            throw new SonsureException("获取Method失败:" + methodName, e);
        }
    }

    /**
     * Gets declared method.
     *
     * @param beanClazz  the bean clazz
     * @param methodName the method name
     * @param paramTypes the param types
     * @return the declared method
     */
    public static Method getDeclaredMethod(Class<?> beanClazz, String methodName, Class<?>... paramTypes) {
        try {
            return beanClazz.getDeclaredMethod(methodName, paramTypes);
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
                    parameters[i] = Boolean.parseBoolean(String.valueOf(value[i]));
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
            throw new SonsureException("根据class创建实例失败", e);
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

    public static void getInterfaces(Class<?> cls, List<Class<?>> interfaces) {
        final Class<?>[] ifs = cls.getInterfaces();
        for (Class<?> anIf : ifs) {
            getInterfaces(anIf, interfaces);
            interfaces.add(anIf);
        }
    }

    /**
     * 扫描指定包下的所有类（包括子包）
     * <p>
     * 功能特性：
     * <ul>
     * <li>支持从文件系统扫描类</li>
     * <li>支持从jar包扫描类</li>
     * <li>自动过滤内部类和匿名类（包含$符号的类）</li>
     * <li>使用懒加载模式（Class.forName第二个参数为false），不会初始化类</li>
     * <li>自动去重，返回LinkedHashSet保证顺序</li>
     * </ul>
     * <p>
     * 使用示例：
     *
     * <pre>{@code
     * // 扫描指定包及其子包下的所有类
     * List<Class<?>> classes = ClassUtils.scanClasses("com.example.service");
     *
     * // 只扫描当前包，不扫描子包
     * List<Class<?>> classes = ClassUtils.scanClasses("com.example.service", false);
     *
     * // 过滤特定类型的类
     * List<Class<?>> serviceClasses = ClassUtils.scanClasses("com.example.service")
     *         .stream()
     *         .filter(cls -> cls.isAnnotationPresent(Service.class))
     *         .collect(Collectors.toList());
     * }</pre>
     *
     * @param packageName 包名，如: com.example.service
     * @return 扫描到的类列表，不会返回null
     * @throws IllegalArgumentException 如果包名为空
     */
    public static List<Class<?>> scanClasses(String packageName) {
        return scanClasses(packageName, true);
    }

    /**
     * 扫描指定包下的类
     *
     * @param packageName 包名，如: com.example.service
     * @param recursive   是否递归扫描子包，true-扫描子包，false-只扫描当前包
     * @return 扫描到的类列表 ，不会返回null
     * @throws IllegalArgumentException 如果包名为空
     */
    public static List<Class<?>> scanClasses(String packageName, boolean recursive) {
        if (packageName == null || packageName.trim().isEmpty()) {
            throw new IllegalArgumentException("包名不能为空");
        }
        ClassLoader classLoader = getDefaultClassLoader();
        Set<Class<?>> classes = new LinkedHashSet<>();
        try {
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                String protocol = resource.getProtocol();
                try {
                    if ("file".equals(protocol)) {
                        // 处理文件系统中的类
                        scanClassesFromFileSystem(resource, packageName, recursive, classes);
                    } else if ("jar".equals(protocol)) {
                        // 处理jar包中的类
                        scanClassesFromJar(resource, packageName, recursive, classes);
                    } else {
                        log.debug("不支持的协议类型: {}, 资源: {}", protocol, resource);
                    }
                } catch (Exception e) {
                    log.warn("扫描资源失败: {}, 错误: {}", resource, e.getMessage());
                }
            }
        } catch (IOException e) {
            log.error("扫描失败:{}", packageName, e);
            throw new SonsureCommonsException("扫描失败", e);
        }
        return new ArrayList<>(classes);
    }

    /**
     * 从文件系统扫描类
     */
    private static void scanClassesFromFileSystem(URL resource, String packageName,
                                                  boolean recursive, Set<Class<?>> classes) {
        try {
            String filePath = URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8.name());
            Path directory = Paths.get(filePath);

            if (!Files.exists(directory) || !Files.isDirectory(directory)) {
                return;
            }

            int maxDepth = recursive ? Integer.MAX_VALUE : 1;

            try (Stream<Path> paths = Files.walk(directory, maxDepth)) {
                paths.filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".class"))
                        .forEach(path -> {
                            String className = toClassName(directory, path, packageName);
                            loadClass(className, classes);
                        });
            }
        } catch (Exception e) {
            log.warn("文件系统扫描失败: {}, 错误: {}", resource, e.getMessage());
        }
    }

    /**
     * 从jar包扫描类
     */
    private static void scanClassesFromJar(URL resource, String packageName,
                                           boolean recursive, Set<Class<?>> classes) {
        try {
            String jarPath = resource.getPath();
            // 提取jar文件路径: jar:file:/path/to/jar.jar!/package/path
            int separatorIndex = jarPath.indexOf("!/");
            if (separatorIndex == -1) {
                return;
            }

            // 去掉 "file:"
            String jarFilePath = jarPath.substring(5, separatorIndex);
            jarFilePath = URLDecoder.decode(jarFilePath, StandardCharsets.UTF_8.name());

            String packagePath = packageName.replace('.', '/');

            try (JarFile jarFile = new JarFile(jarFilePath)) {
                Enumeration<JarEntry> entries = jarFile.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();

                    // 过滤：1.是class文件 2.在指定包路径下 3.不是目录
                    if (!entry.isDirectory() && entryName.endsWith(".class") &&
                            entryName.startsWith(packagePath)) {

                        // 检查是否递归扫描子包
                        if (!recursive) {
                            String subPath = entryName.substring(packagePath.length() + 1);
                            if (subPath.contains("/")) {
                                continue; // 跳过子包
                            }
                        }

                        // 转换为类名: com/example/MyClass.class -> com.example.MyClass
                        String className = entryName.substring(0, entryName.length() - 6)
                                .replace('/', '.');
                        loadClass(className, classes);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Jar包扫描失败: {}", resource, e);
        }
    }

    /**
     * 将文件路径转换为类名
     */
    private static String toClassName(Path baseDir, Path classFile, String basePackage) {
        Path relativePath = baseDir.relativize(classFile);
        String pathStr = relativePath.toString().replace(File.separatorChar, '.');
        // 移除 .class 后缀
        String className = pathStr.substring(0, pathStr.length() - 6);
        return basePackage + "." + className;
    }

    /**
     * 加载类并添加到集合中
     */
    private static void loadClass(String className, Set<Class<?>> classes) {
        try {
            // 过滤掉内部类和匿名类（可选）
            if (className.contains("$")) {
                return;
            }

            Class<?> clazz = Class.forName(className, false, getDefaultClassLoader());
            classes.add(clazz);
        } catch (ClassNotFoundException | LinkageError e) {
            // 跳过无法加载的类（LinkageError包含NoClassDefFoundError等）
            log.debug("类加载失败: {}, 原因: {}", className, e.getMessage());
        } catch (Exception e) {
            log.warn("类加载异常: {}, 错误: {}", className, e.getMessage());
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

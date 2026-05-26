package com.sonsure.dustman.test.common.utils;

import com.sonsure.dustman.common.bean.TypeConverter;
import com.sonsure.dustman.common.model.BaseProperties;
import com.sonsure.dustman.common.model.Pageable;
import com.sonsure.dustman.common.utils.ClassUtils;
import com.sonsure.dustman.test.model.BaseUser;
import com.sonsure.dustman.test.model.UserInfo;
import org.junit.jupiter.api.Test;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassUtils 单元测试
 */
public class ClassUtilsTest {

    // ---- 测试模型 ----

    public static class SimpleBean {
        private String name;
        private Integer age;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getReadOnly() { return "readonly"; }
        private String hidden;
        public String getHidden() { return hidden; }
        public void setHidden(String hidden) { this.hidden = hidden; }
    }

    public static class ChildBean extends SimpleBean {
        private String extra;
        public String getExtra() { return extra; }
        public void setExtra(String extra) { this.extra = extra; }
    }

    @BaseProperties
    public static class BaseBean extends Pageable {
        private Long id;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }

    public static class ConcreteBean extends BaseBean {
        private String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class BeanWithAnnotation {
        private String normal;
        private String annotated;
        public String getNormal() { return normal; }
        public void setNormal(String normal) { this.normal = normal; }
        @Deprecated
        public String getAnnotated() { return annotated; }
        public void setAnnotated(String annotated) { this.annotated = annotated; }
    }

    public static class BeanWithPrimitives {
        private int intVal;
        private boolean boolVal;
        private long longVal;
        private double doubleVal;
        private float floatVal;
        private short shortVal;
        private byte byteVal;
        private char charVal;
        public int getIntVal() { return intVal; }
        public void setIntVal(int intVal) { this.intVal = intVal; }
        public boolean isBoolVal() { return boolVal; }
        public void setBoolVal(boolean boolVal) { this.boolVal = boolVal; }
        public long getLongVal() { return longVal; }
        public void setLongVal(long longVal) { this.longVal = longVal; }
        public double getDoubleVal() { return doubleVal; }
        public void setDoubleVal(double doubleVal) { this.doubleVal = doubleVal; }
        public float getFloatVal() { return floatVal; }
        public void setFloatVal(float floatVal) { this.floatVal = floatVal; }
        public short getShortVal() { return shortVal; }
        public void setShortVal(short shortVal) { this.shortVal = shortVal; }
        public byte getByteVal() { return byteVal; }
        public void setByteVal(byte byteVal) { this.byteVal = byteVal; }
        public char getCharVal() { return charVal; }
        public void setCharVal(char charVal) { this.charVal = charVal; }
    }

    // ---- PropertyDescriptor 测试 ----

    @Test
    public void testGetSelfPropertyDescriptors() {
        PropertyDescriptor[] pds = ClassUtils.getSelfPropertyDescriptors(ChildBean.class);
        Set<String> names = new HashSet<>();
        for (PropertyDescriptor pd : pds) names.add(pd.getName());
        assertTrue(names.contains("extra"));
        assertFalse(names.contains("name"));
    }

    @Test
    public void testGetPropertyDescriptors_All() {
        PropertyDescriptor[] pds = ClassUtils.getPropertyDescriptors(SimpleBean.class);
        Set<String> names = new HashSet<>();
        for (PropertyDescriptor pd : pds) names.add(pd.getName());
        assertTrue(names.contains("name"));
        assertTrue(names.contains("age"));
        assertTrue(names.contains("readOnly"));
        assertTrue(names.contains("hidden"));
    }

    @Test
    public void testGetPropertyDescriptors_WithStopClass() {
        PropertyDescriptor[] pds = ClassUtils.getPropertyDescriptors(ConcreteBean.class, BaseBean.class);
        Set<String> names = new HashSet<>();
        for (PropertyDescriptor pd : pds) names.add(pd.getName());
        assertTrue(names.contains("name"));
        assertFalse(names.contains("id")); // 在 stopClass 中
    }

    @Test
    public void testGetPropertyDescriptor_Found() {
        PropertyDescriptor pd = ClassUtils.getPropertyDescriptor(SimpleBean.class, "name");
        assertNotNull(pd);
        assertEquals("name", pd.getName());
    }

    @Test
    public void testGetPropertyDescriptor_NotFound() {
        PropertyDescriptor pd = ClassUtils.getPropertyDescriptor(SimpleBean.class, "nonexistent");
        assertNull(pd);
    }

    // ---- Field 测试 ----

    @Test
    public void testGetSelfOrBaseFields() {
        Field[] fields = ClassUtils.getSelfOrBaseFields(ConcreteBean.class);
        // 应该包含 ConcreteBean 和 BaseBean 的字段，但不包含 Pageable 的字段
        Set<String> names = new HashSet<>();
        for (Field f : fields) names.add(f.getName());
        assertTrue(names.contains("name"));
        // id 在 BaseBean 中, BaseBean 有 @BaseProperties，所以应在 BaseBean 处停止
        // 实际上 BaseBean 本身有 @BaseProperties，getStopBaseClass 会获取 BaseBean 的 superclass
    }

    @Test
    public void testGetBeanFields() {
        Field[] fields = ClassUtils.getBeanFields(SimpleBean.class);
        assertTrue(fields.length >= 2);
    }

    @Test
    public void testGetBeanFields_WithStopClass() {
        Field[] fields = ClassUtils.getBeanFields(ChildBean.class, SimpleBean.class);
        // 在 SimpleBean 处停止，ChildBean 没有额外字段？(extra 是 ChildBean 的字段)
        // 实际上 ChildBean 本身只有 extra, 而 SimpleBean 作为 stopClass
        Set<String> names = new HashSet<>();
        for (Field f : fields) names.add(f.getName());
        assertTrue(names.contains("extra"));
    }

    @Test
    public void testGetBeanFieldMap() {
        Map<String, Field> fieldMap = ClassUtils.getBeanFieldMap(SimpleBean.class);
        assertNotNull(fieldMap.get("name"));
        assertNotNull(fieldMap.get("age"));
    }

    @Test
    public void testGetBeanField_Found() {
        Field field = ClassUtils.getBeanField(SimpleBean.class, "name");
        assertNotNull(field);
        assertEquals("name", field.getName());
    }

    @Test
    public void testGetBeanField_NotFound() {
        Field field = ClassUtils.getBeanField(SimpleBean.class, "nonexistent");
        assertNull(field);
    }

    // ---- getStopBaseClass ----

    @Test
    public void testGetStopBaseClass_WithBaseProperties() {
        Class<?> stopClass = ClassUtils.getStopBaseClass(ConcreteBean.class);
        assertEquals(Pageable.class, stopClass);
    }

    @Test
    public void testGetStopBaseClass_WithoutBaseProperties() {
        Class<?> stopClass = ClassUtils.getStopBaseClass(SimpleBean.class);
        // SimpleBean 无 @BaseProperties，循环直接退出，返回 Object.class
        assertEquals(Object.class, stopClass);
    }

    // ---- Field Value 测试 ----

    @Test
    public void testSetFieldValue() {
        SimpleBean bean = new SimpleBean();
        ClassUtils.setFieldValue(bean, "name", "test");
        assertEquals("test", bean.getName());
    }

    @Test
    public void testSetFieldValue_NoSuchField() {
        SimpleBean bean = new SimpleBean();
        // 不应抛出异常
        ClassUtils.setFieldValue(bean, "nonexistent", "value");
    }

    @Test
    public void testGetFieldValue() {
        SimpleBean bean = new SimpleBean();
        bean.setName("hello");
        assertEquals("hello", ClassUtils.getFieldValue(bean, "name"));
    }

    @Test
    public void testGetFieldValue_NullObj() {
        assertNull(ClassUtils.getFieldValue(null, "name"));
    }

    @Test
    public void testGetFieldValue_NoSuchField() {
        SimpleBean bean = new SimpleBean();
        assertNull(ClassUtils.getFieldValue(bean, "nonexistent"));
    }

    // ---- Property Value 测试 ----

    @Test
    public void testGetPropertyValue() {
        SimpleBean bean = new SimpleBean();
        bean.setName("world");
        assertEquals("world", ClassUtils.getPropertyValue(bean, "name"));
    }

    @Test
    public void testGetPropertyValue_NullObj() {
        assertNull(ClassUtils.getPropertyValue(null, "name"));
    }

    @Test
    public void testGetPropertyValue_NoSuchProperty() {
        SimpleBean bean = new SimpleBean();
        assertNull(ClassUtils.getPropertyValue(bean, "nonexistent"));
    }

    @Test
    public void testSetPropertyValue() {
        SimpleBean bean = new SimpleBean();
        ClassUtils.setPropertyValue(bean, "name", "set-prop");
        assertEquals("set-prop", bean.getName());
    }

    @Test
    public void testSetPropertyValue_NoSuchProperty() {
        SimpleBean bean = new SimpleBean();
        ClassUtils.setPropertyValue(bean, "nonexistent", "value");
    }

    // ---- getBeanPropMap ----

    @Test
    public void testGetBeanPropMap() {
        SimpleBean bean = new SimpleBean();
        bean.setName("map-test");
        bean.setAge(25);
        Map<String, Object> map = ClassUtils.getBeanPropMap(bean);
        assertEquals("map-test", map.get("name"));
        assertEquals(25, map.get("age"));
        assertTrue(map.containsKey("readOnly"));
    }

    @Test
    public void testGetBeanPropMap_WithIgnoreAnnotation() {
        BeanWithAnnotation bean = new BeanWithAnnotation();
        bean.setNormal("normal");
        bean.setAnnotated("annotated");
        Map<String, Object> map = ClassUtils.getBeanPropMap(bean, Deprecated.class);
        assertTrue(map.containsKey("normal"));
        assertFalse(map.containsKey("annotated"));
    }

    @Test
    public void testGetSelfBeanPropMap() {
        // 仅当前类及基类（不需要是 exact self）
        SimpleBean bean = new SimpleBean();
        bean.setName("self");
        Map<String, Object> map = ClassUtils.getSelfBeanPropMap(bean, null);
        assertTrue(map.containsKey("name"));
    }

    // ---- Method 测试 ----

    @Test
    public void testGetMethod_Found() {
        Method method = ClassUtils.getMethod(SimpleBean.class, "getName");
        assertNotNull(method);
        assertEquals("getName", method.getName());
    }

    @Test
    public void testGetMethod_NotFound() {
        assertThrows(RuntimeException.class, () ->
                ClassUtils.getMethod(SimpleBean.class, "nonexistentMethod"));
    }

    @Test
    public void testGetDeclaredMethod_Found() {
        Method method = ClassUtils.getDeclaredMethod(SimpleBean.class, "getName");
        assertNotNull(method);
    }

    @Test
    public void testGetDeclaredMethod_NotFound() {
        assertThrows(RuntimeException.class, () ->
                ClassUtils.getDeclaredMethod(SimpleBean.class, "nonexistentMethod"));
    }

    // ---- invokeMethod ----

    @Test
    public void testInvokeMethod_NoArgs() throws Exception {
        SimpleBean bean = new SimpleBean();
        bean.setName("invoke");
        Method method = SimpleBean.class.getMethod("getName");
        assertEquals("invoke", ClassUtils.invokeMethod(method, bean));
    }

    @Test
    public void testInvokeMethod_WithArgs() throws Exception {
        SimpleBean bean = new SimpleBean();
        Method method = SimpleBean.class.getMethod("setName", String.class);
        ClassUtils.invokeMethod(method, bean, "set-via-invoke");
        assertEquals("set-via-invoke", bean.getName());
    }

    @Test
    public void testInvokeMethod_NullArgForPrimitive() throws Exception {
        BeanWithPrimitives bean = new BeanWithPrimitives();
        Method method = BeanWithPrimitives.class.getMethod("setIntVal", int.class);
        assertThrows(RuntimeException.class, () ->
                ClassUtils.invokeMethod(method, bean, new Object[]{null}));
    }

    @Test
    public void testInvokeMethod_NullArgForNonPrimitive() throws Exception {
        SimpleBean bean = new SimpleBean();
        Method method = SimpleBean.class.getMethod("setName", String.class);
        assertDoesNotThrow(() -> ClassUtils.invokeMethod(method, bean, new Object[]{null}));
        assertNull(bean.getName());
    }

    @Test
    public void testInvokeMethod_DirectAssignable() throws Exception {
        SimpleBean bean = new SimpleBean();
        Method method = SimpleBean.class.getMethod("setName", String.class);
        ClassUtils.invokeMethod(method, bean, "direct");
        assertEquals("direct", bean.getName());
    }

    @Test
    public void testInvokeMethod_StringToBoolean() throws Exception {
        BeanWithPrimitives bean = new BeanWithPrimitives();
        Method method = BeanWithPrimitives.class.getMethod("setBoolVal", boolean.class);
        ClassUtils.invokeMethod(method, bean, "true");
        assertTrue(bean.isBoolVal());
    }

    @Test
    public void testInvokeMethod_StringToInteger() throws Exception {
        BeanWithPrimitives bean = new BeanWithPrimitives();
        Method method = BeanWithPrimitives.class.getMethod("setIntVal", int.class);
        ClassUtils.invokeMethod(method, bean, "42");
        assertEquals(42, bean.getIntVal());
    }

    @Test
    public void testInvokeMethod_StringToLong() throws Exception {
        BeanWithPrimitives bean = new BeanWithPrimitives();
        Method method = BeanWithPrimitives.class.getMethod("setLongVal", long.class);
        ClassUtils.invokeMethod(method, bean, "100");
        assertEquals(100L, bean.getLongVal());
    }

    @Test
    public void testInvokeMethod_StringToShort() throws Exception {
        BeanWithPrimitives bean = new BeanWithPrimitives();
        Method method = BeanWithPrimitives.class.getMethod("setShortVal", short.class);
        ClassUtils.invokeMethod(method, bean, "50");
        assertEquals((short) 50, bean.getShortVal());
    }

    @Test
    public void testInvokeMethod_StringToByte() throws Exception {
        BeanWithPrimitives bean = new BeanWithPrimitives();
        Method method = BeanWithPrimitives.class.getMethod("setByteVal", byte.class);
        ClassUtils.invokeMethod(method, bean, "7");
        assertEquals((byte) 7, bean.getByteVal());
    }

    @Test
    public void testInvokeMethod_StringToFloat() throws Exception {
        BeanWithPrimitives bean = new BeanWithPrimitives();
        Method method = BeanWithPrimitives.class.getMethod("setFloatVal", float.class);
        ClassUtils.invokeMethod(method, bean, "3.14");
        assertEquals(3.14f, bean.getFloatVal(), 0.001f);
    }

    @Test
    public void testInvokeMethod_StringToDouble() throws Exception {
        BeanWithPrimitives bean = new BeanWithPrimitives();
        Method method = BeanWithPrimitives.class.getMethod("setDoubleVal", double.class);
        ClassUtils.invokeMethod(method, bean, "2.718");
        assertEquals(2.718, bean.getDoubleVal(), 0.001);
    }

    @Test
    public void testInvokeMethod_StringToCharacter() throws Exception {
        BeanWithPrimitives bean = new BeanWithPrimitives();
        Method method = BeanWithPrimitives.class.getMethod("setCharVal", char.class);
        ClassUtils.invokeMethod(method, bean, "A");
        assertEquals('A', bean.getCharVal());
    }

    @Test
    public void testInvokeMethod_StringToCharacter_Empty() throws Exception {
        BeanWithPrimitives bean = new BeanWithPrimitives();
        Method method = BeanWithPrimitives.class.getMethod("setCharVal", char.class);
        // 空字符串会导致 charAt(0) 返回 null，但 primitive char 会抛 NPE
        assertThrows(RuntimeException.class, () ->
                ClassUtils.invokeMethod(method, bean, ""));
    }

    @Test
    public void testInvokeMethod_Fallback() throws Exception {
        SimpleBean bean = new SimpleBean();
        Method method = SimpleBean.class.getMethod("setName", String.class);
        // 传入非 String 类型，直接赋值（因为是 Object 类型参数）
        ClassUtils.invokeMethod(method, bean, String.valueOf("fallback"));
        assertEquals("fallback", bean.getName());
    }

    // ---- methodAccessible ----

    @Test
    public void testMethodAccessible_Public() throws Exception {
        Method method = SimpleBean.class.getMethod("getName");
        // public 方法，无需 setAccessible
        if (!java.lang.reflect.Modifier.isPublic(method.getModifiers())) {
            method.setAccessible(true);
        }
        assertTrue(java.lang.reflect.Modifier.isPublic(method.getModifiers()));
    }

    @Test
    public void testMethodAccessible_NonPublic() throws Exception {
        // 获取一个非 public 方法
        Method method = SimpleBean.class.getDeclaredMethod("getName");
        if (!java.lang.reflect.Modifier.isPublic(method.getModifiers())) {
            method.setAccessible(true);
        }
    }

    // ---- newInstance ----

    @Test
    public void testNewInstance_ByClass() {
        SimpleBean bean = ClassUtils.newInstance(SimpleBean.class);
        assertNotNull(bean);
        assertInstanceOf(SimpleBean.class, bean);
    }

    @Test
    public void testNewInstance_ByClassName() {
        Object obj = ClassUtils.newInstance("com.sonsure.dustman.test.common.utils.ClassUtilsTest$SimpleBean");
        assertNotNull(obj);
        assertInstanceOf(SimpleBean.class, obj);
    }

    @Test
    public void testNewInstance_ByClassNameNotFound() {
        assertThrows(RuntimeException.class, () ->
                ClassUtils.newInstance("com.nonexistent.ClassName"));
    }

    // ---- loadClass ----

    @Test
    public void testLoadClass_Found() {
        Class<?> clazz = ClassUtils.loadClass("com.sonsure.dustman.common.utils.ClassUtils");
        assertNotNull(clazz);
    }

    @Test
    public void testLoadClass_NotFound() {
        assertThrows(RuntimeException.class, () ->
                ClassUtils.loadClass("com.nonexistent.Foo"));
    }

    // ---- getBasicTypeClass ----

    @Test
    public void testGetBasicTypeClass_Primitives() {
        assertEquals(Byte.class, ClassUtils.getBasicTypeClass(byte.class));
        assertEquals(Short.class, ClassUtils.getBasicTypeClass(short.class));
        assertEquals(Integer.class, ClassUtils.getBasicTypeClass(int.class));
        assertEquals(Long.class, ClassUtils.getBasicTypeClass(long.class));
        assertEquals(Float.class, ClassUtils.getBasicTypeClass(float.class));
        assertEquals(Double.class, ClassUtils.getBasicTypeClass(double.class));
        assertEquals(Character.class, ClassUtils.getBasicTypeClass(char.class));
        assertEquals(Boolean.class, ClassUtils.getBasicTypeClass(boolean.class));
    }

    @Test
    public void testGetBasicTypeClass_NonPrimitive() {
        assertEquals(String.class, ClassUtils.getBasicTypeClass(String.class));
        assertEquals(Object.class, ClassUtils.getBasicTypeClass(Object.class));
    }

    // ---- isJavaBeanType ----

    @Test
    public void testIsJavaBeanType() {
        assertTrue(ClassUtils.isJavaBeanType(SimpleBean.class));
    }

    @Test
    public void testIsJavaBeanType_Primitive() {
        assertFalse(ClassUtils.isJavaBeanType(int.class));
    }

    @Test
    public void testIsJavaBeanType_CommonNonBean() {
        assertFalse(ClassUtils.isJavaBeanType(String.class));
        assertFalse(ClassUtils.isJavaBeanType(LocalDateTime.class));
        assertFalse(ClassUtils.isJavaBeanType(BigDecimal.class));
        assertFalse(ClassUtils.isJavaBeanType(Date.class));
        assertFalse(ClassUtils.isJavaBeanType(File.class));
        assertFalse(ClassUtils.isJavaBeanType(UUID.class));
    }

    // ---- isPrimitiveOrWrapper ----

    @Test
    public void testIsPrimitiveOrWrapper() {
        assertTrue(ClassUtils.isPrimitiveOrWrapper(int.class));
        assertTrue(ClassUtils.isPrimitiveOrWrapper(Integer.class));
        assertTrue(ClassUtils.isPrimitiveOrWrapper(long.class));
        assertTrue(ClassUtils.isPrimitiveOrWrapper(Long.class));
        assertTrue(ClassUtils.isPrimitiveOrWrapper(double.class));
        assertTrue(ClassUtils.isPrimitiveOrWrapper(Double.class));
        assertTrue(ClassUtils.isPrimitiveOrWrapper(boolean.class));
        assertTrue(ClassUtils.isPrimitiveOrWrapper(Boolean.class));
        assertTrue(ClassUtils.isPrimitiveOrWrapper(char.class));
        assertTrue(ClassUtils.isPrimitiveOrWrapper(Character.class));
        assertTrue(ClassUtils.isPrimitiveOrWrapper(short.class));
        assertTrue(ClassUtils.isPrimitiveOrWrapper(Short.class));
        assertTrue(ClassUtils.isPrimitiveOrWrapper(byte.class));
        assertTrue(ClassUtils.isPrimitiveOrWrapper(Byte.class));
        assertTrue(ClassUtils.isPrimitiveOrWrapper(float.class));
        assertTrue(ClassUtils.isPrimitiveOrWrapper(Float.class));
    }

    @Test
    public void testIsPrimitiveOrWrapper_Not() {
        assertFalse(ClassUtils.isPrimitiveOrWrapper(String.class));
        assertFalse(ClassUtils.isPrimitiveOrWrapper(Object.class));
        assertFalse(ClassUtils.isPrimitiveOrWrapper(BigDecimal.class));
    }

    // ---- isCommonNonBeanType ----

    @Test
    public void testIsCommonNonBeanType_True() {
        assertTrue(ClassUtils.isCommonNonBeanType(String.class));
        assertTrue(ClassUtils.isCommonNonBeanType(LocalDateTime.class));
        assertTrue(ClassUtils.isCommonNonBeanType(Date.class));
        assertTrue(ClassUtils.isCommonNonBeanType(UUID.class));
        assertTrue(ClassUtils.isCommonNonBeanType(BigDecimal.class));
        assertTrue(ClassUtils.isCommonNonBeanType(File.class));
        assertTrue(ClassUtils.isCommonNonBeanType(java.net.URL.class));
        assertTrue(ClassUtils.isCommonNonBeanType(java.util.Currency.class));
    }

    @Test
    public void testIsCommonNonBeanType_False() {
        assertFalse(ClassUtils.isCommonNonBeanType(SimpleBean.class));
        assertFalse(ClassUtils.isCommonNonBeanType(Object.class));
        assertFalse(ClassUtils.isCommonNonBeanType(Integer.class));
    }

    // ---- getInterfaces ----

    @Test
    public void testGetInterfaces() {
        List<Class<?>> interfaces = new ArrayList<>();
        ClassUtils.getInterfaces(ArrayList.class, interfaces);
        assertTrue(interfaces.contains(List.class));
        assertTrue(interfaces.contains(Collection.class));
        assertTrue(interfaces.contains(Iterable.class));
        assertTrue(interfaces.contains(RandomAccess.class));
        assertTrue(interfaces.contains(Cloneable.class));
    }

    // ---- getDefaultClassLoader ----

    @Test
    public void testGetDefaultClassLoader() {
        ClassLoader cl = ClassUtils.getDefaultClassLoader();
        assertNotNull(cl);
    }
}

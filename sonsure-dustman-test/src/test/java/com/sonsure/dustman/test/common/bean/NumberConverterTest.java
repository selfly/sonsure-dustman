package com.sonsure.dustman.test.common.bean;

import com.sonsure.dustman.common.bean.NumberConverter;
import com.sonsure.dustman.common.bean.TypeConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NumberConverter 单元测试
 */
public class NumberConverterTest {

    private NumberConverter converter;

    @BeforeEach
    public void setUp() {
        converter = new NumberConverter();
    }

    @Test
    public void testSupportSourceType_Number() {
        assertTrue(converter.supportSourceType(Integer.class));
        assertTrue(converter.supportSourceType(Long.class));
        assertTrue(converter.supportSourceType(Double.class));
        assertTrue(converter.supportSourceType(Float.class));
    }

    @Test
    public void testSupportSourceType_NotNumber() {
        assertFalse(converter.supportSourceType(String.class));
        assertFalse(converter.supportSourceType(Object.class));
    }

    @Test
    public void testSupportTargetType_AllNumberTypes() {
        assertTrue(converter.supportTargetType(Byte.class));
        assertTrue(converter.supportTargetType(Short.class));
        assertTrue(converter.supportTargetType(Integer.class));
        assertTrue(converter.supportTargetType(Long.class));
        assertTrue(converter.supportTargetType(Float.class));
        assertTrue(converter.supportTargetType(Double.class));
    }

    @Test
    public void testSupportTargetType_NotSupported() {
        assertFalse(converter.supportTargetType(String.class));
        assertFalse(converter.supportTargetType(Boolean.class));
        assertFalse(converter.supportTargetType(Object.class));
    }

    @Test
    public void testSupportTargetType_Primitive() {
        assertTrue(converter.supportTargetType(int.class));
        assertTrue(converter.supportTargetType(long.class));
    }

    @Test
    public void testConvert_ToByte() throws Exception {
        PropertyDescriptor pd = new PropertyDescriptor("value", BeanWithByte.class);
        Object result = converter.convert(pd, 123);
        assertInstanceOf(Byte.class, result);
        assertEquals((byte) 123, result);
    }

    @Test
    public void testConvert_ToShort() throws Exception {
        PropertyDescriptor pd = new PropertyDescriptor("value", BeanWithShort.class);
        Object result = converter.convert(pd, 12345);
        assertInstanceOf(Short.class, result);
        assertEquals((short) 12345, result);
    }

    @Test
    public void testConvert_ToInteger() throws Exception {
        PropertyDescriptor pd = new PropertyDescriptor("value", BeanWithInteger.class);
        Object result = converter.convert(pd, 999);
        assertInstanceOf(Integer.class, result);
        assertEquals(999, result);
    }

    @Test
    public void testConvert_ToLong() throws Exception {
        PropertyDescriptor pd = new PropertyDescriptor("value", BeanWithLong.class);
        Object result = converter.convert(pd, 123456L);
        assertInstanceOf(Long.class, result);
        assertEquals(123456L, result);
    }

    @Test
    public void testConvert_ToFloat() throws Exception {
        PropertyDescriptor pd = new PropertyDescriptor("value", BeanWithFloat.class);
        Object result = converter.convert(pd, 3.14);
        assertInstanceOf(Float.class, result);
        assertEquals(3.14f, (Float) result, 0.001f);
    }

    @Test
    public void testConvert_ToDouble() throws Exception {
        PropertyDescriptor pd = new PropertyDescriptor("value", BeanWithDouble.class);
        Object result = converter.convert(pd, 2.718);
        assertInstanceOf(Double.class, result);
        assertEquals(2.718, (Double) result, 0.001);
    }

    @Test
    public void testConvert_UnsupportedType() throws Exception {
        // 对于不支持的 target 类型，应原样返回
        PropertyDescriptor pd = new PropertyDescriptor("value", BeanWithString.class);
        Object result = converter.convert(pd, 42);
        assertEquals(42, result);
    }

    // ---- Bean stubs for PropertyDescriptor ----

    public static class BeanWithByte {
        private Byte value;
        public Byte getValue() { return value; }
        public void setValue(Byte value) { this.value = value; }
    }

    public static class BeanWithShort {
        private Short value;
        public Short getValue() { return value; }
        public void setValue(Short value) { this.value = value; }
    }

    public static class BeanWithInteger {
        private Integer value;
        public Integer getValue() { return value; }
        public void setValue(Integer value) { this.value = value; }
    }

    public static class BeanWithLong {
        private Long value;
        public Long getValue() { return value; }
        public void setValue(Long value) { this.value = value; }
    }

    public static class BeanWithFloat {
        private Float value;
        public Float getValue() { return value; }
        public void setValue(Float value) { this.value = value; }
    }

    public static class BeanWithDouble {
        private Double value;
        public Double getValue() { return value; }
        public void setValue(Double value) { this.value = value; }
    }

    public static class BeanWithString {
        private String value;
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }
}

package com.sonsure.dustman.test.common.utils;

import com.sonsure.dustman.common.utils.StrUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StrUtils 单元测试
 */
public class StrUtilsTest {

    @Test
    public void testMinify_Normal() {
        assertEquals("hello world", StrUtils.minify("hello   world"));
    }

    @Test
    public void testMinify_Trim() {
        // minify 压缩连续空白为单空格，前导空白被跳过
        assertEquals("a b c ", StrUtils.minify("  a   b   c  "));
    }

    @Test
    public void testMinify_SingleWord() {
        assertEquals("hello", StrUtils.minify("hello"));
    }

    @Test
    public void testMinify_Empty() {
        assertEquals("", StrUtils.minify(""));
    }

    @Test
    public void testEscapeHtmlTags_Normal() {
        assertEquals("&amp;&lt;&gt;&quot;", StrUtils.escapeHtmlTags("&<>\""));
    }

    @Test
    public void testEscapeHtmlTags_Blank() {
        assertEquals("", StrUtils.escapeHtmlTags(""));
    }

    @Test
    public void testEscapeHtmlTags_Null() {
        assertEquals("", StrUtils.escapeHtmlTags(null));
    }

    @Test
    public void testEscapeHtmlTags_FullWidthSpace() {
        assertEquals(" ", StrUtils.escapeHtmlTags("\u3000"));
    }

    @Test
    public void testEscapeHtmlTags_NoSpecialChars() {
        assertEquals("abc123", StrUtils.escapeHtmlTags("abc123"));
    }

    @Test
    public void testUnescapeHtmlTags_Normal() {
        assertEquals("&<>\"", StrUtils.unescapeHtmlTags("&amp;&lt;&gt;&quot;"));
    }

    @Test
    public void testUnescapeHtmlTags_Blank() {
        assertEquals("", StrUtils.unescapeHtmlTags(""));
    }

    @Test
    public void testUnescapeHtmlTags_Null() {
        assertEquals("", StrUtils.unescapeHtmlTags(null));
    }

    @Test
    public void testReplaceChars_Normal() {
        String[][] chars = new String[][]{{"a", "x"}, {"b", "y"}};
        assertEquals("x y c", StrUtils.replaceChars("a b c", chars));
    }

    @Test
    public void testReplaceChars_Empty() {
        assertEquals("", StrUtils.replaceChars("", new String[][]{{"a", "b"}}));
    }

    @Test
    public void testSubstringForByte_Ascii() {
        assertEquals("hel", StrUtils.substringForByte("hello", 3));
    }

    @Test
    public void testSubstringForByte_Chinese() {
        // 中文 UTF-8 占 3 字节, 6 字节只能对齐到 "你"
        assertEquals("你", StrUtils.substringForByte("你好世界", 6));
    }

    @Test
    public void testSubstringForByte_Mixed() {
        // a(1) + 你(3) + 好(3) + b(1) = 8 字节，对齐到字符边界
        assertEquals("a你好b", StrUtils.substringForByte("a你好bc", 8));
    }

    @Test
    public void testSubstringForByte_MixedFull() {
        assertEquals("a你好bc", StrUtils.substringForByte("a你好bc", 9));
    }

    @Test
    public void testSubstringForByte_MixedAtBoundary() {
        // 7 字节在 "好" 的中间，回退到 "a你"
        assertEquals("a你", StrUtils.substringForByte("a你好bc", 7));
    }

    @Test
    public void testSubstringForByte_LengthExceeds() {
        assertEquals("hello", StrUtils.substringForByte("hello", 100));
    }

    @Test
    public void testSubstringForByte_LengthZero() {
        // length < 1 返回原文本
        assertEquals("hello", StrUtils.substringForByte("hello", 0));
    }

    @Test
    public void testSubstringForByte_LengthNegative() {
        assertEquals("hello", StrUtils.substringForByte("hello", -1));
    }

    @Test
    public void testSubstringForByte_BlankText() {
        assertEquals("", StrUtils.substringForByte("", 5));
    }

    @Test
    public void testReflectionToString_Null() {
        assertEquals("null", StrUtils.reflectionToString(null));
    }

    @Test
    public void testReflectionToString_Object() {
        SimpleReflectionBean bean = new SimpleReflectionBean();
        bean.setMessage("hello");
        String result = StrUtils.reflectionToString(bean);
        assertTrue(result.startsWith("SimpleReflectionBean@"));
        assertTrue(result.contains("message=hello"));
    }

    public static class SimpleReflectionBean {
        private String message = "hello";
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    @Test
    public void testEquals_BothNull() {
        assertTrue(StrUtils.equals(null, null));
    }

    @Test
    public void testEquals_OneNull() {
        assertFalse(StrUtils.equals(null, "a"));
        assertFalse(StrUtils.equals("a", null));
    }

    @Test
    public void testEquals_Equal() {
        assertTrue(StrUtils.equals("abc", "abc"));
    }

    @Test
    public void testEquals_NotEqual() {
        assertFalse(StrUtils.equals("abc", "xyz"));
    }

    @Test
    public void testEqualsIgnoreCase_BothNull() {
        assertTrue(StrUtils.equalsIgnoreCase(null, null));
    }

    @Test
    public void testEqualsIgnoreCase_OneNull() {
        assertFalse(StrUtils.equalsIgnoreCase(null, "a"));
        assertFalse(StrUtils.equalsIgnoreCase("a", null));
    }

    @Test
    public void testEqualsIgnoreCase_CaseInsensitive() {
        assertTrue(StrUtils.equalsIgnoreCase("Hello", "HELLO"));
    }

    @Test
    public void testEqualsIgnoreCase_TurkishI() {
        assertTrue(StrUtils.equalsIgnoreCase("I", "i"));
    }

    @Test
    public void testIsBlank_Null() {
        assertTrue(StrUtils.isBlank(null));
    }

    @Test
    public void testIsBlank_Empty() {
        assertTrue(StrUtils.isBlank(""));
    }

    @Test
    public void testIsBlank_Whitespace() {
        assertTrue(StrUtils.isBlank("   "));
    }

    @Test
    public void testIsBlank_NotEmpty() {
        assertFalse(StrUtils.isBlank("abc"));
    }

    @Test
    public void testIsNotBlank() {
        assertTrue(StrUtils.isNotBlank("abc"));
        assertFalse(StrUtils.isNotBlank(""));
    }

    @Test
    public void testReplace_Normal() {
        assertEquals("hello world", StrUtils.replace("hello java", "java", "world"));
    }

    @Test
    public void testReplace_Null() {
        assertNull(StrUtils.replace(null, "a", "b"));
    }

    @Test
    public void testContains_Normal() {
        assertTrue(StrUtils.contains("hello world", "world"));
    }

    @Test
    public void testContains_NullStr() {
        assertFalse(StrUtils.contains(null, "a"));
    }

    @Test
    public void testContains_NotContains() {
        assertFalse(StrUtils.contains("hello", "xyz"));
    }

    @Test
    public void testSplit_Normal() {
        assertArrayEquals(new String[]{"a", "b", "c"}, StrUtils.split("a,b,c", ","));
    }

    @Test
    public void testSplit_NoDelimiter() {
        assertArrayEquals(new String[]{"abc"}, StrUtils.split("abc", ","));
    }

    @Test
    public void testSplit_StartDelimiter() {
        assertArrayEquals(new String[]{"", "a", "b"}, StrUtils.split(",a,b", ","));
    }

    @Test
    public void testStartsWith_Normal() {
        assertTrue(StrUtils.startsWith("hello", "he"));
    }

    @Test
    public void testStartsWith_NullStr() {
        assertFalse(StrUtils.startsWith(null, "he"));
    }

    @Test
    public void testStartsWith_NotMatch() {
        assertFalse(StrUtils.startsWith("hello", "world"));
    }

    @Test
    public void testEndsWith_Normal() {
        assertTrue(StrUtils.endsWith("hello", "lo"));
    }

    @Test
    public void testEndsWith_NullStr() {
        assertFalse(StrUtils.endsWith(null, "lo"));
    }

    @Test
    public void testEndsWith_NotMatch() {
        assertFalse(StrUtils.endsWith("hello", "start"));
    }

    @Test
    public void testStartsWithIgnoreCase_Normal() {
        assertTrue(StrUtils.startsWithIgnoreCase("HelloWorld", "hello"));
    }

    @Test
    public void testStartsWithIgnoreCase_NullStr() {
        assertFalse(StrUtils.startsWithIgnoreCase(null, "he"));
    }

    @Test
    public void testStartsWithIgnoreCase_NotMatch() {
        assertFalse(StrUtils.startsWithIgnoreCase("abc", "xyz"));
    }

    @Test
    public void testEndsWithIgnoreCase_Normal() {
        assertTrue(StrUtils.endsWithIgnoreCase("HelloWorld", "world"));
    }

    @Test
    public void testEndsWithIgnoreCase_NullStr() {
        assertFalse(StrUtils.endsWithIgnoreCase(null, "ld"));
    }

    @Test
    public void testEndsWithIgnoreCase_NotMatch() {
        assertFalse(StrUtils.endsWithIgnoreCase("abc", "xyz"));
    }
}

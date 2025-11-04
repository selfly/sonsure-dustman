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

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 字符文本操作
 * 太多的StringUtils了，命名为TextUtils
 * <p/>
 *
 * @author liyd
 * @since 2015-8-14
 */
public class StrUtils {

    public static final String EMPTY = "";
    public static final int INDEX_NOT_FOUND = -1;

    public static String minify(String text) {
        StringBuilder sb = new StringBuilder();
        char pre = ' ';
        for (char c : text.toCharArray()) {
            if (!Character.isWhitespace(c)) {
                sb.append(c);
            } else if (!Character.isWhitespace(pre)) {
                sb.append(' ');
            }
            pre = c;
        }
        return sb.toString();
    }

    /**
     * 转换特殊符号
     *
     * @param str the str
     * @return string
     */
    public static String convertHtmlSpecialChars(String str) {
        if (isBlank(str)) {
            return "";
        }
        //最后一个中文全角空格换成英文，防止string的trim方法失效
        String[][] chars = new String[][]{{"&", "&amp;"}, {"<", "&lt;"}, {">", "&gt;"}, {"\"", "&quot;"}, {"　", " "}};
        return replaceChars(str, chars);
    }

    /**
     * 反转特殊符号，将转义后的符号转换回标签，以便缩进等格式化
     *
     * @param str the str
     * @return string
     */
    public static String reverseHtmlSpecialChars(String str) {
        if (isBlank(str)) {
            return "";
        }
        String[][] chars = new String[][]{{"&amp;", "&"}, {"&lt;", "<"}, {"&gt;", ">"}, {"&quot;", "\""}, {"　", " "}};
        return replaceChars(str, chars);
    }

    public static String replaceChars(String str, String[][] chars) {
        for (String[] cs : chars) {
            str = str.replace(cs[0], cs[1]);
        }
        return str;
    }

    /**
     * 截取字符串，按byte长度，可以避免直接按length截取中英文混合显示长短差很多的情况
     *
     * @param text   the text
     * @param length the length
     * @return string
     */
    public static String substringForByte(String text, int length) {

        return substringForByte(text, length, false);
    }

    /**
     * 截取字符串，按byte长度，可以避免直接按length截取中英文混合显示长短差很多的情况
     *
     * @param text                  the text
     * @param length                the length
     * @param isConvertSpecialChars the is convert special chars
     * @return string
     */
    public static String substringForByte(String text, int length, boolean isConvertSpecialChars) {

        if (isBlank(text) || length < 1) {
            return text;
        }
        //转换特殊字符，页面显示时非常有用
        if (isConvertSpecialChars) {
            text = convertHtmlSpecialChars(text);
        }
        try {
            //防止中英文有长有短，转换成byte截取
            byte[] bytes = text.getBytes("GBK");

            //截取
            byte[] contentNameBytes = Arrays.copyOfRange(bytes, 0, length);

            //处理截取了半个汉字的情况
            int count = 0;
            for (byte b : contentNameBytes) {
                if (b < 0) {
                    count++;
                }
            }
            if (count % 2 != 0) {
                contentNameBytes = Arrays.copyOfRange(contentNameBytes, 0, contentNameBytes.length - 1);
            }

            return new String(contentNameBytes, "GBK");
        } catch (UnsupportedEncodingException e) {
            throw new SonsureException("根据byte截取字符串失败", e);
        }
    }

    public static String reflectionToString(Object obj) {
        if (obj == null) {
            return "null";
        }

        Class<?> clazz = obj.getClass();
        StringBuilder sb = new StringBuilder();
        sb.append(clazz.getSimpleName()).append("@").append(Integer.toHexString(obj.hashCode())).append("[\n");

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                sb.append("  ").append(field.getName()).append("=").append(field.get(obj)).append(System.lineSeparator());
            } catch (IllegalAccessException e) {
                sb.append("  ").append(field.getName()).append("=N/A (Access Denied)").append(System.lineSeparator());
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static String replace(String inString, String oldPattern, String newPattern) {
        return inString == null ? null : inString.replace(oldPattern, newPattern);
    }

    public static boolean contains(String str, String subStr) {
        return str != null && str.contains(subStr);
    }

    public static String[] split(String toSplit, String delimiter) {
        List<String> result = new ArrayList<>();
        int start = 0;
        int index;
        while ((index = toSplit.indexOf(delimiter, start)) != -1) {
            result.add(toSplit.substring(start, index));
            start = index + delimiter.length();
        }
        result.add(toSplit.substring(start));
        return result.toArray(new String[0]);
    }

    public static boolean startsWith(String str, String prefix) {
        return str != null && str.startsWith(prefix);
    }

    public static boolean endsWith(String str, String suffix) {
        return str != null && str.endsWith(suffix);
    }

    public static String substringBefore(String str, String separator) {
        if (isBlank(str) || separator == null) {
            return str;
        }
        if (separator.isEmpty()) {
            return EMPTY;
        }
        int pos = str.indexOf(separator);
        if (pos == INDEX_NOT_FOUND) {
            return str;
        }
        return str.substring(0, pos);
    }
}

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
import com.sonsure.dumper.common.spring.StringUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * 字符文本操作
 * 太多的StringUtils了，命名为TextUtils
 * <p/>
 *
 * @author liyd
 * @date 2015-8-14
 */
public class StrUtils {

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
        if (StringUtils.hasText(str)) {
            return null;
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
        if (!StringUtils.hasText(str)) {
            return null;
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

        if (!StringUtils.hasText(text) || length < 1) {
            return text;
        }
        //转换特殊字符，页面显示时非常有用
        if (isConvertSpecialChars) {
            text = convertHtmlSpecialChars(text);
        }
        try {
            //防止中英文有长有短，转换成byte截取
            assert text != null;
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
        return !StringUtils.hasText(str);
    }

    public static boolean isNotBlank(String str) {
        return StringUtils.hasText(str);
    }

    public static String replace(String inString, String oldPattern, String newPattern) {
        return StringUtils.replace(inString, oldPattern, newPattern);
    }

    public static String[] split(String toSplit, String delimiter) {
        String[] split = StringUtils.split(toSplit, delimiter);
        if (split == null) {
            return toSplit == null ? new String[0] : new String[]{toSplit};
        }
        return split;
    }
}

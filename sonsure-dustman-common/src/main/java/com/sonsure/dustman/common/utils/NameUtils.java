/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.common.utils;

/**
 * 名称操作辅助类
 * <p/>
 * Date: 13-12-6
 * Time: 下午5:17
 *
 * @author selfly
 */
public class NameUtils {

    /**
     * 获取合法的变量名(只允许字母,数字,下划线)
     *
     * @param name the name
     * @return legal name
     */
    public static String getLegalName(String name) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < name.length(); i++) {

            char c = name.charAt(i);

            if (Character.isLetter(c) || Character.isDigit(c) || c == '_') {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 首字母大写
     *
     * @param name the name
     * @return first upper name
     */
    public static String getFirstUpperName(String name) {
        if (StrUtils.isBlank(name)) {
            return null;
        }
        //去掉前面的非字母
        char c = name.charAt(0);
        while (!((c >= 65 && c <= 90) || (c >= 97 && c <= 122))) {
            name = name.substring(1);
            c = name.charAt(0);
        }
        String firstChar = name.substring(0, 1).toUpperCase();
        return firstChar + name.substring(1);
    }

    /**
     * 首字母小写
     *
     * @param name the name
     * @return first lower name
     */
    public static String getFirstLowerName(String name) {
        if (StrUtils.isBlank(name)) {
            return null;
        }
        //去掉前面的非字母
        char c = name.charAt(0);
        while (!((c >= 65 && c <= 90) || (c >= 97 && c <= 122))) {
            name = name.substring(1);
            c = name.charAt(0);
        }
        String firstChar = name.substring(0, 1).toLowerCase();
        return firstChar + name.substring(1);
    }

    /**
     * 转换成骆驼命名法返回 默认分隔符下划线_
     *
     * @param name the name
     * @return camel name
     */
    public static String getCamelName(String name) {
        return getCamelName(name, '_');
    }

    /**
     * 转换成骆驼命名法返回,指定分隔符
     *
     * @param name      the name
     * @param delimiter the delimiter
     * @return camel name
     */
    public static String getCamelName(String name, char delimiter) {
        if (StrUtils.isBlank(name)) {
            return null;
        }
        name = name.toLowerCase();
        //去掉前面的 delimiter
        while (name.charAt(0) == delimiter) {
            name = name.substring(1);
        }
        //去掉后面的 delimiter
        while (name.charAt(name.length() - 1) == delimiter) {
            name = name.substring(0, name.length() - 1);
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < name.length(); i++) {

            char c = name.charAt(i);

            if (c == delimiter) {
                i++;
                sb.append(Character.toUpperCase(name.charAt(i)));
                continue;
            }
            sb.append(c);
        }

        return sb.toString();
    }

    /**
     * 将骆驼命名法反转成下划线返回
     *
     * @param name the name
     * @return underline name
     */
    public static String getUnderlineName(String name) {

        return getUpperDelimiterName(name, "_");
    }

    /**
     * 将骆驼命名法反转成下划线返回
     *
     * @param name the name
     * @return line through name
     */
    public static String getLineThroughName(String name) {

        return getLowerDelimiterName(name, "-");
    }

    /**
     * 返回大写的按指定分隔符命名
     *
     * @param name      the name
     * @param delimiter the delimiter
     * @return upper delimiter name
     */
    public static String getUpperDelimiterName(String name, String delimiter) {
        return getLowerDelimiterName(name, delimiter).toUpperCase();
    }

    /**
     * 返回小写的按指定分隔符命名
     *
     * @param name      the name
     * @param delimiter the delimiter
     * @return lower delimiter name
     */
    public static String getLowerDelimiterName(String name, String delimiter) {

        if (StrUtils.isBlank(name)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < name.length(); i++) {

            char c = name.charAt(i);

            if (i > 0 && Character.isUpperCase(c)) {
                sb.append(delimiter);
            }

            sb.append(c);
        }

        return sb.toString().toLowerCase();
    }

    /**
     * 保留原文件后缀生成唯一文件名
     *
     * @param fileName the file name
     * @return string
     */
    public static String createUniqueFileName(String fileName) {

        int index = fileName.lastIndexOf(".");
        String suffix = fileName.substring(index);
        return UUIDUtils.getUUID16() + suffix;
    }

    /**
     * 在文件名后加上指定后缀，不包括后缀名
     *
     * @param fileName  the file name
     * @param endSuffix the end suffix
     * @return string
     */
    public static String createEndSuffixFileName(String fileName, String endSuffix) {
        int index = fileName.lastIndexOf(".");
        String preFileName = fileName.substring(0, index);
        String suffix = fileName.substring(index);
        return preFileName + endSuffix + suffix;
    }

}

/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.common.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;


/**
 * @author liyd
 * @date 7/30/14
 */
public class TimeUtils {

    private static final long ONE_MINUTE = 60000L;
    private static final long ONE_HOUR = 3600000L;
    private static final long ONE_DAY = 86400000L;
    private static final long ONE_WEEK = 604800000L;

    private static final String ONE_SECOND_AGO = "秒前";
    private static final String ONE_MINUTE_AGO = "分钟前";
    private static final String ONE_HOUR_AGO = "小时前";
    private static final String ONE_DAY_AGO = "天前";
    private static final String ONE_MONTH_AGO = "月前";
    private static final String ONE_YEAR_AGO = "年前";

    /**
     * 获取当天开始时间
     *
     * @return
     */
    public static Date getTodayBegin() {
        Calendar currentDate = Calendar.getInstance();
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        return currentDate.getTime();
    }

    /**
     * 获取当天结束时间
     *
     * @return
     */
    public static Date getTodayEnd() {
        Calendar currentDate = Calendar.getInstance();
        currentDate.set(Calendar.HOUR_OF_DAY, 23);
        currentDate.set(Calendar.MINUTE, 59);
        currentDate.set(Calendar.SECOND, 59);
        return currentDate.getTime();
    }


    /**
     * 格式化成多久以前
     *
     * @param date the date
     * @return string string
     */
    public static String formatAgo(LocalDateTime date) {
        return formatAgo(date.toInstant(ZoneOffset.of("+8")).toEpochMilli());
    }

    /**
     * 格式化成多久以前
     *
     * @param date the date
     * @return string
     */
    public static String formatAgo(Date date) {
        return formatAgo(date.getTime());
    }

    /**
     * 格式化成多久以前
     *
     * @param time the time
     * @return string string
     */
    public static String formatAgo(Long time) {

        if (time == null) {
            return "未知";
        }

        long curTime = System.currentTimeMillis() - time;

        if (curTime < ONE_MINUTE) {
            long seconds = toSeconds(curTime);
            return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
        }
        if (curTime < 45L * ONE_MINUTE) {
            long minutes = toMinutes(curTime);
            return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
        }
        if (curTime < 24L * ONE_HOUR) {
            long hours = toHours(curTime);
            return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
        }
        if (curTime < 48L * ONE_HOUR) {
            return "昨天";
        }
        if (curTime < 30L * ONE_DAY) {
            long days = toDays(curTime);
            return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
        }
        if (curTime < 12L * 4L * ONE_WEEK) {
            long months = toMonths(curTime);
            return (months <= 0 ? 1 : months) + ONE_MONTH_AGO;
        } else {
            long years = toYears(curTime);
            return (years <= 0 ? 1 : years) + ONE_YEAR_AGO;
        }
    }

    private static long toSeconds(long date) {
        return date / 1000L;
    }

    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }

    private static long toDays(long date) {
        return toHours(date) / 24L;
    }

    private static long toMonths(long date) {
        return toDays(date) / 30L;
    }

    private static long toYears(long date) {
        return toMonths(date) / 365L;
    }

}

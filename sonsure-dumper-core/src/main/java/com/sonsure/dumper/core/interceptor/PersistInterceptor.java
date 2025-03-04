package com.sonsure.dumper.core.interceptor;

import com.sonsure.dumper.core.command.CommandDetails;

/**
 * @author selfly
 */
public interface PersistInterceptor {

    /**
     * 执行前调用
     *
     * @param dialect        the dialect
     * @param commandDetails the command context
     * @return the boolean
     */
    default boolean executeBefore(String dialect, CommandDetails commandDetails) {
        return true;
    }

    /**
     * 执行后调用,返回结果将替换实际查询结果
     *
     * @param dialect        the dialect
     * @param commandDetails the command context
     * @param commandResult  the command result
     * @return the object
     */
    default Object executeAfter(String dialect, CommandDetails commandDetails, Object commandResult) {
        return commandResult;
    }
}

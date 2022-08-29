package com.sonsure.dumper.core.interceptor;

import com.sonsure.dumper.core.command.CommandContext;
import com.sonsure.dumper.core.command.CommandType;

/**
 * @author selfly
 */
public interface PersistInterceptor {

    /**
     * 执行前调用
     *
     * @param dialect        the dialect
     * @param commandContext the command context
     * @param commandType    the command type
     * @return the boolean
     */
    default boolean executeBefore(String dialect, CommandContext commandContext, CommandType commandType) {
        return true;
    }

    /**
     * 执行后调用,返回结果将替换实际查询结果
     *
     * @param dialect        the dialect
     * @param commandContext the command context
     * @param commandType    the command type
     * @param commandResult  the command result
     * @return the object
     */
    default Object executeAfter(String dialect, CommandContext commandContext, CommandType commandType, Object commandResult) {
        return commandResult;
    }
}

package com.sonsure.dumper.core.interceptor;

import com.sonsure.dumper.core.command.build.ExecutableCmd;

/**
 * @author selfly
 */
public interface PersistInterceptor {

    /**
     * 执行前调用
     *
     * @param dialect       the dialect
     * @param executableCmd the executable cmd
     * @return the boolean
     */
    default boolean executeBefore(String dialect, ExecutableCmd executableCmd) {
        return true;
    }

    /**
     * 执行后调用,返回结果将替换实际查询结果
     *
     * @param dialect       the dialect
     * @param executableCmd the executable cmd
     * @param commandResult the command result
     * @return the object
     */
    default Object executeAfter(String dialect, ExecutableCmd executableCmd, Object commandResult) {
        return commandResult;
    }
}

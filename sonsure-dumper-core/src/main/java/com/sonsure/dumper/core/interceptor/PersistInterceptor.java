package com.sonsure.dumper.core.interceptor;

/**
 * @author selfly
 */
public interface PersistInterceptor {

    /**
     * 执行前调用
     *
     * @param persistContext the interceptor context
     */
    default void executeBefore(PersistContext persistContext) {
    }

    /**
     * 执行后调用,返回结果将替换实际查询结果
     *
     * @param persistContext the persist context
     */
    default void executeAfter(PersistContext persistContext) {
    }
}

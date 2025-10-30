package com.sonsure.dumper.core.interceptor;

/**
 * @author selfly
 */
public interface PersistInterceptor {

    /**
     * 执行前调用
     *
     * @param persistContext the interceptor context
     * @param chain          the chain
     */
    default void executeBefore(PersistContext persistContext, InterceptorChain chain) {
    }

    /**
     * 执行后调用,返回结果将替换实际查询结果
     *
     * @param persistContext the persist context
     * @param chain          the chain
     */
    default void executeAfter(PersistContext persistContext, InterceptorChain chain) {
    }
}

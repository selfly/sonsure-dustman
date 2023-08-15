/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.lambda;

/**
 * 使用 Consumer 会有Result of 'xx.getXxx()' is ignored警告
 *
 * @author liyd
 * @param <T>
 * @param <R>
 */
@FunctionalInterface
public interface Function<T, R> extends SuperFunction {
    /**
     * Gets declared method.
     *
     * @param t the t
     * @return the declared method
     * @throws Exception the exception
     */
    R getDeclaredMethod(T t) throws Exception;
}

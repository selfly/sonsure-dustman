/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command;

/**
 * 执行标识接口
 * <p>
 *
 * @author liyd
 * @since  17/4/11
 */
public interface CommandExecutor<E extends CommandExecutor<E>> {

    /**
     * 是否禁止转换，command不做任何加工
     *
     * @return C
     */
    E forceNative();

    /**
     * 是否使用named parameter 方式
     *
     * @return C
     */
    E namedParameter();
}

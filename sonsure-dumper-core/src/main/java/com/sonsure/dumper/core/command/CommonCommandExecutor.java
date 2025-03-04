/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   https://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command;

/**
 * The interface Common command executor.
 *
 * @author liyd
 */
public interface CommonCommandExecutor<E extends CommonCommandExecutor<E>> extends CommandExecutor {

    /**
     * 是否禁止转换，command不做任何加工
     *
     * @return t t
     */
    E forceNative();

//    /**
//     * 是否使用named parameter 方式
//     *
//     * @return t t
//     */
//    T namedParameter();

}

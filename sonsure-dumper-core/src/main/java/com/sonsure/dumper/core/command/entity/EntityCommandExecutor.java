/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;


import com.sonsure.dumper.core.command.CommonCommandExecutor;

/**
 * 实体执行标识接口
 * <p>
 *
 * @author liyd
 * @since 17/4/19
 */
public interface EntityCommandExecutor<T extends EntityCommandExecutor<T>> extends CommonCommandExecutor<T> {
}

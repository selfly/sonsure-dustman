/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.jdbc.extension.executor;

import com.sonsure.dumper.core.command.CommandExecutor;

public interface CountCommandExecutor extends CommandExecutor {

    CountCommandExecutor clazz(Class<?> clazz);

    long getCount();
}

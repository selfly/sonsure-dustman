/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.build;

/**
 * @author selfly
 */

public enum ExecutionType {

    /**
     * Insert command type.
     */
    INSERT,

    /**
     * fin for list command type.
     */
    FIND_LIST,

    /**
     * Query single result command type.
     */
    FIND_ONE,

    /**
     * Query for map command type.
     */
    FIND_ONE_FOR_MAP,

    /**
     * Query for map list command type.
     */
    FIND_LIST_FOR_MAP,

    /**
     * Query one col command type.
     */
    FIND_ONE_FOR_SCALAR,

    /**
     * Query one col list command type.
     */
    FIND_LIST_FOR_SCALAR,

    /**
     * Update command type.
     */
    UPDATE,

    /**
     * Batch update command type.
     */
    BATCH_UPDATE,

    /**
     * Delete command type.
     */
    DELETE,

    /**
     * Execute command type.
     */
    EXECUTE,

    /**
     * Execute script command type.
     */
    EXECUTE_SCRIPT;
}

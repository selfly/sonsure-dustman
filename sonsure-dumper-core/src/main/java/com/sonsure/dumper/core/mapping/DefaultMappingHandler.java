/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.mapping;

/**
 * 默认名称处理handler
 * <p>
 * Date: 2/12/14
 * Time: 4:51 PM
 *
 * @author liyd
 */
public class DefaultMappingHandler extends AbstractMappingHandler {

    public DefaultMappingHandler() {
        this(null);
    }

    public DefaultMappingHandler(String modelPackages) {
        this(modelPackages, null);
    }

    public DefaultMappingHandler(String modelPackages, ClassLoader classLoader) {
        super(modelPackages, classLoader);
    }
}

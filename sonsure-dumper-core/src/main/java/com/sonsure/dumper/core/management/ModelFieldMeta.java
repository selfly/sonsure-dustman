/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.management;

import lombok.Getter;
import lombok.Setter;

/**
 * @author selfly
 */
@Setter
@Getter
public class ModelFieldMeta {

    private String name;

    private Object idAnnotation;

    private Object columnAnnotation;

}

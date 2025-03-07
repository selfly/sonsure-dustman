/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command;

import lombok.Getter;
import lombok.Setter;

/**
 * @author selfly
 */
@Setter
@Getter
public class ModelClassFieldDetails {

    private String fieldName;

    private Object idAnnotation;

    private Object columnAnnotation;

}

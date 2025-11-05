/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.jdbc.command.build;

import lombok.Getter;
import lombok.Setter;

/**
 * @author selfly
 */
@Setter
@Getter
public class EntityClassFieldWrapper {

    private String fieldName;

    private Object idAnnotation;

    private Object columnAnnotation;

    public String getFieldAnnotationColumn() {
        return CommandBuildHelper.getFieldAnnotationColumn(this.columnAnnotation);
    }

}

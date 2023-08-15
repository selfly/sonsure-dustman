/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.sql;

import net.sf.jsqlparser.schema.Column;
import org.apache.commons.lang3.StringUtils;

/**
 * @author liyd
 */
public class ColumnMapping {

    private final Column column;

    private final String mappingName;

    public ColumnMapping(Column column, String mappingName) {
        this.column = column;
        this.mappingName = mappingName;
    }

    public String getSmartMappingName() {
        if (StringUtils.isBlank(this.mappingName)) {
            return column.getColumnName();
        }
        return mappingName;
    }
}

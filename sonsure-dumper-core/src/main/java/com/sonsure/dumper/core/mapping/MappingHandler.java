/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.mapping;


import com.sonsure.dumper.core.command.build.CmdParameter;

import java.util.List;

/**
 * 实体映射处理
 * <p>
 *
 * @author liyd
 * @since 17/4/11
 */
public interface MappingHandler {

    /**
     * Register class mapping.
     *
     * @param clazz the clazz
     */
    void registerClassMapping(Class<?> clazz);

    /**
     * Register table prefix.
     *
     * @param prefix   the prefix
     * @param packages the packages
     */
    void registerTablePrefix(String prefix, String... packages);

    /**
     * Gets table prefix.
     *
     * @param referenceClassName the reference class name
     * @return the table prefix
     */
    String getTablePrefix(String referenceClassName);

    /**
     * Gets table prefix.
     *
     * @param entityClass the entity class
     * @return the table prefix
     */
    default String getTablePrefix(Class<?> entityClass) {
        return this.getTablePrefix(entityClass.getName());
    }

    /**
     * 根据实体名获取表名
     *
     * @param className  the class name
     * @param parameters the parameters
     * @return table name
     */
    String getTable(String className, List<CmdParameter> parameters);

    /**
     * 根据实体名获取表名
     *
     * @param clazz      the clazz
     * @param parameters the parameters
     * @return table name
     */
    String getTable(Class<?> clazz, List<CmdParameter> parameters);

    /**
     * 根据类名获取主键字段名
     *
     * @param clazz the clazz
     * @return pK name
     */
    String getPkField(Class<?> clazz);

    /**
     * 根据属性名获取列名
     *
     * @param clazzName the clazz name
     * @param fieldName the field name
     * @return column name
     */
    String getColumn(String clazzName, String fieldName);

    /**
     * 根据属性名获取列名
     *
     * @param clazz     the clazz
     * @param fieldName the field name
     * @return column name
     */
    String getColumn(Class<?> clazz, String fieldName);

    /**
     * 根据列获取属性
     *
     * @param clazz      the clazz
     * @param columnName the column name
     * @return field
     */
    String getField(Class<?> clazz, String columnName);
}

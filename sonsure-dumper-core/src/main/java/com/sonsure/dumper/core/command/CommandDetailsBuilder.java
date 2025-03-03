/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command;

import com.sonsure.dumper.common.model.Pageable;
import com.sonsure.dumper.core.command.lambda.Function;
import com.sonsure.dumper.core.config.JdbcEngineConfig;

/**
 * CommandContext构建
 * <p>
 *
 * @author liyd
 * @date 17/4/11
 */
public interface CommandDetailsBuilder {

    /**
     * From command details builder.
     *
     * @param cls the cls
     * @return the command details builder
     */
    CommandDetailsBuilder from(Class<?> cls);

    /**
     * Select command details builder.
     *
     * @param fields the fields
     * @return the command details builder
     */
    CommandDetailsBuilder addSelectFields(String... fields);

    /**
     * Add select fields command details builder.
     *
     * @param <E>      the type parameter
     * @param <R>      the type parameter
     * @param function the function
     * @return the command details builder
     */
    <E, R> CommandDetailsBuilder addSelectFields(Function<E, R> function);


    /**
     * Drop select field command details builder.
     *
     * @param fields the fields
     * @return the command details builder
     */
    CommandDetailsBuilder dropSelectFields(String... fields);

    /**
     * Drop select fields command details builder.
     *
     * @param <E>      the type parameter
     * @param <R>      the type parameter
     * @param function the function
     * @return the command details builder
     */
    <E, R> CommandDetailsBuilder dropSelectFields(Function<E, R> function);

    /**
     * Insert into
     *
     * @param cls the cls
     * @return the command details builder
     */
    CommandDetailsBuilder insertInto(Class<?> cls);

    /**
     * Into column
     *
     * @param field the field
     * @param value the value
     * @return the command details builder
     */
    CommandDetailsBuilder intoField(String field, Object value);

    /**
     * Into field
     *
     * @param <E>      the type parameter
     * @param <R>      the type parameter
     * @param function the function
     * @param value    the value
     * @return the command details builder
     */
    <E, R> CommandDetailsBuilder intoField(Function<E, R> function, Object value);

    /**
     * Into field for object command details builder.
     *
     * @param object the object
     * @return the command details builder
     */
    CommandDetailsBuilder intoFieldForObject(Object object);

    /**
     * Update command details builder.
     *
     * @param cls the cls
     * @return the command details builder
     */
    CommandDetailsBuilder update(Class<?> cls);

    /**
     * set field
     *
     * @param field the field
     * @param value the value
     * @return the command details builder
     */
    CommandDetailsBuilder setField(String field, Object value);

    /**
     * Sets field for object.
     *
     * @param object the object
     * @return the field for object
     */
    CommandDetailsBuilder setFieldForObject(Object object);

    /**
     * Sets field for object where pk.
     *
     * @param object the object
     * @return the field for object where pk
     */
    CommandDetailsBuilder setFieldForObjectWherePk(Object object);

    /**
     * set field
     *
     * @param <E>      the type parameter
     * @param <R>      the type parameter
     * @param function the function
     * @param value    the value
     * @return the command details builder
     */
    <E, R> CommandDetailsBuilder setField(Function<E, R> function, Object value);

    /**
     * Delete from
     *
     * @param cls the cls
     * @return the command details builder
     */
    CommandDetailsBuilder deleteFrom(Class<?> cls);

    /**
     * Where command
     *
     * @param field the field
     * @param value the value
     * @return the command details builder
     */
    CommandDetailsBuilder where(String field, Object value);

    /**
     * Where command details builder.
     *
     * @param field       the field
     * @param sqlOperator the sql operator
     * @param value       the value
     * @return the command details builder
     */
    CommandDetailsBuilder where(String field, SqlOperator sqlOperator, Object value);

    /**
     * Where command
     *
     * @param <E>         the type parameter
     * @param <R>         the type parameter
     * @param function    the function
     * @param sqlOperator the sql operator
     * @param value       the value
     * @return the command details builder
     */
    <E, R> CommandDetailsBuilder where(Function<E, R> function, SqlOperator sqlOperator, Object value);

    /**
     * And
     *
     * @return the command details builder
     */
    CommandDetailsBuilder and();

    /**
     * Or
     *
     * @return the command details builder
     */
    CommandDetailsBuilder or();

    /**
     * Order by command details builder.
     *
     * @param fields  the fields
     * @param orderBy the order by
     * @return the command details builder
     */
    CommandDetailsBuilder orderBy(String fields, OrderBy orderBy);

    /**
     * Order by command details builder.
     *
     * @param <E>      the type parameter
     * @param <R>      the type parameter
     * @param function the function
     * @param orderBy  the order by
     * @return the command details builder
     */
    <E, R> CommandDetailsBuilder orderBy(Function<E, R> function, OrderBy orderBy);

    /**
     * Group by command details builder.
     *
     * @param fields the fields
     * @return the command details builder
     */
    CommandDetailsBuilder groupBy(String... fields);

    /**
     * Group by command details builder.
     *
     * @param <E>      the type parameter
     * @param <R>      the type parameter
     * @param function the function
     * @return the command details builder
     */
    <E, R> CommandDetailsBuilder groupBy(Function<E, R> function);

    /**
     * update null
     *
     * @return the command details builder
     */
    CommandDetailsBuilder updateNull();

    /**
     * Force native
     *
     * @return the command details builder
     */
    CommandDetailsBuilder forceNative();

    /**
     * Named parameter
     *
     * @return the command details builder
     */
    CommandDetailsBuilder namedParameter();

    /**
     * Paginate.
     *
     * @param pageNum  the page num
     * @param pageSize the page size
     * @return the command details builder
     */
    CommandDetailsBuilder paginate(int pageNum, int pageSize);

    /**
     * Paginate.
     *
     * @param pageable the pageable
     * @return the command details builder
     */
    CommandDetailsBuilder paginate(Pageable pageable);

    /**
     * Limit.
     *
     * @param offset the offset
     * @param size   the size
     * @return the command details builder
     */
    CommandDetailsBuilder limit(int offset, int size);

    /**
     * Disable count query command details builder.
     *
     * @return the command details builder
     */
    CommandDetailsBuilder disableCountQuery();

    /**
     * 构建执行内容
     *
     * @param jdbcEngineConfig the jdbc engine config
     * @return command context
     */
    CommandDetails build(JdbcEngineConfig jdbcEngineConfig);

}

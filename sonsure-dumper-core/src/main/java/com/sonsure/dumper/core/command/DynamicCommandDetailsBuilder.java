/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command;

import com.sonsure.dumper.core.command.lambda.Function;

/**
 * CommandContext构建
 * <p>
 *
 * @param <T> the type parameter
 * @author liyd
 */
public interface DynamicCommandDetailsBuilder<T extends DynamicCommandDetailsBuilder<T>> extends CommandDetailsBuilder<T> {

    /**
     * From t.
     *
     * @param entity the entity
     * @return the t
     */
    T from(String entity);

    /**
     * Select command details builder.
     *
     * @param fields the fields
     * @return the t
     */
    T addSelectFields(String... fields);

    /**
     * Add select fields t.
     *
     * @param tableAlias the table alias
     * @param fields     the fields
     * @return the t
     */
    T addAliasSelectFields(String tableAlias, String... fields);

    /**
     * Add select fields command details builder.
     *
     * @param <E>      the type parameter
     * @param <R>      the type parameter
     * @param function the function
     * @return the t
     */
    <E, R> T addSelectFields(Function<E, R> function);


    /**
     * Drop select field
     *
     * @param fields the fields
     * @return the t
     */
    T dropSelectFields(String... fields);

    /**
     * Drop select fields command details builder.
     *
     * @param <E>      the type parameter
     * @param <R>      the type parameter
     * @param function the function
     * @return the t
     */
    <E, R> T dropSelectFields(Function<E, R> function);

    /**
     * Insert into dynamic command details builder.
     *
     * @param entity the entity
     * @return the t
     */
    T insertInto(String entity);

    /**
     * Into column
     *
     * @param field the field
     * @param value the value
     * @return the t
     */
    T intoField(String field, Object value);

    /**
     * Into field
     *
     * @param <E>      the type parameter
     * @param <R>      the type parameter
     * @param function the function
     * @param value    the value
     * @return the t
     */
    <E, R> T intoField(Function<E, R> function, Object value);

    /**
     * Into field for object
     *
     * @param object the object
     * @return the t
     */
    T intoFieldForObject(Object object);

    /**
     * Update dynamic command details builder.
     *
     * @param entity the entity
     * @return the t
     */
    T update(String entity);

    /**
     * set field
     *
     * @param field the field
     * @param value the value
     * @return the field
     */
    T setField(String field, Object value);

    /**
     * Sets field for object.
     *
     * @param object the object
     * @return the field for object
     */
    T setFieldForObject(Object object);

    /**
     * set field
     *
     * @param <E>      the type parameter
     * @param <R>      the type parameter
     * @param function the function
     * @param value    the value
     * @return the field
     */
    <E, R> T setField(Function<E, R> function, Object value);

    /**
     * Delete from dynamic command details builder.
     *
     * @param entity the entity
     * @return the t
     */
    T deleteFrom(String entity);

    /**
     * Alias t.
     *
     * @param aliasName the alias name
     * @return the t
     */
    T as(String aliasName);

    /**
     * Inner join t.
     *
     * @param table the table
     * @return the t
     */
    T innerJoin(String table);

    /**
     * On t.
     *
     * @param on the on
     * @return the t
     */
    T on(String on);

    /**
     * On select.
     *
     * @param <E1>        the type parameter
     * @param <R1>        the type parameter
     * @param <E2>        the type parameter
     * @param <R2>        the type parameter
     * @param table1Field the table 1 field
     * @param table2Field the table 2 field
     * @return the select
     */
    <E1, R1, E2, R2> T on(Function<E1, R1> table1Field, Function<E2, R2> table2Field);

//    /**
//     * On t.
//     *
//     * @param sqlPart the sql part
//     * @return the t
//     */
//    T on(SqlPart sqlPart);

    /**
     * Where t.
     *
     * @return the t
     */
    T where();

    /**
     * Where command
     *
     * @param field the field
     * @param value the value
     * @return the t
     */
    T where(String field, Object value);

    /**
     * Where
     *
     * @param field       the field
     * @param sqlOperator the sql operator
     * @param value       the value
     * @return the t
     */
    T where(String field, SqlOperator sqlOperator, Object value);

    /**
     * Where command
     *
     * @param <E>         the type parameter
     * @param <R>         the type parameter
     * @param function    the function
     * @param sqlOperator the sql operator
     * @param value       the value
     * @return the t
     */
    <E, R> T where(Function<E, R> function, SqlOperator sqlOperator, Object value);

//    /**
//     * Where t.
//     *
//     * @param sqlPart the sql part
//     * @return the t
//     */
//    T where(SqlPart sqlPart);

    /**
     * Where for object t.
     *
     * @param object the object
     * @return the t
     */
    T whereForObject(Object object);

    /**
     * Where append t.
     *
     * @param segment the segment
     * @return the t
     */
    T whereAppend(String segment);

    /**
     * Where append t.
     *
     * @param segment the segment
     * @param params   the params
     * @return the t
     */
    T whereAppend(String segment, Object params);

    /**
     * Open paren t.
     *
     * @return the c
     */
    T openParen();

    /**
     * Close paren t.
     *
     * @return the c
     */
    T closeParen();

    /**
     * And
     *
     * @return the t
     */
    T and();

    /**
     * Or
     *
     * @return the t
     */
    T or();

    /**
     * Order by command details builder.
     *
     * @param fields  the fields
     * @param orderBy the order by
     * @return the t
     */
    T orderBy(String fields, OrderBy orderBy);

    /**
     * Order by command details builder.
     *
     * @param <E>      the type parameter
     * @param <R>      the type parameter
     * @param function the function
     * @param orderBy  the order by
     * @return the t
     */
    <E, R> T orderBy(Function<E, R> function, OrderBy orderBy);

    /**
     * Group by command details builder.
     *
     * @param fields the fields
     * @return the t
     */
    T groupBy(String... fields);

    /**
     * Group by command details builder.
     *
     * @param <E>      the type parameter
     * @param <R>      the type parameter
     * @param function the function
     * @return the t
     */
    <E, R> T groupBy(Function<E, R> function);

    /**
     * update null
     *
     * @param ignoreNull the ignore null
     * @return the t
     */
    T ignoreNull(boolean ignoreNull);

}

/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;

import com.sonsure.dumper.common.utils.ClassUtils;
import com.sonsure.dumper.core.command.AbstractCommandExecutor;
import com.sonsure.dumper.core.command.build.*;
import com.sonsure.dumper.core.config.JdbcContext;

import java.util.Map;

/**
 * The type Abstract condition command executor.
 *
 * @param <E> the type parameter
 * @author liyd
 * @since 17 /4/11
 */
public abstract class AbstractConditionCommandExecutor<E extends ConditionCommandExecutor<E>> extends AbstractCommandExecutor<E> implements ConditionCommandExecutor<E> {

    public AbstractConditionCommandExecutor(JdbcContext jdbcContext) {
        super(jdbcContext);
    }

    @Override
    public E where() {
        this.getExecutableCmdBuilder().where();
        return this.getSelf();
    }

    @Override
    public E where(String field, SqlOperator sqlOperator, Object value) {
        this.getExecutableCmdBuilder().where(field, sqlOperator, value);
        return this.getSelf();
    }

    @Override
    public <T> E where(GetterFunction<T> getter, SqlOperator sqlOperator, Object value) {
        return this.where(lambda2Field(getter), sqlOperator, value);
    }

    @Override
    public <T> E where(GetterFunction<T> getter, Object value) {
        return this.where(getter, value == null ? SqlOperator.IS : SqlOperator.EQ, value);
    }

    @Override
    public E where(String field, Object value) {
        this.where(field, value == null ? SqlOperator.IS : SqlOperator.EQ, value);
        return this.getSelf();
    }

    @Override
    public E condition(String field, Object value) {
        return this.condition(field, SqlOperator.EQ, value);
    }

    @Override
    public <T> E condition(GetterFunction<T> getter, Object value) {
        return this.condition(lambda2Field(getter), SqlOperator.EQ, value);
    }

    @Override
    public E condition(String field, SqlOperator sqlOperator, Object value) {
        this.getExecutableCmdBuilder().condition(field, sqlOperator, value);
        return this.getSelf();
    }

    @Override
    public <T> E condition(GetterFunction<T> getter, SqlOperator sqlOperator, Object value) {
        return this.condition(lambda2Field(getter), sqlOperator, value);
    }

    @Override
    public E whereForBean(Object bean) {
        Map<String, Object> propMap = CommandBuildHelper.obj2PropMap(bean, getExecutableCmdBuilder().isUpdateNull());
        String tableAlias = getExecutableCmdBuilder().resolveTableAlias(bean.getClass().getSimpleName());
        for (Map.Entry<String, Object> entry : propMap.entrySet()) {
            String field = CommandBuildHelper.getTableAliasFieldName(tableAlias, entry.getKey());
            this.where(field, entry.getValue());
        }
        return this.getSelf();
    }

    @Override
    public E whereForBeanPrimaryKey(Object bean) {
        CacheEntityClassWrapper uniqueModelClass = new CacheEntityClassWrapper(bean.getClass());
        EntityClassFieldWrapper pkField = uniqueModelClass.getPrimaryKeyField();
        String fieldName = pkField.getFieldName();
        Object value = ClassUtils.getFieldValue(bean, fieldName);
        return this.where(fieldName, value);
    }

    @Override
    public E appendSegment(String segment) {
        this.getExecutableCmdBuilder().appendSegment(segment);
        return getSelf();
    }

    @Override
    public E appendSegment(String segment, Object value) {
        this.getExecutableCmdBuilder().appendSegment(segment, value);
        return getSelf();
    }

    @Override
    public E openParen() {
        this.getExecutableCmdBuilder().openParen();
        return this.getSelf();
    }

    @Override
    public E closeParen() {
        this.getExecutableCmdBuilder().closeParen();
        return this.getSelf();
    }

    @Override
    public E and() {
        this.getExecutableCmdBuilder().and();
        return this.getSelf();
    }

    @Override
    public E and(String field, Object value) {
        return this.and(field, SqlOperator.EQ, value);
    }

    @Override
    public E and(String field, SqlOperator sqlOperator, Object value) {
        this.getExecutableCmdBuilder().and(field, sqlOperator, value);
        return this.getSelf();
    }

    @Override
    public <T> E and(GetterFunction<T> getter, Object value) {
        this.and(getter, SqlOperator.EQ, value);
        return this.getSelf();
    }

    @Override
    public <T> E and(GetterFunction<T> getter, SqlOperator sqlOperator, Object value) {
        return this.and(lambda2Field(getter), sqlOperator, value);
    }

    @Override
    public E or() {
        this.getExecutableCmdBuilder().or();
        return this.getSelf();
    }

    @Override
    public E or(String field, Object value) {
        return this.or(field, SqlOperator.EQ, value);
    }

    @Override
    public E or(String field, SqlOperator sqlOperator, Object value) {
        this.getExecutableCmdBuilder().or(field, sqlOperator, value);
        return this.getSelf();
    }

    @Override
    public <T> E or(GetterFunction<T> getter, Object value) {
        return this.or(getter, SqlOperator.EQ, value);
    }

    @Override
    public <T> E or(GetterFunction<T> getter, SqlOperator sqlOperator, Object value) {
        return this.or(lambda2Field(getter), sqlOperator, value);
    }
}

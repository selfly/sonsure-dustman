/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.core.command.entity;


import com.sonsure.dumper.common.bean.BeanKit;
import com.sonsure.dumper.common.model.Page;
import com.sonsure.dumper.common.validation.Verifier;
import com.sonsure.dumper.core.command.build.*;
import com.sonsure.dumper.core.config.JdbcContext;
import com.sonsure.dumper.core.persist.PersistExecutor;

import java.util.List;
import java.util.Map;

/**
 * @author liyd
 * @since 17/4/12
 */
public class SelectImpl<M> extends AbstractConditionCommandExecutor<Select<M>> implements Select<M> {

    private final Class<M> cls;

    @SuppressWarnings("unchecked")
    public SelectImpl(JdbcContext jdbcContext, Object... params) {
        super(jdbcContext);
        this.cls = (Class<M>) params[0];
        this.registerClassToMappingHandler(cls);
        this.getExecutableCmdBuilder()
                .addCustomizer(new SelectExecutableCustomizer(this.cls))
                .from(cls.getSimpleName());
    }

    @Override
    public Select<M> as(String alias) {
        this.getExecutableCmdBuilder().as(alias);
        return this;
    }

    @Override
    public Select<M> addAllColumns() {
        Verifier.init().notNull(this.cls, "class对象不能为空").validate();
        String tableAlias = this.getExecutableCmdBuilder().resolveTableAlias(this.cls.getSimpleName());
        CacheEntityClassWrapper cacheEntityClassWrapper = new CacheEntityClassWrapper(this.cls);
        String[] fields = cacheEntityClassWrapper.getEntityFields().stream()
                .map(v -> CommandBuildHelper.getTableAliasFieldName(tableAlias, v.getFieldName())).toArray(String[]::new);
        return this.addColumn(fields);
    }

    @Override
    public Select<M> addColumn(String... fields) {
        this.getExecutableCmdBuilder().select(fields);
        return this;
    }

    @Override
    public Select<M> addAliasColumn(String tableAlias, String... fields) {
        for (String field : fields) {
            String aliasField = CommandBuildHelper.getTableAliasFieldName(tableAlias, field);
            this.addColumn(aliasField);
        }
        return this;
    }

    @Override
    public final <T> Select<M> addColumn(GetterFunction<T> getter) {
        return this.addColumn(lambda2Field(getter));
    }

    @Override
    public Select<M> dropColumn(String... fields) {
        // 如果还没有添加列，则添加所有列
        if (this.getExecutableCmdBuilder().isEmptySelectColumns()) {
            this.addAllColumns();
        }
        this.getExecutableCmdBuilder().dropSelectColumn(fields);
        return this;
    }

    @Override
    public <T> Select<M> dropColumn(GetterFunction<T> getter) {
        return this.dropColumn(lambda2Field(getter));
    }

    @Override
    public Select<M> join(String table) {
        this.getExecutableCmdBuilder().join(table);
        return this;
    }

    @Override
    public Select<M> join(Class<?> cls) {
        this.registerClassToMappingHandler(cls);
        this.getExecutableCmdBuilder().join(cls.getSimpleName());
        return this;
    }

    @Override
    public Select<M> innerJoin(String table) {
        this.getExecutableCmdBuilder().innerJoin(table);
        return this;
    }

    @Override
    public Select<M> innerJoin(Class<?> cls) {
        this.registerClassToMappingHandler(cls);
        this.getExecutableCmdBuilder().innerJoin(cls.getSimpleName());
        return this;
    }

    @Override
    public Select<M> outerJoin(String table) {
        this.getExecutableCmdBuilder().outerJoin(table);
        return this;
    }

    @Override
    public Select<M> outerJoin(Class<?> cls) {
        this.registerClassToMappingHandler(cls);
        this.getExecutableCmdBuilder().outerJoin(cls.getSimpleName());
        return this;
    }

    @Override
    public Select<M> leftJoin(String table) {
        this.getExecutableCmdBuilder().leftOuterJoin(table);
        return this;
    }

    @Override
    public Select<M> leftJoin(Class<?> cls) {
        this.registerClassToMappingHandler(cls);
        this.getExecutableCmdBuilder().leftOuterJoin(cls.getSimpleName());
        return this;
    }

    @Override
    public Select<M> rightJoin(String table) {
        this.getExecutableCmdBuilder().rightOuterJoin(table);
        return this;
    }

    @Override
    public Select<M> rightJoin(Class<?> cls) {
        this.registerClassToMappingHandler(cls);
        this.getExecutableCmdBuilder().rightOuterJoin(cls.getSimpleName());
        return this;
    }

    @Override
    public Select<M> on(String on) {
        this.getExecutableCmdBuilder().joinStepOn(on);
        return this;
    }

    @Override
    public <T1, T2> Select<M> on(GetterFunction<T1> table1Field, GetterFunction<T2> table2Field) {
        String field1 = lambda2Field(table1Field);
        String field2 = lambda2Field(table2Field);
        this.on(String.format("%s %s %s", field1, SqlOperator.EQ.getCode(), field2));
        return this;
    }

    @Override
    public Select<M> groupBy(String... fields) {
        this.getExecutableCmdBuilder().groupBy(fields);
        return this;
    }

    @Override
    public <T> Select<M> groupBy(GetterFunction<T> getter) {
        return this.groupBy(lambda2Field(getter));
    }

    @Override
    public Select<M> orderBy(String field, OrderBy orderBy) {
        this.getExecutableCmdBuilder().orderBy(field, orderBy);
        return this;
    }

    @Override
    public <T> Select<M> orderBy(GetterFunction<T> getter, OrderBy orderBy) {
        return this.orderBy(lambda2Field(getter), orderBy);
    }

    @Override
    public Select<M> having(String having, SqlOperator sqlOperator, Object value) {
        this.getExecutableCmdBuilder().having(having, sqlOperator, value);
        return this;
    }

    @Override
    public <T> Select<M> having(GetterFunction<T> getter, SqlOperator sqlOperator, Object value) {
        return this.having(lambda2Field(getter), sqlOperator, value);
    }

    @Override
    public long findCount() {
        this.getExecutableCmdBuilder().executionType(ExecutionType.FIND_ONE_FOR_SCALAR);
        this.getExecutableCmdBuilder().resultType(Long.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        PersistExecutor persistExecutor = this.getJdbcContext().getPersistExecutor();
        String databaseProduct = persistExecutor.getDatabaseProduct();
        String countCommand = this.getJdbcContext().getPageHandler().getCountCommand(executableCmd.getCommand(), databaseProduct);
        ExecutableCmd countExecutableCmd = BeanKit.copyProperties(new ExecutableCmd(), executableCmd);
        countExecutableCmd.setCommand(countCommand);
        countExecutableCmd.setResultType(Long.class);
        Object result = persistExecutor.execute(countExecutableCmd);
        return (Long) result;
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> T findOne(Class<T> cls) {
        this.registerClassToMappingHandler(cls);
        this.getExecutableCmdBuilder().executionType(ExecutionType.FIND_ONE);
        this.getExecutableCmdBuilder().resultType(cls);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return (T) this.getJdbcContext().getPersistExecutor().execute(executableCmd);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findList(Class<T> cls) {
        this.registerClassToMappingHandler(cls);
        this.getExecutableCmdBuilder().executionType(ExecutionType.FIND_LIST);
        this.getExecutableCmdBuilder().resultType(cls);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return (List<T>) this.getJdbcContext().getPersistExecutor().execute(executableCmd);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Page<T> findPage(Class<T> cls) {
        this.registerClassToMappingHandler(cls);
        this.getExecutableCmdBuilder().executionType(ExecutionType.FIND_LIST);
        this.getExecutableCmdBuilder().resultType(cls);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return this.doPageResult(executableCmd, cmd -> (List<T>) getJdbcContext().getPersistExecutor().execute(cmd));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Page<Map<String, Object>> findPageForMap() {
        this.getExecutableCmdBuilder().executionType(ExecutionType.FIND_LIST_FOR_MAP);
        this.getExecutableCmdBuilder().resultType(Page.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return this.doPageResult(executableCmd, cmd -> (List<Map<String, Object>>) getJdbcContext().getPersistExecutor().execute(cmd));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Page<T> findPageForScalar(Class<T> clazz) {
        this.getExecutableCmdBuilder().executionType(ExecutionType.FIND_LIST_FOR_SCALAR);
        this.getExecutableCmdBuilder().resultType(clazz);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return this.doPageResult(executableCmd, cmd -> (List<T>) getJdbcContext().getPersistExecutor().execute(cmd));
    }

    @Override
    public M findOne() {
        return this.findOne(this.cls);
    }

    @Override
    public M findFirst() {
        return this.findFirst(this.cls);
    }

    @Override
    public List<M> findList() {
        return this.findList(cls);
    }

    @Override
    public Page<M> findPage() {
        return this.findPage(cls);
    }

    private static class SelectExecutableCustomizer implements ExecutableCustomizer {

        private final Class<?> cls;

        public SelectExecutableCustomizer(Class<?> cls) {
            this.cls = cls;
        }

        @Override
        public void customize(ExecutableCmdBuilder executableCmdBuilder) {
            if (cls == null) {
                return;
            }
            if (executableCmdBuilder.isEmptySelectColumns()) {
                CacheEntityClassWrapper entityClassWrapper = new CacheEntityClassWrapper(cls);
                String alias = executableCmdBuilder.resolveTableAlias(cls.getSimpleName());
                String[] fields = entityClassWrapper.getEntityFields().stream()
                        .map(v -> CommandBuildHelper.getTableAliasFieldName(alias, v.getFieldName())).toArray(String[]::new);
                executableCmdBuilder.select(fields);
            }
        }
    }
}

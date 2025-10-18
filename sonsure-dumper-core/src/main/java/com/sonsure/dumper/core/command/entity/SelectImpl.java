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
import com.sonsure.dumper.core.command.*;
import com.sonsure.dumper.core.command.build.ExecutableCmd;
import com.sonsure.dumper.core.command.build.ExecutableCmdBuilder;
import com.sonsure.dumper.core.command.build.ExecutableCustomizer;
import com.sonsure.dumper.core.command.lambda.Function;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
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
    public SelectImpl(JdbcEngineConfig jdbcEngineConfig, Object... params) {
        super(jdbcEngineConfig);
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
        ModelClassWrapper modelClassWrapper = new ModelClassWrapper(this.cls);
        String[] fields = modelClassWrapper.getModelFields().stream()
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
    public final <T, R> Select<M> addColumn(Function<T, R> function) {
        this.getExecutableCmdBuilder().select(function);
        return this;
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
    public <T, R> Select<M> dropColumn(Function<T, R> function) {
        this.getExecutableCmdBuilder().dropSelectColumn(function);
        return this;
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
    public Select<M> having(String having) {
        return null;
    }

    @Override
    public Select<M> on(String on) {
        this.getExecutableCmdBuilder().joinStepOn(on);
        return this;
    }

    @Override
    public <T1, R1, T2, R2> Select<M> on(Function<T1, R1> table1Field, Function<T2, R2> table2Field) {
        this.getExecutableCmdBuilder().joinStepOn(table1Field, table2Field);
        return this;
    }

    @Override
    public Select<M> groupBy(String... fields) {
        this.getExecutableCmdBuilder().groupBy(fields);
        return this;
    }

    @Override
    public <T, R> Select<M> groupBy(Function<T, R> function) {
        this.getExecutableCmdBuilder().groupBy(function);
        return this;
    }

    @Override
    public Select<M> orderBy(String field, OrderBy orderBy) {
        this.getExecutableCmdBuilder().orderBy(field, orderBy);
        return this;
    }

    @Override
    public <T, R> Select<M> orderBy(Function<T, R> function, OrderBy orderBy) {
        this.getExecutableCmdBuilder().orderBy(function, orderBy);
        return this;
    }

    @Override
    public Select<M> paginate(int pageNum, int pageSize) {
        this.getExecutableCmdBuilder().paginate(pageNum, pageSize);
        return this;
    }

    @Override
    public Select<M> limit(int offset, int size) {
        this.getExecutableCmdBuilder().limit(offset, size);
        return this;
    }

    @Override
    public Select<M> disableCount() {
        this.getExecutableCmdBuilder().disableCountQuery();
        return this;
    }

    @Override
    public long count() {
        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_ONE_COL);
        this.getExecutableCmdBuilder().resultType(Long.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        PersistExecutor persistExecutor = this.getJdbcEngineConfig().getPersistExecutor();
        String countCommand = this.getJdbcEngineConfig().getPageHandler().getCountCommand(executableCmd.getCommand(), persistExecutor.getDialect());
        ExecutableCmd countExecutableCmd = BeanKit.copyProperties(new ExecutableCmd(), executableCmd);
        countExecutableCmd.setCommand(countCommand);
        countExecutableCmd.setResultType(Long.class);
        Object result = persistExecutor.execute(countExecutableCmd);
        return (Long) result;
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> T singleResult(Class<T> cls) {
        this.registerClassToMappingHandler(cls);
        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_SINGLE_RESULT);
        this.getExecutableCmdBuilder().resultType(cls);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return (T) this.getJdbcEngineConfig().getPersistExecutor().execute(executableCmd);
    }


    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> singleMapResult() {
        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_FOR_MAP);
        this.getExecutableCmdBuilder().resultType(Map.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return (Map<String, Object>) this.getJdbcEngineConfig().getPersistExecutor().execute(executableCmd);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T oneColResult(Class<T> clazz) {
        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_ONE_COL);
        this.getExecutableCmdBuilder().resultType(clazz);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return (T) this.getJdbcEngineConfig().getPersistExecutor().execute(executableCmd);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> oneColList(Class<T> clazz) {
        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_ONE_COL_LIST);
        this.getExecutableCmdBuilder().resultType(clazz);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return (List<T>) this.getJdbcEngineConfig().getPersistExecutor().execute(executableCmd);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> list(Class<T> cls) {
        this.registerClassToMappingHandler(cls);
        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_FOR_LIST);
        this.getExecutableCmdBuilder().resultType(cls);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return (List<T>) this.getJdbcEngineConfig().getPersistExecutor().execute(executableCmd);
    }


    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> listMaps() {
        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_FOR_MAP_LIST);
        this.getExecutableCmdBuilder().resultType(List.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return (List<Map<String, Object>>) this.getJdbcEngineConfig().getPersistExecutor().execute(executableCmd);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Page<T> pageResult(Class<T> cls) {
        this.registerClassToMappingHandler(cls);
        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_FOR_LIST);
        this.getExecutableCmdBuilder().resultType(cls);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return this.doPageResult(executableCmd, cmd -> (List<T>) getJdbcEngineConfig().getPersistExecutor().execute(cmd));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Page<Map<String, Object>> pageMapResult() {
        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_FOR_MAP_LIST);
        this.getExecutableCmdBuilder().resultType(Page.class);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return this.doPageResult(executableCmd, cmd -> (List<Map<String, Object>>) getJdbcEngineConfig().getPersistExecutor().execute(cmd));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Page<T> oneColPageResult(Class<T> clazz) {
        this.getExecutableCmdBuilder().executionType(ExecutionType.QUERY_ONE_COL_LIST);
        this.getExecutableCmdBuilder().resultType(clazz);
        ExecutableCmd executableCmd = this.getExecutableCmdBuilder().build();
        return this.doPageResult(executableCmd, cmd -> (List<T>) getJdbcEngineConfig().getPersistExecutor().execute(cmd));
    }

    @Override
    public M singleResult() {
        return this.singleResult(this.cls);
    }

    @Override
    public M firstResult() {
        return this.firstResult(this.cls);
    }

    @Override
    public List<M> list() {
        return this.list(cls);
    }

    @Override
    public Page<M> pageResult() {
        return this.pageResult(cls);
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
                ModelClassDetails modelClassDetails = new ModelClassDetails(cls);
                String alias = executableCmdBuilder.resolveTableAlias(cls.getSimpleName());
                String[] fields = modelClassDetails.getModelFields().stream()
                        .map(v -> CommandBuildHelper.getTableAliasFieldName(alias, v.getFieldName())).toArray(String[]::new);
                executableCmdBuilder.select(fields);
            }
        }
    }
}

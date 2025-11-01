/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.springjdbc.persist;

import com.sonsure.dumper.core.command.batch.BatchExecutableCmd;
import com.sonsure.dumper.core.command.batch.ParameterizedSetter;
import com.sonsure.dumper.core.command.build.ExecutableCmd;
import com.sonsure.dumper.core.command.build.GenerateKey;
import com.sonsure.dumper.core.persist.AbstractPersistExecutor;
import com.sonsure.dumper.core.persist.ExecutionFunction;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.NonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author liyd
 * @since 17/4/12
 */
public class JdbcTemplatePersistExecutor extends AbstractPersistExecutor {

    private final JdbcOperations jdbcOperations;

    public JdbcTemplatePersistExecutor(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public <R> R executeInConnection(ExecutionFunction<Connection, R> function) {
        return jdbcOperations.execute(function::apply);
    }

    @Override
    public Object insert(final ExecutableCmd executableCmd) {
        final GenerateKey generateKey = executableCmd.getGenerateKey();
        //数据库自增 或设置主键值 处理
        if (generateKey != null && !generateKey.isPrimaryKeyParameter()) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcOperations.update(new InsertPreparedStatementCreator(executableCmd, generateKey), keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            //显示指定主键时为null
            if (keys == null || keys.isEmpty()) {
                return null;
            } else if (keys.size() == 1) {
                Object obj = keys.values().iterator().next();
                //Spring 5 return BigInteger
                if (obj instanceof Number) {
                    return ((Number) obj).longValue();
                }
                return obj;
            } else {
                return keys;
            }
        } else {
            jdbcOperations.update(executableCmd.getCommand(), executableCmd.getParsedParameterValues().toArray());
            //显示指定了主键，可能为null
            return Optional.ofNullable(generateKey).map(GenerateKey::getValue).orElse(null);
        }
    }

    @Override
    public List<?> queryForList(ExecutableCmd executableCmd) {
        return jdbcOperations.query(executableCmd.getCommand(), JdbcRowMapper.newInstance(executableCmd.getJdbcContext(), executableCmd.getResultType()), executableCmd.getParsedParameterValues().toArray());
    }

    @Override
    public Object querySingleResult(ExecutableCmd executableCmd) {
        //采用list方式查询，当记录不存在时返回null而不会抛出异常,多于一条时会抛异常
        List<?> list = jdbcOperations.query(executableCmd.getCommand(), JdbcRowMapper.newInstance(executableCmd.getJdbcContext(), executableCmd.getResultType()), executableCmd.getParsedParameterValues().toArray());
        return DataAccessUtils.singleResult(list);
    }

    @Override
    public Map<String, Object> queryForMap(ExecutableCmd executableCmd) {
        //直接queryForMap没有记录时会抛出异常，采用list方式查询，当记录不存在时返回null而不会抛出异常,多于一条时会抛异常
        List<Map<String, Object>> maps = jdbcOperations.queryForList(executableCmd.getCommand(), executableCmd.getParsedParameterValues().toArray());
        return DataAccessUtils.singleResult(maps);
    }

    @Override
    public List<Map<String, Object>> queryForMapList(ExecutableCmd executableCmd) {
        return jdbcOperations.queryForList(executableCmd.getCommand(), executableCmd.getParsedParameterValues().toArray());
    }

    @Override
    public Object queryOneCol(ExecutableCmd executableCmd) {
        return jdbcOperations.queryForObject(executableCmd.getCommand(), executableCmd.getResultType(), executableCmd.getParsedParameterValues().toArray());
    }

    @Override
    public List<?> queryOneColList(ExecutableCmd executableCmd) {
        return jdbcOperations.queryForList(executableCmd.getCommand(), executableCmd.getResultType(), executableCmd.getParsedParameterValues().toArray());
    }

    @Override
    public int update(ExecutableCmd executableCmd) {
        return jdbcOperations.update(executableCmd.getCommand(), executableCmd.getParsedParameterValues().toArray());
    }

    @Override
    protected <T> Object batchUpdate(BatchExecutableCmd<T> batchExecutableCmd) {
        final ParameterizedSetter<T> parameterizedSetter = batchExecutableCmd.getParameterizedSetter();
        return jdbcOperations.batchUpdate(batchExecutableCmd.getCommand(), batchExecutableCmd.getBatchData(), batchExecutableCmd.getBatchSize(), (ps, argument) -> parameterizedSetter.setValues(ps, batchExecutableCmd.getParsedParameterNames(), argument));
    }

    @Override
    public int delete(ExecutableCmd executableCmd) {
        return jdbcOperations.update(executableCmd.getCommand(), executableCmd.getParsedParameterValues().toArray());
    }

    @Override
    public Object doExecute(ExecutableCmd executableCmd) {
        jdbcOperations.execute(executableCmd.getCommand());
        return true;
    }

    @Override
    protected Object doExecuteScript(ExecutableCmd executableCmd) {
        return jdbcOperations.execute((ConnectionCallback<Void>) connection -> {
            ScriptUtils.executeSqlScript(connection, new ByteArrayResource(executableCmd.getCommand().getBytes()));
            return null;
        });
    }

    private static class InsertPreparedStatementCreator implements PreparedStatementCreator, PreparedStatementSetter, SqlProvider {

        private final ExecutableCmd executableCmd;

        private final GenerateKey generateKey;

        public InsertPreparedStatementCreator(ExecutableCmd executableCmd, GenerateKey generateKey) {
            this.executableCmd = executableCmd;
            this.generateKey = generateKey;
        }

        @Override
        @NonNull
        public PreparedStatement createPreparedStatement(@NonNull Connection con) throws SQLException {
            PreparedStatement ps = generateKey.getColumn() == null ? con.prepareStatement(executableCmd.getCommand(), PreparedStatement.RETURN_GENERATED_KEYS) : con.prepareStatement(executableCmd.getCommand(), new String[]{generateKey.getColumn()});
            setValues(ps);
            return ps;
        }

        @Override
        public void setValues(@NonNull PreparedStatement ps) throws SQLException {
            ArgumentPreparedStatementSetter pss = new ArgumentPreparedStatementSetter(executableCmd.getParsedParameterValues().toArray());
            pss.setValues(ps);
        }

        @Override
        public String getSql() {
            return executableCmd.getCommand();
        }
    }
}

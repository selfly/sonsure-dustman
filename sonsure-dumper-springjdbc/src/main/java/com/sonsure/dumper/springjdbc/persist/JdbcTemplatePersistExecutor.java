/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.springjdbc.persist;

import com.sonsure.dumper.core.command.CommandDetails;
import com.sonsure.dumper.core.command.GenerateKey;
import com.sonsure.dumper.core.command.batch.BatchCommandDetails;
import com.sonsure.dumper.core.command.batch.ParameterizedSetter;
import com.sonsure.dumper.core.persist.AbstractPersistExecutor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.NonNull;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author liyd
 * @date 17/4/12
 */
public class JdbcTemplatePersistExecutor extends AbstractPersistExecutor {

    private final JdbcOperations jdbcOperations;

    public JdbcTemplatePersistExecutor(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    protected String doGetDialect() {
        return jdbcOperations.execute((ConnectionCallback<String>) con -> {
            final DatabaseMetaData metaData = con.getMetaData();
            return metaData.getDatabaseProductName().toLowerCase() + "/" + metaData.getDatabaseProductVersion();
        });
    }

    @Override
    public Object insert(final CommandDetails commandDetails) {
        final GenerateKey generateKey = commandDetails.getGenerateKey();
        //数据库自增 或设置主键值 处理
        if (generateKey != null && !generateKey.isPrimaryKeyParameter()) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcOperations.update(new InsertPreparedStatementCreator(commandDetails, generateKey), keyHolder);
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
            jdbcOperations.update(commandDetails.getCommand(), commandDetails.getCommandParameters().getParsedParameterValues().toArray());
            //显示指定了主键，可能为null
            return Optional.ofNullable(generateKey).map(GenerateKey::getValue).orElse(null);
        }
    }

    @Override
    public List<?> queryForList(CommandDetails commandDetails) {
        return jdbcOperations.query(commandDetails.getCommand(), commandDetails.getCommandParameters().getParsedParameterValues().toArray(), JdbcRowMapper.newInstance(this.getDialect(), this.getJdbcEngineConfig(), commandDetails.getResultType()));
    }

    @Override
    public Object querySingleResult(CommandDetails commandDetails) {
        //采用list方式查询，当记录不存在时返回null而不会抛出异常,多于一条时会抛异常
        List<?> list = jdbcOperations.query(commandDetails.getCommand(), commandDetails.getCommandParameters().getParsedParameterValues().toArray(), JdbcRowMapper.newInstance(this.getDialect(), this.getJdbcEngineConfig(), commandDetails.getResultType()));
        return DataAccessUtils.singleResult(list);
    }

    @Override
    public Map<String, Object> queryForMap(CommandDetails commandDetails) {
        //直接queryForMap没有记录时会抛出异常，采用list方式查询，当记录不存在时返回null而不会抛出异常,多于一条时会抛异常
        List<Map<String, Object>> maps = jdbcOperations.queryForList(commandDetails.getCommand(), commandDetails.getCommandParameters().getParsedParameterValues().toArray());
        return DataAccessUtils.singleResult(maps);
    }

    @Override
    public List<Map<String, Object>> queryForMapList(CommandDetails commandDetails) {
        return jdbcOperations.queryForList(commandDetails.getCommand(), commandDetails.getCommandParameters().getParsedParameterValues().toArray());
    }

    @Override
    public Object queryOneCol(CommandDetails commandDetails) {
        return jdbcOperations.queryForObject(commandDetails.getCommand(), commandDetails.getCommandParameters().getParsedParameterValues().toArray(), commandDetails.getResultType());
    }

    @Override
    public List<?> queryOneColList(CommandDetails commandDetails) {
        return jdbcOperations.queryForList(commandDetails.getCommand(), commandDetails.getResultType(), commandDetails.getCommandParameters().getParsedParameterValues().toArray());
    }

    @Override
    public int update(CommandDetails commandDetails) {
        return jdbcOperations.update(commandDetails.getCommand(), commandDetails.getCommandParameters().getParsedParameterValues().toArray());
    }

    @Override
    protected <T> Object batchUpdate(BatchCommandDetails<T> commandContext) {
        final ParameterizedSetter<T> parameterizedSetter = commandContext.getParameterizedSetter();
        return jdbcOperations.batchUpdate(commandContext.getCommand(), commandContext.getBatchData(), commandContext.getBatchSize(), (ps, argument) -> parameterizedSetter.setValues(ps, commandContext.getCommandParameters().getParsedParameterNames(), argument));
    }

    @Override
    public int delete(CommandDetails commandDetails) {
        return jdbcOperations.update(commandDetails.getCommand(), commandDetails.getCommandParameters().getParsedParameterValues().toArray());
    }

    @Override
    public Object doExecute(CommandDetails commandDetails) {
        jdbcOperations.execute(commandDetails.getCommand());
        return true;
    }

    @Override
    protected Object doExecuteScript(CommandDetails commandDetails) {
        return jdbcOperations.execute((ConnectionCallback<Void>) connection -> {
            ScriptUtils.executeSqlScript(connection, new ByteArrayResource(commandDetails.getCommand().getBytes()));
            return null;
        });
    }

    private static class InsertPreparedStatementCreator implements PreparedStatementCreator, PreparedStatementSetter, SqlProvider {

        private final CommandDetails commandDetails;

        private final GenerateKey generateKey;

        public InsertPreparedStatementCreator(CommandDetails commandDetails, GenerateKey generateKey) {
            this.commandDetails = commandDetails;
            this.generateKey = generateKey;
        }

        @Override
        @NonNull
        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            PreparedStatement ps = generateKey.getColumn() == null ? con.prepareStatement(commandDetails.getCommand(), PreparedStatement.RETURN_GENERATED_KEYS) : con.prepareStatement(commandDetails.getCommand(), new String[]{generateKey.getColumn()});
            setValues(ps);
            return ps;
        }

        @Override
        public void setValues(@NonNull PreparedStatement ps) throws SQLException {
            ArgumentPreparedStatementSetter pss = new ArgumentPreparedStatementSetter(commandDetails.getCommandParameters().getParsedParameterValues().toArray());
            pss.setValues(ps);
        }

        @Override
        public String getSql() {
            return commandDetails.getCommand();
        }
    }
}

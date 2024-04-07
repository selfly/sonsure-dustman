/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.springjdbc.persist;

import com.sonsure.dumper.core.command.CommandContext;
import com.sonsure.dumper.core.command.GenerateKey;
import com.sonsure.dumper.core.command.batch.BatchCommandContext;
import com.sonsure.dumper.core.command.batch.ParameterizedSetter;
import com.sonsure.dumper.core.persist.AbstractPersistExecutor;
import org.apache.commons.lang3.StringUtils;
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
    public Object insert(final CommandContext commandContext) {
        final GenerateKey generateKey = commandContext.getGenerateKey();
        //数据库自增 或设置主键值 处理
        if (!generateKey.isPkIsParamName() && StringUtils.isNotBlank(generateKey.getColumn())) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcOperations.update(new InsertPreparedStatementCreator(commandContext, generateKey), keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            //显示指定主键时为null，只有一个主键列，多个不支持
            if (keys == null) {
                return null;
            }
            Object obj = keys.values().iterator().next();
            //Spring 5 return BigInteger
            if (obj instanceof Number) {
                return ((Number) obj).longValue();
            }
            return obj;
        } else {
            jdbcOperations.update(commandContext.getCommand(), commandContext.getParameters().toArray());
            //显示指定了主键，可能为null
            return generateKey.getValue();
        }
    }

    @Override
    public List<?> queryForList(CommandContext commandContext) {
        return jdbcOperations.query(commandContext.getCommand(), commandContext.getParameters().toArray(), JdbcRowMapper.newInstance(this.getDialect(), this.getJdbcEngineConfig(), commandContext.getResultType()));
    }

    @Override
    public Object querySingleResult(CommandContext commandContext) {
        //采用list方式查询，当记录不存在时返回null而不会抛出异常,多于一条时会抛异常
        List<?> list = jdbcOperations.query(commandContext.getCommand(), commandContext.getParameters().toArray(), JdbcRowMapper.newInstance(this.getDialect(), this.getJdbcEngineConfig(), commandContext.getResultType()));
        return DataAccessUtils.singleResult(list);
    }

    @Override
    public Map<String, Object> queryForMap(CommandContext commandContext) {
        //直接queryForMap没有记录时会抛出异常，采用list方式查询，当记录不存在时返回null而不会抛出异常,多于一条时会抛异常
        List<Map<String, Object>> maps = jdbcOperations.queryForList(commandContext.getCommand(), commandContext.getParameters().toArray());
        return DataAccessUtils.singleResult(maps);
    }

    @Override
    public List<Map<String, Object>> queryForMapList(CommandContext commandContext) {
        return jdbcOperations.queryForList(commandContext.getCommand(), commandContext.getParameters().toArray());
    }

    @Override
    public Object queryOneCol(CommandContext commandContext) {
        return jdbcOperations.queryForObject(commandContext.getCommand(), commandContext.getParameters().toArray(), commandContext.getResultType());
    }

    @Override
    public List<?> queryOneColList(CommandContext commandContext) {
        return jdbcOperations.queryForList(commandContext.getCommand(), commandContext.getResultType(), commandContext.getParameters().toArray());
    }

    @Override
    public int update(CommandContext commandContext) {
        return jdbcOperations.update(commandContext.getCommand(), commandContext.getParameters().toArray());
    }

    @Override
    protected <T> Object batchUpdate(BatchCommandContext<T> commandContext) {
        final ParameterizedSetter<T> parameterizedSetter = commandContext.getParameterizedSetter();
        return jdbcOperations.batchUpdate(commandContext.getCommand(), commandContext.getBatchData(), commandContext.getBatchSize(), (ps, argument) -> parameterizedSetter.setValues(ps, commandContext.getNamedParamNames(), argument));
    }

    @Override
    public int delete(CommandContext commandContext) {
        return jdbcOperations.update(commandContext.getCommand(), commandContext.getParameters().toArray());
    }

    @Override
    public Object doExecute(CommandContext commandContext) {
        jdbcOperations.execute(commandContext.getCommand());
        return true;
    }

    @Override
    protected Object doExecuteScript(CommandContext commandContext) {
        return jdbcOperations.execute((ConnectionCallback<Void>) connection -> {
            ScriptUtils.executeSqlScript(connection, new ByteArrayResource(commandContext.getCommand().getBytes()));
            return null;
        });
    }

    private static class InsertPreparedStatementCreator implements PreparedStatementCreator, PreparedStatementSetter, SqlProvider {

        private final CommandContext commandContext;

        private final GenerateKey generateKey;

        public InsertPreparedStatementCreator(CommandContext commandContext, GenerateKey generateKey) {
            this.commandContext = commandContext;
            this.generateKey = generateKey;
        }

        @Override
        @NonNull
        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            PreparedStatement ps = con.prepareStatement(commandContext.getCommand(), new String[]{generateKey.getColumn()});
            setValues(ps);
            return ps;
        }

        @Override
        public void setValues(@NonNull PreparedStatement ps) throws SQLException {
            ArgumentPreparedStatementSetter pss = new ArgumentPreparedStatementSetter(commandContext.getParameters()
                    .toArray());
            pss.setValues(ps);
        }

        @Override
        public String getSql() {
            return commandContext.getCommand();
        }
    }
}

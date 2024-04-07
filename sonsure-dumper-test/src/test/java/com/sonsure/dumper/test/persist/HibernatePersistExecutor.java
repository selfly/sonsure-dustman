/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.persist;

import com.sonsure.dumper.core.command.CommandContext;
import com.sonsure.dumper.core.command.batch.BatchCommandContext;
import com.sonsure.dumper.core.persist.AbstractPersistExecutor;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.query.NativeQuery;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by liyd on 17/4/12.
 */
@Setter
public class HibernatePersistExecutor extends AbstractPersistExecutor {

    private SessionFactory sessionFactory;

    @Override
    protected String doGetDialect() {
        Session session = sessionFactory.openSession();
        String dialect = session.doReturningWork(new ReturningWork<String>() {
            @Override
            public String execute(Connection connection) throws SQLException {
                return connection.getMetaData().getDatabaseProductName().toLowerCase();
            }
        });
        session.close();
        return dialect;
    }

    @Override
    public Object insert(final CommandContext commandContext) {
        return null;
    }

    @Override
    public List<?> queryForList(CommandContext commandContext) {
        Session session = sessionFactory.openSession();
        NativeQuery<?> nativeQuery = session.createNativeQuery(commandContext.getCommand(), commandContext.getResultType());
        List<Object> parameters = commandContext.getParameters();
        for (int i = 0; i < parameters.size(); i++) {
            nativeQuery.setParameter(i + 1, parameters.get(i));
        }
        List<?> resultList = nativeQuery.getResultList();
        session.close();
        return resultList;
    }

    @Override
    public Object querySingleResult(CommandContext commandContext) {
        return null;
    }

    @Override
    public Map<String, Object> queryForMap(CommandContext commandContext) {
        return null;
    }

    @Override
    public List<Map<String, Object>> queryForMapList(CommandContext commandContext) {
        return null;
    }

    @Override
    public Object queryOneCol(CommandContext commandContext) {
        return null;
    }

    @Override
    public List<?> queryOneColList(CommandContext commandContext) {
        return null;
    }

    @Override
    public int update(CommandContext commandContext) {
        return 0;
    }

    @Override
    public int delete(CommandContext commandContext) {
        return 0;
    }

    @Override
    protected <T> Object batchUpdate(BatchCommandContext<T> commandContext) {
        return null;
    }

    @Override
    public Object doExecute(CommandContext commandContext) {
        return this.update(commandContext);
    }

    @Override
    protected Object doExecuteScript(CommandContext commandContext) {
        return null;
    }

}

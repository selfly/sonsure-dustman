/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.persist;

import com.sonsure.dumper.core.command.batch.BatchExecutableCmd;
import com.sonsure.dumper.core.command.build.ExecutableCmd;
import com.sonsure.dumper.core.persist.AbstractPersistExecutor;
import com.sonsure.dumper.core.persist.ExecutionFunction;
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
    public <R> R executeInConnection(ExecutionFunction<Connection, R> function) {
        Session session = sessionFactory.openSession();
        R r = session.doReturningWork(new ReturningWork<R>() {
            @Override
            public R execute(Connection connection) throws SQLException {
                return function.apply(connection);
            }
        });
        session.close();
        return r;
    }

    @Override
    public Object insert(final ExecutableCmd executableCmd) {
        return null;
    }

    @Override
    public List<?> queryForList(ExecutableCmd executableCmd) {
        Session session = sessionFactory.openSession();
        NativeQuery<?> nativeQuery = session.createNativeQuery(executableCmd.getCommand(), executableCmd.getResultType());
        List<Object> parameters = executableCmd.getParsedParameterValues();
        for (int i = 0; i < parameters.size(); i++) {
            nativeQuery.setParameter(i + 1, parameters.get(i));
        }
        List<?> resultList = nativeQuery.getResultList();
        session.close();
        return resultList;
    }

    @Override
    public Object querySingleResult(ExecutableCmd executableCmd) {
        return null;
    }

    @Override
    public Map<String, Object> queryForMap(ExecutableCmd executableCmd) {
        return null;
    }

    @Override
    public List<Map<String, Object>> queryForMapList(ExecutableCmd executableCmd) {
        return null;
    }

    @Override
    public Object queryOneCol(ExecutableCmd executableCmd) {
        return null;
    }

    @Override
    public List<?> queryOneColList(ExecutableCmd executableCmd) {
        return null;
    }

    @Override
    public int update(ExecutableCmd executableCmd) {
        return 0;
    }

    @Override
    public int delete(ExecutableCmd executableCmd) {
        return 0;
    }

    @Override
    protected <T> Object batchUpdate(BatchExecutableCmd<T> commandContext) {
        return null;
    }

    @Override
    public Object doExecute(ExecutableCmd executableCmd) {
        return this.update(executableCmd);
    }

    @Override
    protected Object doExecuteScript(ExecutableCmd executableCmd) {
        return null;
    }

}

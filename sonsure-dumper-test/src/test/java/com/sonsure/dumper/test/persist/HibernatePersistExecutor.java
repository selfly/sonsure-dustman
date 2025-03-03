/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.persist;

import com.sonsure.dumper.core.command.CommandDetails;
import com.sonsure.dumper.core.command.batch.BatchCommandDetails;
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
    public Object insert(final CommandDetails commandDetails) {
        return null;
    }

    @Override
    public List<?> queryForList(CommandDetails commandDetails) {
        Session session = sessionFactory.openSession();
        NativeQuery<?> nativeQuery = session.createNativeQuery(commandDetails.getCommand(), commandDetails.getResultType());
        List<Object> parameters = commandDetails.getParameters();
        for (int i = 0; i < parameters.size(); i++) {
            nativeQuery.setParameter(i + 1, parameters.get(i));
        }
        List<?> resultList = nativeQuery.getResultList();
        session.close();
        return resultList;
    }

    @Override
    public Object querySingleResult(CommandDetails commandDetails) {
        return null;
    }

    @Override
    public Map<String, Object> queryForMap(CommandDetails commandDetails) {
        return null;
    }

    @Override
    public List<Map<String, Object>> queryForMapList(CommandDetails commandDetails) {
        return null;
    }

    @Override
    public Object queryOneCol(CommandDetails commandDetails) {
        return null;
    }

    @Override
    public List<?> queryOneColList(CommandDetails commandDetails) {
        return null;
    }

    @Override
    public int update(CommandDetails commandDetails) {
        return 0;
    }

    @Override
    public int delete(CommandDetails commandDetails) {
        return 0;
    }

    @Override
    protected <T> Object batchUpdate(BatchCommandDetails<T> commandContext) {
        return null;
    }

    @Override
    public Object doExecute(CommandDetails commandDetails) {
        return this.update(commandDetails);
    }

    @Override
    protected Object doExecuteScript(CommandDetails commandDetails) {
        return null;
    }

}

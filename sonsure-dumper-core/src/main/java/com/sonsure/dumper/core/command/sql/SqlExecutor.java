package com.sonsure.dumper.core.command.sql;

import com.sonsure.dumper.core.command.lambda.Function;
import com.sonsure.dumper.core.command.simple.SimpleCommandExecutor;

/**
 * @author selfly
 */
interface SqlExecutor extends SimpleCommandExecutor<SqlExecutor> {


    /**
     * Update sql executor.
     *
     * @param cls the cls
     * @return the sql executor
     */
    SqlExecutor update(Class<?> cls);


    <E, R> SqlExecutor set(Function<E, R> function);


    SqlExecutor insertInto(String tableName);


    SqlExecutor values(String columns, String values);


    SqlExecutor intoColumns(String... columns);

    <E, R> SqlExecutor intoColumns(Function<E, R>... function);


    SqlExecutor intoValues(String... values);


    SqlExecutor select(String columns);


    SqlExecutor selectDistinct(String columns);


    SqlExecutor deleteFrom(String table);


    SqlExecutor from(String table);


    SqlExecutor from(String... tables);


    SqlExecutor join(String join);


    SqlExecutor join(String... joins);


    SqlExecutor innerJoin(String join);


    SqlExecutor leftOuterJoin(String join);


    SqlExecutor rightOuterJoin(String join);


    SqlExecutor outerJoin(String join);

    SqlExecutor where(String conditions);

    SqlExecutor or();


    SqlExecutor and();


    SqlExecutor groupBy(String columns);


    SqlExecutor having(String conditions);


    SqlExecutor orderBy(String columns);


}

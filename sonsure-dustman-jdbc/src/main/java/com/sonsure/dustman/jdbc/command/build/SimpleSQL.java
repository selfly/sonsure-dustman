/*
 *    Copyright 2009-2022 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.sonsure.dustman.jdbc.command.build;


import java.util.List;

/**
 * 修改父类 sql()、SQLStatement 为 protected
 *
 * @author selfly
 */
public class SimpleSQL extends AbstractSQL<SimpleSQL> {


    @Override
    public SimpleSQL getSelf() {
        return this;
    }

//    /**
//     * Clear select columns command sql.
//     *
//     * @return the command sql
//     */
//    public SimpleSQL clearSelectColumns() {
//        this.sql().select.clear();
//        return getSelf();
//    }

    public SimpleSQL appendSegment(String segment) {
        //默认到 where
        if (this.sql().lastList.isEmpty()) {
            this.sql().where.add(segment);
        } else {
            this.sql().lastList.add(segment);
        }
        return getSelf();
    }

    /**
     * Drop select columns command sql.
     *
     * @param fields the fields
     * @return the command sql
     */
    public SimpleSQL dropSelectColumns(String... fields) {
        for (String field : fields) {
            this.sql().select.remove(field);
        }
        return getSelf();
    }

    /**
     * Table alias command sql.
     *
     * @param aliasName the alias name
     * @return the command sql
     */
    public SimpleSQL as(String aliasName, SqlStatementType sqlStatementType) {
        List<String> list = this.getStatementLatest(sqlStatementType);
        String last = list.remove(list.size() - 1);
        list.add(last + " " + aliasName);
        return getSelf();
    }

    /**
     * Join on command sql.
     *
     * @param on               the on
     * @param sqlStatementType the sql statement
     * @return the command sql
     */
    public SimpleSQL joinStepOn(String on, SqlStatementType sqlStatementType) {
        List<String> list = this.getStatementLatest(sqlStatementType);
        String last = list.remove(list.size() - 1);
        list.add(last + " on " + on);
        return getSelf();
    }

    public boolean isEmptySelectColumns() {
        return this.sql().select.isEmpty();
    }

    private List<String> getStatementLatest(SqlStatementType sqlStatementType) {
        List<String> list;
        if (SqlStatementType.JOIN == sqlStatementType) {
            list = this.sql().join;
        } else if (SqlStatementType.INNER_JOIN == sqlStatementType) {
            list = this.sql().innerJoin;
        } else if (SqlStatementType.OUTER_JOIN == sqlStatementType) {
            list = this.sql().outerJoin;
        } else if (SqlStatementType.LEFT_OUTER_JOIN == sqlStatementType) {
            list = this.sql().leftOuterJoin;
        } else if (SqlStatementType.RIGHT_OUTER_JOIN == sqlStatementType) {
            list = this.sql().rightOuterJoin;
        } else {
            list = this.sql().tables;
        }
        return list;
    }
}

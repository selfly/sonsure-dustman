/**
 * Copyright 2009-2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sonsure.dumper.core.command.sql;

import com.sonsure.dumper.core.command.lambda.Function;

import java.util.function.Supplier;

/**
 * @author liyd
 */
public class SqlExecutorImpl extends AbstractSQL<SqlExecutorImpl> {

    @Override
    public SqlExecutorImpl getSelf() {
        return this;
    }

   public <E, R> SqlExecutorImpl name1(Function<E, R> function){
        return this;
    }


    public <T> SqlExecutorImpl name2(Supplier<T> supplier) {
        return this;
    }


    public static void main(String[] args) {

//        SqlExecutorImpl sqlExecutor = new SqlExecutorImpl();
//        String sql = sqlExecutor.UPDATE("USER")
//                .SET("username = #{username}")
//                .WHERE("id = #{id}")
//                .toString();
//
//        String sql2 = sqlExecutor
//                .INSERT_INTO("PERSON")
//                .VALUES("ID, FIRST_NAME", "#{id}, #{firstName}")
//                .VALUES("LAST_NAME", "#{lastName}")
//                .toString();
//
//        System.out.println(sql);


    }
}

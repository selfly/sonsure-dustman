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
package com.sonsure.dumper.core.third.mybatis;


/**
 * 修改父类 sql()、SQLStatement 为 protected
 *
 * @author selfly
 */
public class CommandSql extends AbstractSQL<CommandSql> {


    @Override
    public CommandSql getSelf() {
        return this;
    }

    /**
     * Clear select columns command sql.
     *
     * @return the command sql
     */
    public CommandSql clearSelectColumns() {
        this.sql().select.clear();
        return getSelf();
    }

    /**
     * Drop select columns command sql.
     *
     * @param fields the fields
     * @return the command sql
     */
    public CommandSql dropSelectColumns(String... fields) {
        for (String field : fields) {
            this.sql().select.remove(field);
        }
        return this;
    }
}

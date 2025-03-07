package com.sonsure.dumper.core.command;

import com.sonsure.dumper.core.command.lambda.Function;
import com.sonsure.dumper.core.command.lambda.LambdaMethod;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author selfly
 */
@Getter
public class SqlPart {

    private static final String OR = " OR ";
    private static final String AND = " AND ";

    private final List<Condition> conditions = new ArrayList<>(8);

    public static SqlPart of(String name, Object value) {
        return of(name, SqlOperator.EQ, value);
    }

    public static <E, R> SqlPart of(Function<E, R> function, Object value) {
        String name = LambdaMethod.getField(function);
        return of(name, SqlOperator.EQ, value);
    }

    public static SqlPart of(String name, SqlOperator sqlOperator, Object value) {
        return new SqlPart().condition(null, name, sqlOperator, value);
    }

    public static <E, R> SqlPart of(Function<E, R> function, SqlOperator sqlOperator, Object value) {
        String name = LambdaMethod.getField(function);
        return new SqlPart().condition(null, name, sqlOperator, value);
    }

    public SqlPart or(String name, Object value) {
        return this.or(name, SqlOperator.EQ, value);
    }

    public <E, R> SqlPart or(Function<E, R> function, Object value) {
        String name = LambdaMethod.getField(function);
        return this.or(name, SqlOperator.EQ, value);
    }

    public <E, R> SqlPart or(Function<E, R> function, SqlOperator sqlOperator, Object value) {
        String name = LambdaMethod.getField(function);
        return this.or(name, sqlOperator, value);
    }

    public SqlPart or(String name, SqlOperator sqlOperator, Object value) {
        return this.condition(OR, name, sqlOperator, value);
    }

    public SqlPart and(String name, Object value) {
        return this.and(name, SqlOperator.EQ, value);
    }

    public <E, R> SqlPart and(Function<E, R> function, Object value) {
        String name = LambdaMethod.getField(function);
        return this.and(name, SqlOperator.EQ, value);
    }

    public <E, R> SqlPart and(Function<E, R> function, SqlOperator sqlOperator, Object value) {
        String name = LambdaMethod.getField(function);
        return this.and(name, sqlOperator, value);
    }

    public SqlPart and(String name, SqlOperator sqlOperator, Object value) {
        return this.condition(AND, name, sqlOperator, value);
    }


    private SqlPart condition(String logical, String name, SqlOperator sqlOperator, Object value) {
        this.conditions.add(Condition.of(logical, name, sqlOperator, value));
        return this;
    }

    @Getter
    public static class Condition {

        private final String logical;
        private final String name;
        private final SqlOperator sqlOperator;
        private final Object value;

        private Condition(String logical, String name, SqlOperator sqlOperator, Object value) {
            this.logical = logical;
            this.name = name;
            this.sqlOperator = sqlOperator;
            this.value = value;
        }

        public static Condition of(String logical, String name, SqlOperator sqlOperator, Object value) {
            return new Condition(logical, name, sqlOperator, value);
        }
    }
}

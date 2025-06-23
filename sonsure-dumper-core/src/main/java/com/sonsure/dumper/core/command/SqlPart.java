package com.sonsure.dumper.core.command;

import com.sonsure.dumper.core.command.lambda.Function;
import com.sonsure.dumper.core.command.lambda.LambdaHelper;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 属性分开处理，适配联表查询时使用，例如：
 * .on(SqlPart.of(UserInfo::getUserInfoId).eq(Account::getAccountId))
 *
 * @author selfly
 */
@Getter
public class SqlPart {

    private static final String OR = " OR ";
    private static final String AND = " AND ";
    private static final String FUZZY = "%";

    private final List<PartStatement> partStatements = new ArrayList<>(8);

    private SqlPart(PartStatement partStatement) {
        this.addPartStatement(partStatement);
    }

    public static SqlPart of(String name) {
        PartStatement partStatement = new PartStatement(name);
        return new SqlPart(partStatement);
    }

    public static <E, R> SqlPart of(Function<E, R> function) {
        PartStatement partStatement = new PartStatement(LambdaHelper.getLambdaClass(function));
        return new SqlPart(partStatement);
    }

    public SqlPart eq(Object value) {
        PartStatement partStatement = this.partStatements.get(this.partStatements.size() - 1);
        partStatement.setSqlOperator(SqlOperator.EQ);
        partStatement.setTarget(value);
        return this;
    }

    public <E, R> SqlPart eq(Function<E, R> function) {
        PartStatement partStatement = this.partStatements.get(this.partStatements.size() - 1);
        partStatement.setSqlOperator(SqlOperator.EQ);
        partStatement.setTarget(LambdaHelper.getLambdaClass(function));
        partStatement.setRaw(true);
        return this;
    }

    public SqlPart like(String value) {
        PartStatement partStatement = this.partStatements.get(this.partStatements.size() - 1);
        partStatement.setSqlOperator(SqlOperator.LIKE);
        partStatement.setTarget(value);
        return this;
    }

    public SqlPart likeFuzzy(String value) {
        return this.like(FUZZY + value + FUZZY);
    }

    public SqlPart likeLeftFuzzy(String value) {
        return this.like(FUZZY + value);
    }

    public SqlPart likeRightFuzzy(String value) {
        return this.like(value + FUZZY);
    }

    public SqlPart or(String name) {
        PartStatement partStatement = new PartStatement(name);
        partStatement.setLogical(OR);
        this.partStatements.add(partStatement);
        return this;
    }

    public <E, R> SqlPart or(Function<E, R> function) {
        PartStatement partStatement = new PartStatement(LambdaHelper.getLambdaClass(function));
        partStatement.setLogical(OR);
        this.partStatements.add(partStatement);
        return this;
    }

    public SqlPart and(String name) {
        PartStatement partStatement = new PartStatement(name);
        partStatement.setLogical(AND);
        this.partStatements.add(partStatement);
        return this;
    }

    public <E, R> SqlPart and(Function<E, R> function) {
        PartStatement partStatement = new PartStatement(LambdaHelper.getLambdaClass(function));
        partStatement.setLogical(AND);
        this.partStatements.add(partStatement);
        return this;
    }

    private void addPartStatement(PartStatement partStatement) {
        this.partStatements.add(partStatement);
    }
//
//    public static SqlPart of(String name, Object value) {
//        return of(name, SqlOperator.EQ, value);
//    }
//
//    public static <E, R> SqlPart of(Function<E, R> function, Object value) {
//        return of(function, SqlOperator.EQ, value);
//    }
//
//    public static SqlPart of(String name, SqlOperator sqlOperator, Object value) {
//        return new SqlPart().condition(null, name, sqlOperator, value);
//    }
//
//    public static <E, R> SqlPart of(Function<E, R> function, SqlOperator sqlOperator, Object value) {
//        LambdaClass lambdaClass = LambdaHelper.getLambdaClass(function);
//        return new SqlPart().condition(null, lambdaClass, sqlOperator, value);
//    }
//
//    public SqlPart or(String name, Object value) {
//        return this.or(name, SqlOperator.EQ, value);
//    }
//
//    public <E, R> SqlPart or(Function<E, R> function, Object value) {
//        return this.or(function, SqlOperator.EQ, value);
//    }
//
//    public <E, R> SqlPart or(Function<E, R> function, SqlOperator sqlOperator, Object value) {
//        LambdaClass lambdaClass = LambdaHelper.getLambdaClass(function);
//        return this.condition(OR, lambdaClass, sqlOperator, value);
//    }
//
//    public SqlPart or(String name, SqlOperator sqlOperator, Object value) {
//        return this.condition(OR, name, sqlOperator, value);
//    }
//
//    public SqlPart and(String name, Object value) {
//        return this.and(name, SqlOperator.EQ, value);
//    }
//
//    public <E, R> SqlPart and(Function<E, R> function, Object value) {
//        return this.and(function, SqlOperator.EQ, value);
//    }
//
//    public <E, R> SqlPart and(Function<E, R> function, SqlOperator sqlOperator, Object value) {
//        LambdaClass lambdaClass = LambdaHelper.getLambdaClass(function);
//        return this.condition(AND, lambdaClass, sqlOperator, value);
//    }
//
//    public SqlPart and(String name, SqlOperator sqlOperator, Object value) {
//        return this.condition(AND, name, sqlOperator, value);
//    }
//
//
//    private SqlPart condition(String logical, String name, SqlOperator sqlOperator, Object value) {
//        this.conditions.add(Condition.of(logical, name, sqlOperator, value));
//        return this;
//    }
//
//    private SqlPart condition(String logical, LambdaClass lambdaClass, SqlOperator sqlOperator, Object value) {
//        this.conditions.add(Condition.of(logical, lambdaClass, sqlOperator, value));
//        return this;
//    }

    @Getter
    @Setter
    public static class PartStatement {
        private String logical;
        private Object source;
        private Object target;
        private SqlOperator sqlOperator;
        private boolean isRaw = false;

        public PartStatement(Object source) {
            this.source = source;
        }

    }
}

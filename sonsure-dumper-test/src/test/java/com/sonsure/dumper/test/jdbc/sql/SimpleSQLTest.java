package com.sonsure.dumper.test.jdbc.sql;

import com.sonsure.dumper.common.utils.StrUtils;
import com.sonsure.dumper.core.command.build.SimpleSQL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SimpleSQLTest {

    @Test
    public void select() {
        String expect = "SELECT * FROM user_info";
        SimpleSQL sql = new SimpleSQL();
        sql.select("*").from("user_info");
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect, minify);
    }

    @Test
    public void selectColumnsAndWhere() {
        String expect = "select userInfoId, loginName, password, userAge,userType,status,gender,realName,email,mobile,avatar,description FROM user_info where loginName = 'test-user-123' and password = '123456'";
        SimpleSQL sql = new SimpleSQL();
        sql.select("userInfoId, loginName, password")
                .select("userAge,userType,status,gender,realName,email,mobile,avatar,description")
                .from("user_info")
                .where("loginName = 'test-user-123'")
                .and("password = '123456'");
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect.toLowerCase(), minify.toLowerCase());
    }

    @Test
    public void selectColumnsAndWhereParen() {
        String expect = "select userInfoId, loginName, password, userAge,userType,status,gender,realName,email,mobile,avatar,description FROM user_info where (loginName = 'test-user-123' or email = 'test-user-123@sonsure.com') and password = '123456'";
        SimpleSQL sql = new SimpleSQL();
        sql.select("userInfoId, loginName, password")
                .select("userAge,userType,status,gender,realName,email,mobile,avatar,description")
                .from("user_info")
                .where()
                .openParen()
                .where("loginName = 'test-user-123'")
                .or("email = 'test-user-123@sonsure.com'")
                .closeParen()
                .and("password = '123456'");
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect.toLowerCase(), minify.toLowerCase());
    }

    @Test
    public void selectDynamicWhere() {
        String expect = "select userInfoId, loginName, password, userAge,userType,status,gender,realName,email,mobile,avatar,description FROM user_info";
        SimpleSQL sql = new SimpleSQL();
        sql.select("userInfoId, loginName, password")
                .select("userAge,userType,status,gender,realName,email,mobile,avatar,description")
                .from("user_info")
                .where();
//                .openParen()
//                .where("loginName = 'test-user-123'")
//                .or("email = 'test-user-123@sonsure.com'")
//                .closeParen()
//                .and("password = '123456'");
        //动态条件为空处理where情况
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect.toLowerCase(), minify.toLowerCase());

        //动态条件不为空，处理 where and 情况
        sql.and("loginName = 'test-user-123'");

        String expect2 = expect + " where loginName = 'test-user-123'";
        String minify2 = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect2.toLowerCase(), minify2.toLowerCase());
    }

    @Test
    public void update() {
        String expect = "UPDATE user_info SET userAge = userAge + 1 WHERE userInfoId = 10";
        SimpleSQL sql = new SimpleSQL();
        sql.update("user_info")
                .set("userAge = userAge + 1")
                .where("userInfoId = 10");
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect, minify);
    }

    @Test
    public void delete() {
        String expect = "DELETE FROM user_info WHERE status = 'resigned'";
        SimpleSQL sql = new SimpleSQL();
        sql.deleteFrom("user_info")
                .where("status = 'resigned'");
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect, minify);
    }

    @Test
    public void insert() {
        String expect = "INSERT INTO user_info (loginName, password, userAge,userType) VALUES ('test-user-123', '123456', 13, '1')";
        SimpleSQL sql = new SimpleSQL();
        sql.insertInto("user_info")
                .values("loginName, password, userAge,userType", "'test-user-123', '123456', 13, '1'");
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect, minify);
    }

    @Test
    public void selectDistinct() {
        String expect = "SELECT DISTINCT loginName FROM user_info";
        SimpleSQL sql = new SimpleSQL();
        sql.selectDistinct("loginName")
                .from("user_info");
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect, minify);
    }

    @Test
    public void innerJoin() {
        String expect = "SELECT t1.loginName, t2.accountName FROM user_info t1 INNER JOIN account t2 ON t1.userInfoId = t2.accountId";
        SimpleSQL sql = new SimpleSQL();
        sql.select("t1.loginName, t2.accountName")
                .from("user_info t1")
                .innerJoin("account t2 ON t1.userInfoId = t2.accountId");
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect, minify);
    }

    @Test
    public void leftJoin() {
        String expect = "SELECT t1.loginName, t2.accountName FROM user_info t1 LEFT OUTER JOIN account t2 ON t1.userInfoId = t2.accountId";
        SimpleSQL sql = new SimpleSQL();
        sql.select("t1.loginName, t2.accountName")
                .from("user_info t1")
                .leftOuterJoin("account t2 ON t1.userInfoId = t2.accountId");
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect, minify);
    }


    @Test
    public void rightJoin() {
        String expect = "SELECT t1.loginName, t2.accountName FROM user_info t1 RIGHT OUTER JOIN account t2 ON t1.userInfoId = t2.accountId";
        SimpleSQL sql = new SimpleSQL();
        sql.select("t1.loginName, t2.accountName")
                .from("user_info t1")
                .rightOuterJoin("account t2 ON t1.userInfoId = t2.accountId");
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect, minify);
    }

    @Test
    public void outerJoin() {
        String expect = "SELECT t1.loginName, t2.accountName FROM user_info t1 OUTER JOIN account t2 ON t1.userInfoId = t2.accountId";
        SimpleSQL sql = new SimpleSQL();
        sql.select("t1.loginName, t2.accountName")
                .from("user_info t1")
                .outerJoin("account t2 ON t1.userInfoId = t2.accountId");
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect, minify);
    }

    @Test
    public void selectGroupBy() {

        String expect = "SELECT gender, COUNT(*) AS user_count FROM user_info WHERE userAge > 15 AND (status = '1' OR status = '2') GROUP BY gender";
        SimpleSQL sql = new SimpleSQL();
        sql.select("gender, COUNT(*) AS user_count")
                .from("user_info")
                .where("userAge > 15")
                .and()
                .openParen()
                .condition("status = '1'").or("status = '2'")
                .closeParen()
                .groupBy("gender");
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect, minify);
    }

    @Test
    public void selectOrderBy() {
        String expect = "SELECT loginName, gender, userAge FROM user_info ORDER BY gender DESC, userAge ASC";
        SimpleSQL sql = new SimpleSQL();
        sql.select("loginName, gender, userAge")
                .from("user_info")
                .orderBy("gender DESC")
                .orderBy("userAge ASC");
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect, minify);
    }

    @Test
    public void selectHaving() {
        String expect = "SELECT gender, COUNT(*) AS user_count FROM user_info GROUP BY gender HAVING COUNT(*) > 5";
        SimpleSQL sql = new SimpleSQL();
        sql.select("gender, COUNT(*) AS user_count")
                .from("user_info")
                .groupBy("gender")
                .having("COUNT(*) > 5");
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect, minify);
    }

    @Test
    public void select2() {
        String expect = "SELECT t1.department_id, t1.name, t1.salary, t2.avg_salary FROM user_info e INNER JOIN ( SELECT department_id, AVG(salary) AS avg_salary FROM user_info GROUP BY department_id HAVING AVG(salary) > 6000 ) d ON t1.department_id = t2.department_id WHERE t1.salary > t2.avg_salary ORDER BY t1.salary DESC";
        SimpleSQL sql = new SimpleSQL();
        sql.select("t1.department_id, t1.name, t1.salary, t2.avg_salary")
                .from("user_info e")
                .innerJoin("( SELECT department_id, AVG(salary) AS avg_salary FROM user_info GROUP BY department_id HAVING AVG(salary) > 6000 ) d ON t1.department_id = t2.department_id")
                .where("t1.salary > t2.avg_salary")
                .orderBy("t1.salary DESC");
        String minify = StrUtils.minify(sql.toString());
        System.out.println(expect);
        System.out.println(minify);
        Assertions.assertEquals(expect, minify);
    }

}

package com.sonsure.dumper.test.sql;

import com.sonsure.dumper.common.utils.StrUtils;
import com.sonsure.dumper.core.third.mybatis.SimpleSQL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SimpleSQLTest {

    @Test
    public void select() {
        //SELECT * FROM employees;
        String expect = "SELECT * FROM employees";
        SimpleSQL sql = new SimpleSQL();
        sql.select("*").from("employees");
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect, minify);
    }

    @Test
    public void update() {
        String expect = "UPDATE employees SET salary = salary * 1.1 WHERE department_id = 10";
        SimpleSQL sql = new SimpleSQL();
        sql.update("employees")
                .set("salary = salary * 1.1")
                .where("department_id = 10");
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect, minify);
    }

    @Test
    public void delete() {
        String expect = "DELETE FROM employees WHERE status = 'resigned'";
        SimpleSQL sql = new SimpleSQL();
        sql.deleteFrom("employees")
                .where("status = 'resigned'");
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect, minify);
    }

    @Test
    public void insert() {
        String expect = "INSERT INTO employees (id, name, department_id, salary) VALUES (101, 'Alice', 3, 75000)";
        SimpleSQL sql = new SimpleSQL();
        sql.insertInto("employees")
                .values("id, name, department_id, salary", "101, 'Alice', 3, 75000");
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect, minify);
    }

    @Test
    public void selectDistinct() {
        String expect = "SELECT DISTINCT department_name FROM departments";
        SimpleSQL sql = new SimpleSQL();
        sql.selectDistinct("department_name")
                .from("departments");
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect, minify);
    }

    @Test
    public void innerJoin() {
        String expect = "SELECT e.name, d.department_name FROM employees e INNER JOIN departments d ON e.department_id = d.id";
        SimpleSQL sql = new SimpleSQL();
        sql.select("e.name, d.department_name")
                .from("employees e")
                .innerJoin("departments d ON e.department_id = d.id");
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect, minify);
    }

    @Test
    public void leftJoin() {
        String expect = "SELECT e.name, d.department_name FROM employees e LEFT OUTER JOIN departments d ON e.department_id = d.id";
        SimpleSQL sql = new SimpleSQL();
        sql.select("e.name, d.department_name")
                .from("employees e")
                .leftOuterJoin("departments d ON e.department_id = d.id");
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect, minify);
    }

    @Test
    public void rightJoin() {
        String expect = "SELECT e.name, d.department_name FROM employees e RIGHT OUTER JOIN departments d ON e.department_id = d.id";
        SimpleSQL sql = new SimpleSQL();
        sql.select("e.name, d.department_name")
                .from("employees e")
                .rightOuterJoin("departments d ON e.department_id = d.id");
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect, minify);
    }

    @Test
    public void outerJoin() {
        String expect = "SELECT e.name, d.department_name FROM employees e OUTER JOIN departments d ON e.department_id = d.id";
        SimpleSQL sql = new SimpleSQL();
        sql.select("e.name, d.department_name")
                .from("employees e")
                .outerJoin("departments d ON e.department_id = d.id");
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect, minify);
    }

    @Test
    public void selectOrderByGroupBy() {
        String expect = "SELECT department, COUNT(*) AS employee_count FROM employees WHERE salary > 5000 AND (department = 'IT' OR department = 'HR') GROUP BY department";
        SimpleSQL sql = new SimpleSQL();
        sql.select("department, COUNT(*) AS employee_count")
                .from("employees")
                .where("salary > 5000")
                .and()
                .openParen()
                .where("department = 'IT'").or().where("department = 'HR'")
                .closeParen()
                .groupBy("department");
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect, minify);
    }

    @Test
    public void selectOrderBy() {
        String expect = "SELECT name, salary, hire_date FROM employees ORDER BY salary DESC, hire_date ASC";
        SimpleSQL sql = new SimpleSQL();
        sql.select("name, salary, hire_date")
                .from("employees")
                .orderBy("salary DESC")
                .orderBy("hire_date ASC");
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect, minify);
    }
    @Test
    public void selectGroupBy() {
        String expect = "SELECT department, AVG(salary) AS avg_salary FROM employees GROUP BY department";
        SimpleSQL sql = new SimpleSQL();
        sql.select("department, AVG(salary) AS avg_salary")
                .from("employees")
                .groupBy("department");
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect, minify);
    }

    @Test
    public void selectHaving() {
        String expect = "SELECT department, COUNT(*) AS employee_count FROM employees GROUP BY department HAVING COUNT(*) > 5";
        SimpleSQL sql = new SimpleSQL();
        sql.select("department, COUNT(*) AS employee_count")
                .from("employees")
                .groupBy("department")
                .having("COUNT(*) > 5");
        String minify = StrUtils.minify(sql.toString());
        Assertions.assertEquals(expect, minify);
    }

    @Test
    public void select2() {
        String expect = "SELECT e.department_id, e.name, e.salary, d.avg_salary FROM employees e INNER JOIN ( SELECT department_id, AVG(salary) AS avg_salary FROM employees GROUP BY department_id HAVING AVG(salary) > 6000 ) d ON e.department_id = d.department_id WHERE e.salary > d.avg_salary ORDER BY e.salary DESC";
        SimpleSQL sql = new SimpleSQL();
        sql.select("e.department_id, e.name, e.salary, d.avg_salary")
                .from("employees e")
                .innerJoin("( SELECT department_id, AVG(salary) AS avg_salary FROM employees GROUP BY department_id HAVING AVG(salary) > 6000 ) d ON e.department_id = d.department_id")
                .where("e.salary > d.avg_salary")
                .orderBy("e.salary DESC");
        String minify = StrUtils.minify(sql.toString());
        System.out.println(expect);
        System.out.println(minify);
        Assertions.assertEquals(expect, minify);
    }

}

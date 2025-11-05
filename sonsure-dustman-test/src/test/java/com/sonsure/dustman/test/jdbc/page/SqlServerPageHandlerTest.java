/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.test.jdbc.page;

import com.sonsure.dustman.common.model.Pagination;
import com.sonsure.dustman.jdbc.config.DatabaseDialect;
import com.sonsure.dustman.jdbc.exception.SonsureJdbcException;
import com.sonsure.dustman.jdbc.page.SqlServerPageHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * SqlServerPageHandler 单元测试
 */
public class SqlServerPageHandlerTest {

    private SqlServerPageHandler pageHandler;

    @BeforeEach
    public void setUp() {
        pageHandler = new SqlServerPageHandler();
    }

    // ========== support() 方法测试 ==========

    @Test
    public void testSupport_SqlServer() {
        Assertions.assertTrue(pageHandler.support("sql server"));
        Assertions.assertTrue(pageHandler.support("SQL Server"));
        Assertions.assertTrue(pageHandler.support("Microsoft SQL Server"));
    }

    @Test
    public void testSupport_NotSqlServer() {
        Assertions.assertFalse(pageHandler.support("mysql"));
        Assertions.assertFalse(pageHandler.support("oracle"));
        Assertions.assertFalse(pageHandler.support("postgresql"));
        Assertions.assertFalse(pageHandler.support("h2"));
        Assertions.assertFalse(pageHandler.support(""));
    }

    // ========== getPageCommand() 方法测试 - 基础 SELECT 语句 ==========

    @Test
    public void testGetPageCommand_SimpleSelect() {
        String sql = "SELECT id, name FROM users";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("ROW_NUMBER()"));
        Assertions.assertTrue(result.contains("PAGE_TABLE_ALIAS"));
        Assertions.assertTrue(result.contains(String.valueOf(pagination.getBeginIndex())));
        Assertions.assertTrue(result.contains(String.valueOf(pagination.getPageSize())));
    }

    @Test
    public void testGetPageCommand_SimpleSelectWithOrderBy() {
        String sql = "SELECT id, name FROM users ORDER BY id ASC";
        Pagination pagination = new Pagination();
        pagination.setPageNum(2);
        pagination.setPageSize(5);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("ROW_NUMBER()"));
    }

    @Test
    public void testGetPageCommand_SelectWithAlias() {
        String sql = "SELECT id AS userId, name AS userName FROM users ORDER BY id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(20);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("userId"));
        Assertions.assertTrue(result.contains("userName"));
    }

    @Test
    public void testGetPageCommand_SelectAll() {
        String sql = "SELECT * FROM users ORDER BY id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("*"));
    }

    @Test
    public void testGetPageCommand_SelectAllTableColumns() {
        String sql = "SELECT u.*, u.name FROM users u ORDER BY u.id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
    }

    @Test
    public void testGetPageCommand_SelectWithTableAlias() {
        String sql = "SELECT u.id, u.name FROM users u ORDER BY u.id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
    }

    @Test
    public void testGetPageCommand_SelectWithFunction() {
        String sql = "SELECT COUNT(*) AS cnt, name FROM users GROUP BY name ORDER BY cnt DESC";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("cnt"));
    }

    @Test
    public void testGetPageCommand_SelectWithoutOrderBy() {
        // 没有 ORDER BY 的情况，会自动添加 ORDER BY RAND()
        String sql = "SELECT id, name FROM users";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("ROW_NUMBER()"));
    }

    // ========== JOIN 查询测试 ==========

    @Test
    public void testGetPageCommand_InnerJoin() {
        String sql = "SELECT u.id, u.name, d.department_name " +
                "FROM users u INNER JOIN departments d ON u.dept_id = d.id " +
                "ORDER BY u.id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
    }

    @Test
    public void testGetPageCommand_LeftJoin() {
        String sql = "SELECT u.id, u.name, d.department_name " +
                "FROM users u LEFT JOIN departments d ON u.dept_id = d.id " +
                "ORDER BY u.id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
    }

    @Test
    public void testGetPageCommand_RightJoin() {
        String sql = "SELECT u.id, u.name, d.department_name " +
                "FROM users u RIGHT JOIN departments d ON u.dept_id = d.id " +
                "ORDER BY u.id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
    }

    @Test
    public void testGetPageCommand_MultipleJoins() {
        String sql = "SELECT u.id, u.name, d.department_name, r.role_name " +
                "FROM users u " +
                "INNER JOIN departments d ON u.dept_id = d.id " +
                "LEFT JOIN roles r ON u.role_id = r.id " +
                "ORDER BY u.id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
    }

    // ========== 子查询测试 ==========

    @Test
    public void testGetPageCommand_SubqueryInFrom() {
        String sql = "SELECT * FROM (SELECT id, name FROM users WHERE age > 18) AS sub " +
                "ORDER BY id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
    }

    @Test
    public void testGetPageCommand_SubqueryInSelect() {
        String sql = "SELECT u.id, u.name, " +
                "(SELECT COUNT(*) FROM orders o WHERE o.user_id = u.id) AS order_count " +
                "FROM users u ORDER BY u.id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
    }

    @Test
    public void testGetPageCommand_NestedSubquery() {
        String sql = "SELECT * FROM (" +
                "SELECT u.id, u.name FROM (" +
                "SELECT id, name FROM users WHERE status = 1" +
                ") u WHERE u.age > 18" +
                ") final ORDER BY id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
    }

    // ========== UNION 查询测试 ==========

    @Test
    public void testGetPageCommand_Union() {
        String sql = "SELECT id, name FROM users WHERE age > 18 " +
                "UNION " +
                "SELECT id, name FROM users WHERE age < 65 " +
                "ORDER BY id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
    }

    @Test
    public void testGetPageCommand_UnionAll() {
        String sql = "SELECT id, name FROM users WHERE dept_id = 1 " +
                "UNION ALL " +
                "SELECT id, name FROM users WHERE dept_id = 2 " +
                "ORDER BY id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
    }

    // ========== WHERE 条件测试 ==========

    @Test
    public void testGetPageCommand_WithWhereClause() {
        String sql = "SELECT id, name FROM users WHERE age > 18 AND status = 1 ORDER BY id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("WHERE"));
    }

    @Test
    public void testGetPageCommand_WithComplexWhere() {
        String sql = "SELECT id, name FROM users " +
                "WHERE (age > 18 OR age < 65) AND status = 1 " +
                "ORDER BY id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
    }

    // ========== GROUP BY / HAVING 测试 ==========

    @Test
    public void testGetPageCommand_WithGroupBy() {
        String sql = "SELECT dept_id, COUNT(*) AS cnt FROM users " +
                "GROUP BY dept_id " +
                "ORDER BY cnt DESC";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("cnt"));
    }

    @Test
    public void testGetPageCommand_WithGroupByAndHaving() {
        String sql = "SELECT dept_id, COUNT(*) AS cnt FROM users " +
                "GROUP BY dept_id " +
                "HAVING COUNT(*) > 10 " +
                "ORDER BY cnt DESC";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
    }

    // ========== ORDER BY 复杂场景测试 ==========

    @Test
    public void testGetPageCommand_MultipleOrderBy() {
        String sql = "SELECT id, name, age FROM users ORDER BY age DESC, name ASC, id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
    }

    @Test
    public void testGetPageCommand_OrderByExpression() {
        String sql = "SELECT id, name FROM users ORDER BY CASE WHEN age > 18 THEN 1 ELSE 2 END, id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
    }

    @Test
    public void testGetPageCommand_OrderByFunction() {
        String sql = "SELECT id, name FROM users ORDER BY UPPER(name), id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
    }

    @Test
    public void testGetPageCommand_OrderByNotInSelectList() {
        // ORDER BY 的列不在 SELECT 列表中，会自动添加到 SELECT
        String sql = "SELECT id, name FROM users ORDER BY age";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("ROW_ALIAS_"));
    }

    // ========== 分页参数测试 ==========

    @Test
    public void testGetPageCommand_FirstPage() {
        String sql = "SELECT id, name FROM users ORDER BY id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("0")); // beginIndex = 0
        Assertions.assertTrue(result.contains("10")); // pageSize = 10
    }

    @Test
    public void testGetPageCommand_SecondPage() {
        String sql = "SELECT id, name FROM users ORDER BY id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(2);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("10")); // beginIndex = 10
        Assertions.assertTrue(result.contains("10")); // pageSize = 10
    }

    @Test
    public void testGetPageCommand_LargePageSize() {
        String sql = "SELECT id, name FROM users ORDER BY id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(1000);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("1000"));
    }

    @Test
    public void testGetPageCommand_ZeroPageNum() {
        String sql = "SELECT id, name FROM users ORDER BY id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(0);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("0")); // beginIndex should be 0
    }

    // ========== 错误场景测试 ==========

    @Test
    public void testGetPageCommand_WithTopClause() {
        // SQL 中已包含 TOP，应该抛出异常
        String sql = "SELECT TOP 10 id, name FROM users ORDER BY id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        Assertions.assertThrows(SonsureJdbcException.class, () -> {
            pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        });
    }

    @Test
    public void testGetPageCommand_InvalidSql() {
        String sql = "INVALID SQL STATEMENT";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        Assertions.assertThrows(SonsureJdbcException.class, () -> {
            pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        });
    }

    @Test
    public void testGetPageCommand_NonSelectStatement() {
        String sql = "INSERT INTO users (id, name) VALUES (1, 'test')";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        Assertions.assertThrows(SonsureJdbcException.class, () -> {
            pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        });
    }

    @Test
    public void testGetPageCommand_UpdateStatement() {
        String sql = "UPDATE users SET name = 'test' WHERE id = 1";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        Assertions.assertThrows(SonsureJdbcException.class, () -> {
            pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        });
    }

    @Test
    public void testGetPageCommand_DeleteStatement() {
        String sql = "DELETE FROM users WHERE id = 1";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        Assertions.assertThrows(SonsureJdbcException.class, () -> {
            pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        });
    }

    @Test
    public void testGetPageCommand_OrderByAliasWithoutAliasInSelect() {
        // ORDER BY 使用别名，但该列在 SELECT 中没有别名定义
        String sql = "SELECT id, UPPER(name) FROM users ORDER BY UPPER(name)";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        // 这种情况应该会抛出异常，因为复杂表达式需要别名
        Assertions.assertThrows(SonsureJdbcException.class, () -> {
            pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        });
    }

    // ========== 特殊 SQL 场景测试 ==========

    @Test
    public void testGetPageCommand_SelectWithDistinct() {
        String sql = "SELECT DISTINCT id, name FROM users ORDER BY id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
    }

    @Test
    public void testGetPageCommand_SelectWithCaseWhen() {
        String sql = "SELECT id, name, " +
                "CASE WHEN age > 18 THEN 'Adult' ELSE 'Minor' END AS age_group " +
                "FROM users ORDER BY id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("age_group"));
    }

    @Test
    public void testGetPageCommand_SelectWithConstants() {
        String sql = "SELECT id, name, 1 AS constant_value FROM users ORDER BY id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("constant_value"));
    }

    @Test
    public void testGetPageCommand_SelectWithNullCoalesce() {
        String sql = "SELECT id, COALESCE(name, 'Unknown') AS display_name FROM users ORDER BY id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("display_name"));
    }

    @Test
    public void testGetPageCommand_SelectWithAggregateFunctions() {
        String sql = "SELECT dept_id, " +
                "SUM(salary) AS total_salary, " +
                "AVG(salary) AS avg_salary, " +
                "MAX(age) AS max_age " +
                "FROM users " +
                "GROUP BY dept_id " +
                "ORDER BY total_salary DESC";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("total_salary"));
        Assertions.assertTrue(result.contains("avg_salary"));
        Assertions.assertTrue(result.contains("max_age"));
    }

    // ========== WITH 子句测试 ==========

    @Test
    public void testGetPageCommand_WithCTE() {
        String sql = "WITH ranked_users AS (" +
                "SELECT id, name, ROW_NUMBER() OVER (PARTITION BY dept_id ORDER BY id) AS rn " +
                "FROM users" +
                ") " +
                "SELECT id, name FROM ranked_users WHERE rn = 1 ORDER BY id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
    }

    // ========== 边界值测试 ==========

    @Test
    public void testGetPageCommand_EmptySelectList() {
        // 这种情况在实际中很少见，但测试边界情况
        String sql = "SELECT 1 FROM users ORDER BY (SELECT 1)";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
    }

    @Test
    public void testGetPageCommand_SelectAllWithOrderByTableAlias() {
        String sql = "SELECT u.*, d.* FROM users u " +
                "LEFT JOIN departments d ON u.dept_id = d.id " +
                "ORDER BY u.id, d.id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("*"));
    }

    // ========== Pagination 边界测试 ==========

    @Test
    public void testGetPageCommand_PageNumOne() {
        String sql = "SELECT id, name FROM users ORDER BY id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(20);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("0")); // beginIndex = 0
        Assertions.assertTrue(result.contains("20")); // pageSize = 20
    }

    @Test
    public void testGetPageCommand_LargePageNum() {
        String sql = "SELECT id, name FROM users ORDER BY id";
        Pagination pagination = new Pagination();
        pagination.setPageSize(10);
        pagination.setPageNum(100);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("990")); // beginIndex = 990
        Assertions.assertTrue(result.contains("10")); // pageSize = 10
    }

    @Test
    public void testGetPageCommand_PageSizeOne() {
        String sql = "SELECT id, name FROM users ORDER BY id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(1);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("1")); // pageSize = 1
    }

    // ========== 复杂 ORDER BY 场景 ==========

    @Test
    public void testGetPageCommand_OrderByColumnFromJoin() {
        String sql = "SELECT u.id, u.name " +
                "FROM users u " +
                "INNER JOIN departments d ON u.dept_id = d.id " +
                "ORDER BY d.department_name, u.id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
    }

    @Test
    public void testGetPageCommand_OrderByAliasThatExistsInSelect() {
        String sql = "SELECT id, name AS display_name FROM users ORDER BY display_name";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("display_name"));
    }

    // ========== 测试 SQLServerParser 的 isNotEmptyList 方法覆盖 ==========

    @Test
    public void testGetPageCommand_OrderByNullHandling() {
        // 测试 NULL 排序处理（如果 SQLServerParser 支持）
        String sql = "SELECT id, name FROM users ORDER BY name NULLS FIRST, id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
    }

    // ========== 多表查询复杂场景 ==========

    @Test
    public void testGetPageCommand_ComplexMultiTableQuery() {
        String sql = "SELECT u.id, u.name, d.department_name, r.role_name, " +
                "(SELECT COUNT(*) FROM orders o WHERE o.user_id = u.id) AS order_count " +
                "FROM users u " +
                "LEFT JOIN departments d ON u.dept_id = d.id " +
                "LEFT JOIN roles r ON u.role_id = r.id " +
                "WHERE u.status = 1 " +
                "ORDER BY d.department_name, u.name, u.id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
    }

    // ========== 测试不同方言代码 ==========

    @Test
    public void testGetPageCommand_WithDifferentDialectCodes() {
        String sql = "SELECT id, name FROM users ORDER BY id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        // 测试不同的 SQL Server 方言代码
        String result1 = pageHandler.getPageCommand(sql, pagination, "sql server");
        String result2 = pageHandler.getPageCommand(sql, pagination, "SQL Server");
        String result3 = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());

        Assertions.assertNotNull(result1);
        Assertions.assertNotNull(result2);
        Assertions.assertNotNull(result3);
        // 结果应该相同，因为都是 SQL Server
        Assertions.assertEquals(result1.replaceAll("\\s+", " "), result2.replaceAll("\\s+", " "));
    }

    // ========== 测试 Pagination 的各种设置 ==========

    @Test
    public void testGetPageCommand_WithPaginationOffset() {
        String sql = "SELECT id, name FROM users ORDER BY id";
        Pagination pagination = new Pagination();
        pagination.setPageSize(10);
        pagination.setOffset(25); // 设置偏移量，会自动计算页码

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
        // offset 25, pageSize 10 -> pageNum 应该是 3, beginIndex 应该是 20
        int expectedBeginIndex = pagination.getBeginIndex();
        Assertions.assertTrue(result.contains(String.valueOf(expectedBeginIndex)));
    }

    @Test
    public void testGetPageCommand_PaginationWithTotalItems() {
        String sql = "SELECT id, name FROM users ORDER BY id";
        Pagination pagination = new Pagination();
        pagination.setPageSize(10);
        pagination.setTotalItems(100);
        pagination.setPageNum(10); // 最后一页

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQL_SERVER.getCode());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains(String.valueOf(pagination.getBeginIndex())));
        Assertions.assertTrue(result.contains(String.valueOf(pagination.getPageSize())));
    }
}

package com.sonsure.dustman.test.jdbc.page;

import com.sonsure.dustman.common.model.Pagination;
import com.sonsure.dustman.jdbc.config.DatabaseDialect;
import com.sonsure.dustman.jdbc.page.PostgresqlPageHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PostgresqlPageHandler 单元测试
 */
public class PostgresqlPageHandlerTest {

    private PostgresqlPageHandler pageHandler;

    @BeforeEach
    public void setUp() {
        pageHandler = new PostgresqlPageHandler();
    }

    @Test
    public void testSupport_Postgresql() {
        assertTrue(pageHandler.support(DatabaseDialect.POSTGRESQL.getCode()));
        assertTrue(pageHandler.support("postgresql"));
        assertTrue(pageHandler.support("PostgreSQL"));
    }

    @Test
    public void testSupport_NotPostgresql() {
        assertFalse(pageHandler.support("mysql"));
        assertFalse(pageHandler.support("oracle"));
        assertFalse(pageHandler.support("h2"));
        assertFalse(pageHandler.support(""));
    }

    @Test
    public void testGetPageCommand_FirstPage() {
        String sql = "SELECT id, name FROM users ORDER BY id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.POSTGRESQL.getCode());
        assertTrue(result.contains("SELECT id, name FROM users ORDER BY id"));
        assertTrue(result.contains("limit 10"));
        assertTrue(result.contains("offset 0"));
    }

    @Test
    public void testGetPageCommand_SecondPage() {
        String sql = "SELECT id, name FROM users";
        Pagination pagination = new Pagination();
        pagination.setPageNum(2);
        pagination.setPageSize(20);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.POSTGRESQL.getCode());
        assertTrue(result.contains("limit 20"));
        assertTrue(result.contains("offset " + pagination.getBeginIndex()));
    }

    @Test
    public void testGetPageCommand_WithComplexSql() {
        String sql = "SELECT u.id, u.name FROM users u WHERE u.age > 18 ORDER BY u.id DESC";
        Pagination pagination = new Pagination();
        pagination.setPageSize(50);
        pagination.setPageNum(3);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.POSTGRESQL.getCode());
        assertTrue(result.contains("ORDER BY u.id DESC limit 50 offset 100"));
    }
}

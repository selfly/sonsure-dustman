package com.sonsure.dustman.test.jdbc.page;

import com.sonsure.dustman.common.model.Pagination;
import com.sonsure.dustman.jdbc.config.DatabaseDialect;
import com.sonsure.dustman.jdbc.page.SqlitePageHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SqlitePageHandler 单元测试
 */
public class SqlitePageHandlerTest {

    private SqlitePageHandler pageHandler;

    @BeforeEach
    public void setUp() {
        pageHandler = new SqlitePageHandler();
    }

    @Test
    public void testSupport_Sqlite() {
        assertTrue(pageHandler.support(DatabaseDialect.SQLITE.getCode()));
        assertTrue(pageHandler.support("sqlite"));
        assertTrue(pageHandler.support("SQLite"));
    }

    @Test
    public void testSupport_NotSqlite() {
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

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQLITE.getCode());
        assertTrue(result.contains("SELECT id, name FROM users ORDER BY id"));
        assertTrue(result.contains("limit 10"));
        assertTrue(result.contains("offset 0"));
    }

    @Test
    public void testGetPageCommand_SecondPage() {
        String sql = "SELECT id, name FROM users";
        Pagination pagination = new Pagination();
        pagination.setPageNum(2);
        pagination.setPageSize(15);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.SQLITE.getCode());
        assertTrue(result.contains("limit 15"));
        assertTrue(result.contains("offset " + pagination.getBeginIndex()));
    }
}

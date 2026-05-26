package com.sonsure.dustman.test.jdbc.page;

import com.sonsure.dustman.common.model.Pagination;
import com.sonsure.dustman.jdbc.config.DatabaseDialect;
import com.sonsure.dustman.jdbc.page.OraclePageHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OraclePageHandler 单元测试
 */
public class OraclePageHandlerTest {

    private OraclePageHandler pageHandler;

    @BeforeEach
    public void setUp() {
        pageHandler = new OraclePageHandler();
    }

    @Test
    public void testSupport_Oracle() {
        assertTrue(pageHandler.support(DatabaseDialect.ORACLE.getCode()));
        assertTrue(pageHandler.support("oracle"));
        assertTrue(pageHandler.support("Oracle"));
    }

    @Test
    public void testSupport_NotOracle() {
        assertFalse(pageHandler.support("mysql"));
        assertFalse(pageHandler.support("postgresql"));
        assertFalse(pageHandler.support("h2"));
        assertFalse(pageHandler.support(""));
    }

    @Test
    public void testGetPageCommand_FirstPage() {
        String sql = "SELECT id, name FROM users";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.ORACLE.getCode());
        assertTrue(result.contains("select * from ( select rownum rownum_,temp_.* from ("));
        assertTrue(result.contains("SELECT id, name FROM users"));
        assertTrue(result.contains("rownum <= " + pagination.getEndIndex()));
        assertTrue(result.contains("rownum_ > " + pagination.getBeginIndex()));
    }

    @Test
    public void testGetPageCommand_SecondPage() {
        String sql = "SELECT id, name FROM users";
        Pagination pagination = new Pagination();
        pagination.setPageNum(2);
        pagination.setPageSize(10);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.ORACLE.getCode());
        assertTrue(result.contains("rownum <= " + pagination.getEndIndex()));
        assertTrue(result.contains("rownum_ > " + pagination.getBeginIndex()));
    }

    @Test
    public void testGetPageCommand_WithOrderBy() {
        String sql = "SELECT id, name FROM users ORDER BY id";
        Pagination pagination = new Pagination();
        pagination.setPageNum(1);
        pagination.setPageSize(20);

        String result = pageHandler.getPageCommand(sql, pagination, DatabaseDialect.ORACLE.getCode());
        assertTrue(result.contains("ORDER BY id"));
    }
}

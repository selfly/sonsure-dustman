/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.common.parse;

import com.sonsure.dumper.common.parse.GenericTokenParser;
import com.sonsure.dumper.common.parse.TokenHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GenericTokenParser 单元测试
 */
public class GenericTokenParserTest {

    private GenericTokenParser parser;
    private Map<String, String> replacements;
    private TestTokenHandler handler;

    @BeforeEach
    public void setUp() {
        replacements = new HashMap<>();
        handler = new TestTokenHandler(replacements);
        parser = new GenericTokenParser("#{", "}", handler);
    }

    /**
     * 测试用的 TokenHandler 实现
     */
    private static class TestTokenHandler implements TokenHandler {

        private static final Pattern PATTERN = Pattern.compile(":\\w+");

        private final Map<String, String> params;

        public TestTokenHandler(Map<String, String> params) {
            this.params = params;
        }

        @Override
        public String handleToken(String content) {
            Matcher matcher = PATTERN.matcher(content);
            boolean hasMatch = false;
            while (matcher.find()) {
                hasMatch = true;
                String name = matcher.group().substring(1);
                Object val = params.get(name);
                if (val == null) {
                    return "";
                }
            }
            if (hasMatch) {
                return content;
            } else {
                String val = params.get(content);
                return val == null ? "" : val;
            }
        }
    }

// ========== 基础 SQL 替换测试 ==========

    @Test
    public void testParse_SimpleSqlReplacement() {
        replacements.put("username", "testuser");

        String sql = "SELECT * FROM users WHERE username = #{username}";
        String result = parser.parse(sql);

        Assertions.assertEquals("SELECT * FROM users WHERE username = testuser", result);
    }

    @Test
    public void testParse_MultipleReplacements() {
        replacements.put("id", "1");
        replacements.put("name", "John");
        replacements.put("email", "john@example.com");

        String sql = "SELECT * FROM users WHERE id = #{id} AND name = #{name} AND email = #{email}";
        String result = parser.parse(sql);

        Assertions.assertEquals("SELECT * FROM users WHERE id = 1 AND name = John AND email = john@example.com", result);
    }

    @Test
    public void testParse_UnreplacedTokens() {
        String sql = "SELECT * FROM users WHERE id = #{id}";
        String result = parser.parse(sql);

        // 未替换的标记应该保留为
        Assertions.assertEquals("SELECT * FROM users WHERE id = ", result);
    }

    @Test
    public void testParse_EmptyString() {
        String sql = "";
        String result = parser.parse(sql);

        Assertions.assertEquals("", result);
    }

    @Test
    public void testParse_NullString() {
        String sql = null;
        String result = parser.parse(sql);

        Assertions.assertEquals("", result);
    }

// ========== 嵌套标记测试（重点） ==========

    @Test
    public void testParse_DoubleNestedTokens() {
        replacements.put("inner3", "val3");
        replacements.put("inner2_val3", "val2");
        replacements.put("inner_val2", "innerValue");
        replacements.put("outer_innerValue", "outerInnerValue");

        String sql = "SELECT * FROM users WHERE #{outer_#{inner_#{inner2_#{inner3}}}}";
        String result = parser.parse(sql);

        // 嵌套标记应该被正确处理，内部的 #{inner} 应该先被处理
        // 但由于嵌套处理逻辑，可能会找到外层的 }，然后处理整个 "outer #{inner"
        // 实际行为取决于嵌套处理算法
        Assertions.assertNotNull(result);
        Assertions.assertEquals("SELECT * FROM users WHERE outerInnerValue", result);
    }

    @Test
    public void testParse_ComplexNestedCondition() {
        replacements.put("id", "123");
        replacements.put("username", "user");
        String sql = "SELECT * FROM users WHERE 1=1 #{and id = :id} #{and (username = :username or email = :username)}";
        String result = parser.parse(sql);

        Assertions.assertEquals("SELECT * FROM users WHERE 1=1 and id = :id and (username = :username or email = :username)", result);
    }

    @Test
    public void testParse_ConditionWithNestedVariable() {
        replacements.put("id", "123");
        replacements.put("email", "user@qq.com");

        String sql = "SELECT * FROM users WHERE 1=1 #{and id = :id} #{and username = :username} #{and email = :email}";
        String result = parser.parse(sql);

        Assertions.assertEquals("SELECT * FROM users WHERE 1=1 and id = :id  and email = :email", result);
    }

    @Test
    public void testParse_ConditionWithLIKE() {
        replacements.clear();
        String sql = "SELECT * FROM users WHERE 1=1 #{and name LIKE :pattern}";
        String result = parser.parse(sql);
        Assertions.assertEquals("SELECT * FROM users WHERE 1=1", result.trim());

        replacements.put("pattern", "'%test%'");
        result = parser.parse(sql);

        Assertions.assertEquals("SELECT * FROM users WHERE 1=1 and name LIKE :pattern", result);
    }

// ========== 转义测试 ==========

    @Test
    public void testParse_EscapedToken() {
        replacements.put("username", "testuser");

        String sql = "SELECT * FROM users WHERE username = \\#{username}";
        String result = parser.parse(sql);

        // 转义的标记不应该被替换
        Assertions.assertEquals("SELECT * FROM users WHERE username = #{username}", result);
    }

    @Test
    public void testParse_EscapedAndNormalTokens() {
        replacements.put("id", "1");
        replacements.put("name", "John");

        String sql = "SELECT * FROM users WHERE id = #{id} AND name = \\#{name}";
        String result = parser.parse(sql);

        Assertions.assertEquals("SELECT * FROM users WHERE id = 1 AND name = #{name}", result);
    }

    @Test
    public void testParse_BackslashBeforeNonToken() {
        String sql = "SELECT * FROM users WHERE name = \\test";
        String result = parser.parse(sql);

        Assertions.assertEquals("SELECT * FROM users WHERE name = \\test", result);
    }

    @Test
    public void testParse_TokenAtStart() {
        replacements.put("id", "1");

        String sql = "#{id} = 1";
        String result = parser.parse(sql);

        Assertions.assertEquals("1 = 1", result);
    }

    @Test
    public void testParse_TokenAtEnd() {
        replacements.put("id", "1");

        String sql = "id = #{id}";
        String result = parser.parse(sql);

        Assertions.assertEquals("id = 1", result);
    }

    @Test
    public void testParse_OnlyToken() {
        replacements.put("value", "test");

        String sql = "#{value}";
        String result = parser.parse(sql);

        Assertions.assertEquals("test", result);
    }

    @Test
    public void testParse_AdjacentTokens() {
        replacements.put("a", "A");
        replacements.put("b", "B");

        String sql = "#{a}#{b}";
        String result = parser.parse(sql);

        Assertions.assertEquals("AB", result);
    }

    @Test
    public void testParse_EmptyToken() {
        replacements.put("", "EMPTY");

        String sql = "SELECT * FROM users WHERE id = #{}";
        String result = parser.parse(sql);

        Assertions.assertEquals("SELECT * FROM users WHERE id = EMPTY", result);
    }

    @Test
    public void testParse_WhitespaceInToken() {
        replacements.put("划分名字", "test name");

        String sql = "SELECT * FROM users WHERE name = #{划分名字}";
        String result = parser.parse(sql);

        Assertions.assertEquals("SELECT * FROM users WHERE name = test name", result);
    }

// ========== 未闭合标记测试 ==========

    @Test
    public void testParse_UnclosedToken() {
        replacements.put("id", "1");

        String sql = "SELECT * FROM users WHERE id = #{id";
        String result = parser.parse(sql);

        // 未闭合的标记应该保留原样
        Assertions.assertEquals("SELECT * FROM users WHERE id = #{id", result);
    }

    @Test
    public void testParse_UnclosedNestedToken() {
        String sql = "SELECT * FROM users WHERE #{id = :id";
        String result = parser.parse(sql);

        // 未闭合的嵌套标记应该保留原样
        Assertions.assertEquals("SELECT * FROM users WHERE #{id = :id", result);
    }

    @Test
    public void testParse_OnlyOpenToken() {
        String sql = "#{";
        String result = parser.parse(sql);

        Assertions.assertEquals("#{", result);
    }

// ========== 复杂 SQL 场景测试 ==========

    @Test
    public void testParse_ComplexSelectStatement() {
        replacements.put("condition", "id > 10");
        replacements.put("orderBy", "id DESC");

        String sql = "SELECT * FROM users WHERE #{condition} ORDER BY #{orderBy}";
        String result = parser.parse(sql);

        Assertions.assertEquals("SELECT * FROM users WHERE id > 10 ORDER BY id DESC", result);
    }

    @Test
    public void testParse_UpdateStatement() {
        replacements.put("setClause", "name = 'newName', email = 'new@example.com'");
        replacements.put("whereClause", "id = 1");

        String sql = "UPDATE users SET #{setClause} WHERE #{whereClause}";
        String result = parser.parse(sql);

        Assertions.assertEquals("UPDATE users SET name = 'newName', email = 'new@example.com' WHERE id = 1", result);
    }

    @Test
    public void testParse_InsertStatement() {
        replacements.put("columns", "name, email, age");
        replacements.put("values", "'John', 'john@example.com', 30");

        String sql = "INSERT INTO users (#{columns}) VALUES (#{values})";
        String result = parser.parse(sql);

        Assertions.assertEquals("INSERT INTO users (name, email, age) VALUES ('John', 'john@example.com', 30)", result);
    }

    @Test
    public void testParse_DeleteStatement() {
        replacements.put("whereClause", "id = 1");

        String sql = "DELETE FROM users WHERE #{whereClause}";
        String result = parser.parse(sql);

        Assertions.assertEquals("DELETE FROM users WHERE id = 1", result);
    }

    @Test
    public void testParse_JoinStatement() {
        replacements.put("joinCondition", "u.id = o.user_id");
        replacements.put("whereClause", "u.status = 1");

        String sql = "SELECT * FROM users u JOIN orders o ON #{joinCondition} WHERE #{whereClause}";
        String result = parser.parse(sql);

        Assertions.assertEquals("SELECT * FROM users u JOIN orders o ON u.id = o.user_id WHERE u.status = 1", result);
    }

// ========== 特殊字符测试 ==========

    @Test
    public void testParse_TokenWithSpecialCharacters() {
        replacements.put("path/to/file", "/var/log/app.log");

        String sql = "SELECT * FROM logs WHERE file_path = #{path/to/file}";
        String result = parser.parse(sql);

        Assertions.assertEquals("SELECT * FROM logs WHERE file_path = /var/log/app.log", result);
    }

    @Test
    public void testParse_TokenWithQuotes() {
        replacements.put("'name'", "John");

        String sql = "SELECT * FROM users WHERE name = #{'name'}";
        String result = parser.parse(sql);

        Assertions.assertEquals("SELECT * FROM users WHERE name = John", result);
    }

    @Test
    public void testParse_TokenWithParentheses() {
        replacements.put("(id)", "1");

        String sql = "SELECT * FROM users WHERE id = #{(id)}";
        String result = parser.parse(sql);

        Assertions.assertEquals("SELECT * FROM users WHERE id = 1", result);
    }

    @Test
    public void testParse_TokenWithOperators() {
        replacements.put("id + 1", "2");

        String sql = "SELECT * FROM users WHERE id = #{id + 1}";
        String result = parser.parse(sql);

        Assertions.assertEquals("SELECT * FROM users WHERE id = 2", result);
    }

// ========== 多行 SQL 测试 ==========

    @Test
    public void testParse_MultilineSql() {
        replacements.put("condition", "id > 10");

        String sql = "SELECT *\nFROM users\nWHERE #{condition}\nORDER BY id";
        String result = parser.parse(sql);

        Assertions.assertEquals("SELECT *\nFROM users\nWHERE id > 10\nORDER BY id", result);
    }

    @Test
    public void testParse_MultilineWithMultipleTokens() {
        replacements.put("selectClause", "id, name, email");
        replacements.put("whereClause", "status = 1");
        replacements.put("orderClause", "id DESC");

        String sql = "SELECT #{selectClause}\nFROM users\nWHERE #{whereClause}\nORDER BY #{orderClause}";
        String result = parser.parse(sql);

        Assertions.assertEquals("SELECT id, name, email\nFROM users\nWHERE status = 1\nORDER BY id DESC", result);
    }

// ========== 性能测试（大量标记） ==========

    @Test
    public void testParse_ManyTokens() {
        for (int i = 0; i < 100; i++) {
            replacements.put("var" + i, "value" + i);
        }

        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM users WHERE ");
        for (int i = 0; i < 100; i++) {
            if (i > 0) {
                sqlBuilder.append(" AND ");
            }
            sqlBuilder.append("col").append(i).append(" = #{var").append(i).append("}");
        }

        String result = parser.parse(sqlBuilder.toString());

        Assertions.assertNotNull(result);
        // 验证所有标记都被替换
        for (int i = 0; i < 100; i++) {
            Assertions.assertTrue(result.contains("value" + i));
            Assertions.assertFalse(result.contains("[UNREPLACED:var" + i + "]"));
        }
    }

// ========== Getter 方法测试 ==========

    @Test
    public void testGetOpenToken() {
        Assertions.assertEquals("#{", parser.getOpenToken());
    }

    @Test
    public void testGetCloseToken() {
        Assertions.assertEquals("}", parser.getCloseToken());
    }

    @Test
    public void testGetHandler() {
        Assertions.assertNotNull(parser.getHandler());
        Assertions.assertSame(handler, parser.getHandler());
    }

    @Test
    public void testParse_DynamicOrderBy() {
        replacements.put("column_name", "name");
        replacements.put("direction", "ASC");

        String sql = "SELECT * FROM users ORDER BY #{column_name} #{direction}";
        String result = parser.parse(sql);

        Assertions.assertEquals("SELECT * FROM users ORDER BY name ASC", result);
    }

}

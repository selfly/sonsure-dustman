package com.sonsure.dustman.test.common.utils;

import com.sonsure.dustman.common.utils.EncryptUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EncryptUtils 测试类
 */
@DisplayName("EncryptUtils 工具类测试")
public class EncryptUtilsTest {

    @Test
    @DisplayName("测试 getMD5 - 字符串输入")
    public void testGetMD5_String() {
        String input = "hello world";
        String result = EncryptUtils.getMD5(input);
        
        assertNotNull(result);
        assertEquals(32, result.length());
        assertTrue(result.matches("[0-9a-f]{32}"));
        
        // 相同输入应该产生相同输出
        assertEquals(result, EncryptUtils.getMD5(input));
    }

    @Test
    @DisplayName("测试 getMD5 - 空字符串")
    public void testGetMD5_EmptyString() {
        String result = EncryptUtils.getMD5("");
        assertNotNull(result);
        assertEquals(32, result.length());
        assertTrue(result.matches("[0-9a-f]{32}"));
    }

    @Test
    @DisplayName("测试 getMD5 - 带盐")
    public void testGetMD5_WithSalt() {
        String input = "password";
        String salt = "mysalt";
        
        String result1 = EncryptUtils.getMD5(input, salt);
        String result2 = EncryptUtils.getMD5(input, "");
        
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(32, result1.length());
        // 不同盐应该产生不同结果
        assertNotEquals(result1, result2);
        
        // 相同输入和盐应该产生相同结果
        assertEquals(result1, EncryptUtils.getMD5(input, salt));
    }

    @Test
    @DisplayName("测试 getMD5 - 迭代次数")
    public void testGetMD5_WithIterations() {
        String input = "test";
        
        String result1 = EncryptUtils.getMD5(input, 1);
        String result2 = EncryptUtils.getMD5(input, 3);
        
        assertNotNull(result1);
        assertNotNull(result2);
        // 不同迭代次数可能产生不同结果（取决于MD5实现）
        assertEquals(32, result1.length());
        assertEquals(32, result2.length());
    }

    @Test
    @DisplayName("测试 getMD5 - 字符串、盐和迭代次数")
    public void testGetMD5_WithSaltAndIterations() {
        String input = "password";
        String salt = "salt123";
        
        String result1 = EncryptUtils.getMD5(input, salt, 1);
        String result2 = EncryptUtils.getMD5(input, salt, 2);
        
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(32, result1.length());
        assertEquals(32, result2.length());
    }

    @Test
    @DisplayName("测试 getMD5 - 字节数组输入")
    public void testGetMD5_Bytes() {
        byte[] input = "hello world".getBytes();
        
        String result = EncryptUtils.getMD5(input);
        
        assertNotNull(result);
        assertEquals(32, result.length());
        assertTrue(result.matches("[0-9a-f]{32}"));
    }

    @Test
    @DisplayName("测试 getMD5 - 字节数组、盐和迭代次数")
    public void testGetMD5_BytesWithSaltAndIterations() {
        byte[] input = "test data".getBytes();
        String salt = "salt";
        
        String result1 = EncryptUtils.getMD5(input, salt, 1);
        String result2 = EncryptUtils.getMD5(input, salt, 1);
        
        assertNotNull(result1);
        assertEquals(32, result1.length());
        // 相同输入应该产生相同结果
        assertEquals(result1, result2);
    }

    @Test
    @DisplayName("测试 getMD5 - 需要补齐前导0的情况")
    public void testGetMD5_LeadingZeros() {
        // 找一个会产生前导0的输入（MD5以0x0开头）
        byte[] bytes = new byte[]{0, 0, 0, 0};
        String result = EncryptUtils.getMD5(bytes);
        
        assertNotNull(result);
        assertEquals(32, result.length());
        assertTrue(result.matches("[0-9a-f]{32}"));
    }

    // ========== SHA-256 测试 ==========

    @Test
    @DisplayName("测试 getSHA256 - 字符串输入")
    public void testGetSHA256_String() {
        String input = "hello world";
        String result = EncryptUtils.getSHA256(input);
        
        assertNotNull(result);
        assertEquals(64, result.length());
        assertTrue(result.matches("[0-9a-f]{64}"));
        
        // 相同输入应该产生相同输出
        assertEquals(result, EncryptUtils.getSHA256(input));
    }

    @Test
    @DisplayName("测试 getSHA256 - 空字符串")
    public void testGetSHA256_EmptyString() {
        String result = EncryptUtils.getSHA256("");
        assertNotNull(result);
        assertEquals(64, result.length());
        assertTrue(result.matches("[0-9a-f]{64}"));
    }

    @Test
    @DisplayName("测试 getSHA256 - 带盐")
    public void testGetSHA256_WithSalt() {
        String input = "password";
        String salt = "mysalt";
        
        String result1 = EncryptUtils.getSHA256(input, salt);
        String result2 = EncryptUtils.getSHA256(input, "");
        
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(64, result1.length());
        // 不同盐应该产生不同结果
        assertNotEquals(result1, result2);
        
        // 相同输入和盐应该产生相同结果
        assertEquals(result1, EncryptUtils.getSHA256(input, salt));
    }

    @Test
    @DisplayName("测试 getSHA256 - 迭代次数")
    public void testGetSHA256_WithIterations() {
        String input = "test";
        
        String result1 = EncryptUtils.getSHA256(input, 1);
        String result2 = EncryptUtils.getSHA256(input, 3);
        
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(64, result1.length());
        assertEquals(64, result2.length());
    }

    @Test
    @DisplayName("测试 getSHA256 - 字符串、盐和迭代次数")
    public void testGetSHA256_WithSaltAndIterations() {
        String input = "password";
        String salt = "salt123";
        
        String result1 = EncryptUtils.getSHA256(input, salt, 1);
        String result2 = EncryptUtils.getSHA256(input, salt, 2);
        
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(64, result1.length());
        assertEquals(64, result2.length());
    }

    @Test
    @DisplayName("测试 getSHA256 - 字节数组输入")
    public void testGetSHA256_Bytes() {
        byte[] input = "hello world".getBytes();
        
        String result = EncryptUtils.getSHA256(input);
        
        assertNotNull(result);
        assertEquals(64, result.length());
        assertTrue(result.matches("[0-9a-f]{64}"));
    }

    @Test
    @DisplayName("测试 getSHA256 - 字节数组、盐和迭代次数")
    public void testGetSHA256_BytesWithSaltAndIterations() {
        byte[] input = "test data".getBytes();
        String salt = "salt";
        
        String result1 = EncryptUtils.getSHA256(input, salt, 1);
        String result2 = EncryptUtils.getSHA256(input, salt, 1);
        
        assertNotNull(result1);
        assertEquals(64, result1.length());
        // 相同输入应该产生相同结果
        assertEquals(result1, result2);
    }

    @Test
    @DisplayName("测试 getSHA256 - 需要补齐前导0的情况")
    public void testGetSHA256_LeadingZeros() {
        byte[] bytes = new byte[]{0, 0, 0, 0};
        String result = EncryptUtils.getSHA256(bytes);
        
        assertNotNull(result);
        assertEquals(64, result.length());
        assertTrue(result.matches("[0-9a-f]{64}"));
    }

    // ========== CRC32 测试 ==========

    @Test
    @DisplayName("测试 getCRC32 - 字符串输入")
    public void testGetCRC32_String() {
        String input = "hello world";
        String result = EncryptUtils.getCRC32(input);
        
        assertNotNull(result);
        assertEquals(8, result.length());
        assertTrue(result.matches("[0-9a-f]{8}"));
        
        // 相同输入应该产生相同输出
        assertEquals(result, EncryptUtils.getCRC32(input));
    }

    @Test
    @DisplayName("测试 getCRC32 - 空字符串")
    public void testGetCRC32_EmptyString() {
        String result = EncryptUtils.getCRC32("");
        assertNotNull(result);
        assertEquals(8, result.length());
        assertTrue(result.matches("[0-9a-f]{8}"));
    }

    @Test
    @DisplayName("测试 getCRC32 - 字节数组输入")
    public void testGetCRC32_Bytes() {
        byte[] input = "hello world".getBytes();
        
        String result = EncryptUtils.getCRC32(input);
        
        assertNotNull(result);
        assertEquals(8, result.length());
        assertTrue(result.matches("[0-9a-f]{8}"));
    }

    @Test
    @DisplayName("测试 getCRC32 - 字节数组和字符串应该得到相同结果")
    public void testGetCRC32_BytesVsString() {
        String input = "test";
        String result1 = EncryptUtils.getCRC32(input);
        String result2 = EncryptUtils.getCRC32(input.getBytes());
        
        assertEquals(result1, result2);
    }

    @Test
    @DisplayName("测试 getCRC32 - 带盐")
    public void testGetCRC32_WithSalt() {
        String input = "password";
        String salt = "mysalt";
        
        String result1 = EncryptUtils.getCRC32(input, salt);
        String result2 = EncryptUtils.getCRC32(input, "");
        
        assertNotNull(result1);
        assertEquals(8, result1.length());
        // 不同盐应该产生不同结果
        assertNotEquals(result1, result2);
        
        // 相同输入和盐应该产生相同结果
        assertEquals(result1, EncryptUtils.getCRC32(input, salt));
    }

    @Test
    @DisplayName("测试 getCRC32 - null 盐")
    public void testGetCRC32_NullSalt() {
        String input = "test";
        
        String result1 = EncryptUtils.getCRC32(input, null);
        String result2 = EncryptUtils.getCRC32(input, "");
        
        assertEquals(result1, result2);
    }

    @Test
    @DisplayName("测试 getCRC32 - 空盐")
    public void testGetCRC32_EmptySalt() {
        String input = "test";
        
        String result1 = EncryptUtils.getCRC32(input, "");
        String result2 = EncryptUtils.getCRC32(input);
        
        assertEquals(result1, result2);
    }

    // ========== BCrypt 测试 ==========

    @Test
    @DisplayName("测试 encodeBCrypt - 基本加密")
    public void testEncodeBCrypt_Basic() {
        String password = "mypassword";
        String encoded = EncryptUtils.encodeBCrypt(password);
        
        assertNotNull(encoded);
        assertTrue(encoded.startsWith("$2a$"));
        assertTrue(encoded.matches("\\A\\$2a\\$\\d\\d\\$[./0-9A-Za-z]{53}"));
        
        // 相同密码每次加密结果不同（因为有随机盐）
        String encoded2 = EncryptUtils.encodeBCrypt(password);
        assertNotEquals(encoded, encoded2);
    }

    @Test
    @DisplayName("测试 encodeBCrypt - 指定强度")
    public void testEncodeBCrypt_WithStrength() {
        String password = "mypassword";
        
        String encoded10 = EncryptUtils.encodeBCrypt(password, 10);
        String encoded12 = EncryptUtils.encodeBCrypt(password, 12);
        
        assertNotNull(encoded10);
        assertNotNull(encoded12);
        assertTrue(encoded10.startsWith("$2a$10$"));
        assertTrue(encoded12.startsWith("$2a$12$"));
        assertTrue(encoded10.matches("\\A\\$2a\\$10\\$[./0-9A-Za-z]{53}"));
        assertTrue(encoded12.matches("\\A\\$2a\\$12\\$[./0-9A-Za-z]{53}"));
    }

    @Test
    @DisplayName("测试 encodeBCrypt - 自定义SecureRandom")
    public void testEncodeBCrypt_WithSecureRandom() {
        String password = "mypassword";
        SecureRandom random = new SecureRandom();
        
        String encoded = EncryptUtils.encodeBCrypt(password, 10, random);
        
        assertNotNull(encoded);
        assertTrue(encoded.startsWith("$2a$10$"));
        assertTrue(encoded.matches("\\A\\$2a\\$10\\$[./0-9A-Za-z]{53}"));
    }

    @Test
    @DisplayName("测试 encodeBCrypt - 空密码")
    public void testEncodeBCrypt_EmptyPassword() {
        String password = "";
        String encoded = EncryptUtils.encodeBCrypt(password);
        
        assertNotNull(encoded);
        assertTrue(encoded.startsWith("$2a$"));
    }

    @ParameterizedTest
    @DisplayName("测试 encodeBCrypt - 不同强度的 rounds")
    @ValueSource(ints = {4, 5, 10, 15})
    public void testEncodeBCrypt_DifferentStrengths(int strength) {
        String password = "testpass";
        String encoded = EncryptUtils.encodeBCrypt(password, strength);
        
        assertNotNull(encoded);
        assertTrue(encoded.startsWith("$2a$"));
    }

    @Test
    @DisplayName("测试 matchesBCrypt - 匹配成功")
    public void testMatchesBCrypt_Success() {
        String password = "mypassword";
        String encoded = EncryptUtils.encodeBCrypt(password);
        
        assertTrue(EncryptUtils.matchesBCrypt(password, encoded));
    }

    @Test
    @DisplayName("测试 matchesBCrypt - 匹配失败")
    public void testMatchesBCrypt_Fail() {
        String password = "mypassword";
        String wrongPassword = "wrongpassword";
        String encoded = EncryptUtils.encodeBCrypt(password);
        
        assertFalse(EncryptUtils.matchesBCrypt(wrongPassword, encoded));
    }

    @Test
    @DisplayName("测试 matchesBCrypt - 空密码")
    public void testMatchesBCrypt_EmptyPassword() {
        String password = "";
        String encoded = EncryptUtils.encodeBCrypt(password);
        
        assertTrue(EncryptUtils.matchesBCrypt(password, encoded));
    }

    @Test
    @DisplayName("测试 matchesBCrypt - null 参数")
    public void testMatchesBCrypt_Null() {
        assertFalse(EncryptUtils.matchesBCrypt("password", null));
        assertFalse(EncryptUtils.matchesBCrypt("password", ""));
    }

    @Test
    @DisplayName("测试 matchesBCrypt - 无效格式")
    public void testMatchesBCrypt_InvalidFormat() {
        assertFalse(EncryptUtils.matchesBCrypt("password", "invalid"));
        assertFalse(EncryptUtils.matchesBCrypt("password", "$2a$10$short"));
        assertFalse(EncryptUtils.matchesBCrypt("password", "$2b$10$invalid"));
    }

    @Test
    @DisplayName("测试 matchesBCrypt - 多种强度的哈希")
    public void testMatchesBCrypt_DifferentStrengths() {
        String password = "testpass";
        
        for (int strength = 4; strength <= 20; strength++) {
            String encoded = EncryptUtils.encodeBCrypt(password, strength);
            assertTrue(EncryptUtils.matchesBCrypt(password, encoded), 
                "Strength " + strength + " should match");
        }
    }

    @Test
    @DisplayName("测试 encodeBCrypt 和 matchesBCrypt - 多次加密验证")
    public void testEncodeBCrypt_MatchesBCrypt_MultipleTimes() {
        String password = "complex_password_123";
        
        // 对同一个密码加密多次，每次结果都不同
        String encoded1 = EncryptUtils.encodeBCrypt(password);
        String encoded2 = EncryptUtils.encodeBCrypt(password);
        String encoded3 = EncryptUtils.encodeBCrypt(password);
        
        // 但都应该能正确验证密码
        assertTrue(EncryptUtils.matchesBCrypt(password, encoded1));
        assertTrue(EncryptUtils.matchesBCrypt(password, encoded2));
        assertTrue(EncryptUtils.matchesBCrypt(password, encoded3));
        
        // 每次加密的结果应该不同（因为有随机盐）
        assertNotEquals(encoded1, encoded2);
        assertNotEquals(encoded2, encoded3);
        assertNotEquals(encoded1, encoded3);
    }

    // ========== 边界情况测试 ==========

    @Test
    @DisplayName("测试 getMD5 - null 值处理")
    public void testGetMD5_NullString() {
        String result = EncryptUtils.getMD5((String) null);
        assertNotNull(result);
        assertEquals(32, result.length());
    }

    @Test
    @DisplayName("测试 getSHA256 - null 值处理")
    public void testGetSHA256_NullString() {
        String result = EncryptUtils.getSHA256((String) null);
        assertNotNull(result);
        assertEquals(64, result.length());
    }

    @Test
    @DisplayName("测试 getCRC32 - null 值处理")
    public void testGetCRC32_NullString() {
        String result = EncryptUtils.getCRC32((String) null);
        assertNotNull(result);
        assertEquals(8, result.length());
    }

    @Test
    @DisplayName("测试 getCRC32 - null 字节数组")
    public void testGetCRC32_NullBytes() {
        // 这会抛出 NullPointerException，验证行为
        assertThrows(NullPointerException.class, () -> {
            EncryptUtils.getCRC32((byte[]) null);
        });
    }

    @ParameterizedTest
    @DisplayName("测试 getMD5 - 特殊字符和Unicode")
    @ValueSource(strings = {
        "你好世界",
        "Hello 世界",
        "特殊字符!@#$%^&*()",
        "tab\t换行\n字符",
        "   leading and trailing spaces   "
    })
    public void testGetMD5_SpecialCharacters(String input) {
        String result = EncryptUtils.getMD5(input);
        assertNotNull(result);
        assertEquals(32, result.length());
        assertTrue(result.matches("[0-9a-f]{32}"));
    }

    @Test
    @DisplayName("测试 getMD5 - 长字符串")
    public void testGetMD5_LongString() {
        // 生成一个长字符串
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("a");
        }
        String longString = sb.toString();
        
        String result = EncryptUtils.getMD5(longString);
        assertNotNull(result);
        assertEquals(32, result.length());
        assertTrue(result.matches("[0-9a-f]{32}"));
    }

    @ParameterizedTest
    @DisplayName("测试 getSHA256 - 特殊字符和Unicode")
    @ValueSource(strings = {
        "你好世界",
        "Hello 世界",
        "特殊字符!@#$%^&*()",
        "tab\t换行\n字符",
        "   leading and trailing spaces   "
    })
    public void testGetSHA256_SpecialCharacters(String input) {
        String result = EncryptUtils.getSHA256(input);
        assertNotNull(result);
        assertEquals(64, result.length());
        assertTrue(result.matches("[0-9a-f]{64}"));
    }

    @ParameterizedTest
    @DisplayName("测试 encodeBCrypt - 特殊字符和Unicode")
    @ValueSource(strings = {
        "你好世界",
        "Hello 世界",
        "特殊字符!@#$%^&*()",
        "tab\t换行\n字符"
    })
    public void testEncodeBCrypt_SpecialCharacters(String password) {
        String encoded = EncryptUtils.encodeBCrypt(password);
        
        assertNotNull(encoded);
        assertTrue(encoded.startsWith("$2a$"));
        
        // 应该能正确验证
        assertTrue(EncryptUtils.matchesBCrypt(password, encoded));
    }

    @ParameterizedTest
    @DisplayName("测试 getMD5 - 相同输入不同参数的重载方法")
    @CsvSource({
        "test, mysalt, 1",
        "test, mysalt, 3",
        "test, '', 1",
        "test, '', 3"
    })
    public void testGetMD5_Overloads(String str, String salt, int iterations) {
        String result = EncryptUtils.getMD5(str, salt, iterations);
        
        assertNotNull(result);
        assertEquals(32, result.length());
        assertTrue(result.matches("[0-9a-f]{32}"));
    }

    @ParameterizedTest
    @DisplayName("测试 getSHA256 - 相同输入不同参数的重载方法")
    @CsvSource({
        "test, mysalt, 1",
        "test, mysalt, 3",
        "test, '', 1",
        "test, '', 3"
    })
    public void testGetSHA256_Overloads(String str, String salt, int iterations) {
        String result = EncryptUtils.getSHA256(str, salt, iterations);
        
        assertNotNull(result);
        assertEquals(64, result.length());
        assertTrue(result.matches("[0-9a-f]{64}"));
    }

    @Test
    @DisplayName("测试 BCrypt - 验证生成格式的一致性")
    public void testBCrypt_FormatConsistency() {
        String password = "test";
        
        // 生成多个加密字符串
        for (int i = 0; i < 10; i++) {
            String encoded = EncryptUtils.encodeBCrypt(password);
            
            // 验证格式
            assertTrue(encoded.matches("\\A\\$2a\\$\\d\\d\\$[./0-9A-Za-z]{53}"));
            assertEquals(60, encoded.length());
            
            // 验证能够匹配
            assertTrue(EncryptUtils.matchesBCrypt(password, encoded));
        }
    }

    @Test
    @DisplayName("测试 getMD5 和 getSHA256 - 结果长度验证")
    public void testHashLengths() {
        String input = "test";
        
        String md5 = EncryptUtils.getMD5(input);
        String sha256 = EncryptUtils.getSHA256(input);
        
        assertEquals(32, md5.length());
        assertEquals(64, sha256.length());
        assertTrue(md5.matches("[0-9a-f]{32}"));
        assertTrue(sha256.matches("[0-9a-f]{64}"));
    }

    @Test
    @DisplayName("测试 getCRC32 - 不同内容的CRC32应该不同")
    public void testGetCRC32_DifferentContent() {
        String input1 = "content1";
        String input2 = "content2";
        
        String result1 = EncryptUtils.getCRC32(input1);
        String result2 = EncryptUtils.getCRC32(input2);
        
        // 不同内容应该产生不同的CRC32值
        assertNotEquals(result1, result2);
    }

    @Test
    @DisplayName("测试 多次哈希的幂等性")
    public void testHashIdempotency() {
        String input = "test";
        String salt = "salt";
        
        // MD5
        String md5_1 = EncryptUtils.getMD5(input);
        String md5_2 = EncryptUtils.getMD5(input);
        assertEquals(md5_1, md5_2);
        
        // SHA-256
        String sha_1 = EncryptUtils.getSHA256(input);
        String sha_2 = EncryptUtils.getSHA256(input);
        assertEquals(sha_1, sha_2);
        
        // CRC32
        String crc_1 = EncryptUtils.getCRC32(input);
        String crc_2 = EncryptUtils.getCRC32(input);
        assertEquals(crc_1, crc_2);
        
        // 带盐的情况
        String md5_salt_1 = EncryptUtils.getMD5(input, salt);
        String md5_salt_2 = EncryptUtils.getMD5(input, salt);
        assertEquals(md5_salt_1, md5_salt_2);
    }
}

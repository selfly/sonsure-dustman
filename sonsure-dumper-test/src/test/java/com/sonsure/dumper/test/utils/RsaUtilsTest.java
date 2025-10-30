package com.sonsure.dumper.test.utils;

import com.sonsure.dumper.common.exception.SonsureCommonsException;
import com.sonsure.dumper.common.utils.RsaUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class RsaUtilsTest {

    // ==================== 密钥创建测试 ====================

    @Test
    public void testCreateKeysDefault() {
        String[] keys = RsaUtils.createKeys();
        assertNotNull(keys);
        assertEquals(2, keys.length);
        assertNotNull(keys[0]); // 公钥
        assertNotNull(keys[1]); // 私钥
        assertFalse(keys[0].isEmpty());
        assertFalse(keys[1].isEmpty());
    }

    @Test
    public void testCreateKeys1024() {
        String[] keys = RsaUtils.createKeys(1024);
        assertNotNull(keys);
        assertEquals(2, keys.length);
        assertNotNull(keys[0]);
        assertNotNull(keys[1]);
    }

    @Test
    public void testCreateKeys2048() {
        String[] keys = RsaUtils.createKeys(2048);
        assertNotNull(keys);
        assertEquals(2, keys.length);
        assertNotNull(keys[0]);
        assertNotNull(keys[1]);
    }

    @Test
    public void testCreateKeys4096() {
        String[] keys = RsaUtils.createKeys(4096);
        assertNotNull(keys);
        assertEquals(2, keys.length);
        assertNotNull(keys[0]);
        assertNotNull(keys[1]);
    }

    @Test
    public void testCreateKeysWithByteDefault() {
        byte[][] keys = RsaUtils.createKeysWithByte();
        assertNotNull(keys);
        assertEquals(2, keys.length);
        assertNotNull(keys[0]); // 公钥
        assertNotNull(keys[1]); // 私钥
        assertTrue(keys[0].length > 0);
        assertTrue(keys[1].length > 0);
    }

    @Test
    public void testCreateKeysWithByte1024() {
        byte[][] keys = RsaUtils.createKeysWithByte(1024);
        assertNotNull(keys);
        assertEquals(2, keys.length);
        assertNotNull(keys[0]);
        assertNotNull(keys[1]);
    }

    @Test
    public void testCreateKeysWithByte2048() {
        byte[][] keys = RsaUtils.createKeysWithByte(2048);
        assertNotNull(keys);
        assertEquals(2, keys.length);
        assertNotNull(keys[0]);
        assertNotNull(keys[1]);
    }

    // ==================== 公钥加密/私钥解密测试 ====================

    @Test
    public void testEncryptByPublicKeyAndDecryptByPrivateKey() {
        String[] keys = RsaUtils.createKeys(2048);
        String data = "Hello World!";
        
        String encrypted = RsaUtils.encryptByPublicKey(data, keys[0]);
        assertNotNull(encrypted);
        assertFalse(encrypted.isEmpty());
        
        String decrypted = RsaUtils.decryptByPrivateKey(encrypted, keys[1]);
        assertEquals(data, decrypted);
    }

    @Test
    public void testEncryptByPublicKeyByteArrayAndDecryptByPrivateKey() {
        String[] keys = RsaUtils.createKeys(2048);
        byte[] data = "Hello World!".getBytes();
        
        String encrypted = RsaUtils.encryptByPublicKey(data, keys[0]);
        assertNotNull(encrypted);
        
        byte[] decrypted = RsaUtils.decryptByPrivateKeyWithByte(encrypted, keys[1]);
        assertArrayEquals(data, decrypted);
    }

    @Test
    public void testEncryptByPublicKeyEmptyString() {
        String[] keys = RsaUtils.createKeys(2048);
        String data = "";
        
        String encrypted = RsaUtils.encryptByPublicKey(data, keys[0]);
        String decrypted = RsaUtils.decryptByPrivateKey(encrypted, keys[1]);
        assertEquals(data, decrypted);
    }

    @Test
    public void testEncryptByPublicKeyEmptyByteArray() {
        String[] keys = RsaUtils.createKeys(2048);
        byte[] data = new byte[0];
        
        String encrypted = RsaUtils.encryptByPublicKey(data, keys[0]);
        byte[] decrypted = RsaUtils.decryptByPrivateKeyWithByte(encrypted, keys[1]);
        assertArrayEquals(data, decrypted);
    }

    @Test
    public void testEncryptByPublicKeyLargeData() {
        String[] keys = RsaUtils.createKeys(2048);
        // 创建一个大于单个块的数据（2048位的最大块大小是245字节）
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 500; i++) {
            sb.append("这是一个测试数据，用于测试RSA加密算法的分块处理能力。");
        }
        String data = sb.toString();
        
        String encrypted = RsaUtils.encryptByPublicKey(data, keys[0]);
        assertNotNull(encrypted);
        
        String decrypted = RsaUtils.decryptByPrivateKey(encrypted, keys[1]);
        assertEquals(data, decrypted);
    }

    @Test
    public void testEncryptByPublicKeyWith1024Key() {
        String[] keys = RsaUtils.createKeys(1024);
        String data = "Test with 1024 bit key";
        
        String encrypted = RsaUtils.encryptByPublicKey(data, keys[0]);
        String decrypted = RsaUtils.decryptByPrivateKey(encrypted, keys[1]);
        assertEquals(data, decrypted);
    }

    @Test
    public void testEncryptByPublicKeyWith4096Key() {
        String[] keys = RsaUtils.createKeys(4096);
        String data = "Test with 4096 bit key";
        
        String encrypted = RsaUtils.encryptByPublicKey(data, keys[0]);
        String decrypted = RsaUtils.decryptByPrivateKey(encrypted, keys[1]);
        assertEquals(data, decrypted);
    }

    @Test
    public void testEncryptByPublicKeyChineseCharacters() {
        String[] keys = RsaUtils.createKeys(2048);
        String data = "测试中文字符加密：你好世界！😀🎉";
        
        String encrypted = RsaUtils.encryptByPublicKey(data, keys[0]);
        String decrypted = RsaUtils.decryptByPrivateKey(encrypted, keys[1]);
        assertEquals(data, decrypted);
    }

    @Test
    public void testEncryptByPublicKeySpecialCharacters() {
        String[] keys = RsaUtils.createKeys(2048);
        String data = "!@#$%^&*()_+-=[]{}|; Carlos,./<>?~`";
        
        String encrypted = RsaUtils.encryptByPublicKey(data, keys[0]);
        String decrypted = RsaUtils.decryptByPrivateKey(encrypted, keys[1]);
        assertEquals(data, decrypted);
    }

    // ==================== 私钥加密/公钥解密测试 ====================

    @Test
    public void testEncryptByPrivateKeyAndDecryptByPublicKey() {
        String[] keys = RsaUtils.createKeys(2048);
        String data = "Hello World!";
        
        String encrypted = RsaUtils.encryptByPrivateKey(data, keys[1]);
        assertNotNull(encrypted);
        assertFalse(encrypted.isEmpty());
        
        String decrypted = RsaUtils.decryptByPublicKey(encrypted, keys[0]);
        assertEquals(data, decrypted);
    }

    @Test
    public void testEncryptByPrivateKeyByteArrayAndDecryptByPublicKey() {
        String[] keys = RsaUtils.createKeys(2048);
        byte[] data = "Hello World!".getBytes();
        
        String encrypted = RsaUtils.encryptByPrivateKey(data, keys[1]);
        assertNotNull(encrypted);
        
        byte[] decrypted = RsaUtils.decryptByPublicKeyWithByte(encrypted, keys[0]);
        assertArrayEquals(data, decrypted);
    }

    @Test
    public void testEncryptByPrivateKeyEmptyString() {
        String[] keys = RsaUtils.createKeys(2048);
        String data = "";
        
        String encrypted = RsaUtils.encryptByPrivateKey(data, keys[1]);
        String decrypted = RsaUtils.decryptByPublicKey(encrypted, keys[0]);
        assertEquals(data, decrypted);
    }

    @Test
    public void testEncryptByPrivateKeyEmptyByteArray() {
        String[] keys = RsaUtils.createKeys(2048);
        byte[] data = new byte[0];
        
        String encrypted = RsaUtils.encryptByPrivateKey(data, keys[1]);
        byte[] decrypted = RsaUtils.decryptByPublicKeyWithByte(encrypted, keys[0]);
        assertArrayEquals(data, decrypted);
    }

    @Test
    public void testEncryptByPrivateKeyLargeData() {
        String[] keys = RsaUtils.createKeys(2048);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 500; i++) {
            sb.append("这是一个测试数据，用于测试RSA加密算法的分块处理能力。");
        }
        String data = sb.toString();
        
        String encrypted = RsaUtils.encryptByPrivateKey(data, keys[1]);
        assertNotNull(encrypted);
        
        String decrypted = RsaUtils.decryptByPublicKey(encrypted, keys[0]);
        assertEquals(data, decrypted);
    }

    @Test
    public void testEncryptByPrivateKeyWith1024Key() {
        String[] keys = RsaUtils.createKeys(1024);
        String data = "Test with 1024 bit key";
        
        String encrypted = RsaUtils.encryptByPrivateKey(data, keys[1]);
        String decrypted = RsaUtils.decryptByPublicKey(encrypted, keys[0]);
        assertEquals(data, decrypted);
    }

    @Test
    public void testEncryptByPrivateKeyWith4096Key() {
        String[] keys = RsaUtils.createKeys(4096);
        String data = "Test with 4096 bit key";
        
        String encrypted = RsaUtils.encryptByPrivateKey(data, keys[1]);
        String decrypted = RsaUtils.decryptByPublicKey(encrypted, keys[0]);
        assertEquals(data, decrypted);
    }

    // ==================== 混合测试 ====================

    @Test
    public void testCrossEncryptionDecryption() {
        // 测试公钥加密可以用私钥解密，私钥加密可以用公钥解密
        String[] keys = RsaUtils.createKeys(2048);
        String data1 = "Test data 1";
        String data2 = "Test data 2";
        
        // 公钥加密 -> 私钥解密
        String encrypted1 = RsaUtils.encryptByPublicKey(data1, keys[0]);
        String decrypted1 = RsaUtils.decryptByPrivateKey(encrypted1, keys[1]);
        assertEquals(data1, decrypted1);
        
        // 私钥加密 -> 公钥解密
        String encrypted2 = RsaUtils.encryptByPrivateKey(data2, keys[1]);
        String decrypted2 = RsaUtils.decryptByPublicKey(encrypted2, keys[0]);
        assertEquals(data2, decrypted2);
    }

    @Test
    public void testDifferentKeysCannotDecrypt() {
        String[] keys1 = RsaUtils.createKeys(2048);
        String[] keys2 = RsaUtils.createKeys(2048);
        String data = "Secret message";
        
        String encrypted = RsaUtils.encryptByPublicKey(data, keys1[0]);
        
        // 使用不同的私钥解密应该失败
        assertThrows(SonsureCommonsException.class, () -> {
            RsaUtils.decryptByPrivateKey(encrypted, keys2[1]);
        });
    }

    @Test
    public void testMultipleEncryptionDecryption() {
        String[] keys = RsaUtils.createKeys(2048);
        String[] testData = {
            "Short",
            "Medium length text",
            "This is a longer text that might require multiple blocks depending on key size",
            "测试数据" + new String(new char[200]).replace('\0', 'A'),
            ""
        };
        
        for (String data : testData) {
            // 公钥加密/私钥解密
            String encrypted = RsaUtils.encryptByPublicKey(data, keys[0]);
            String decrypted = RsaUtils.decryptByPrivateKey(encrypted, keys[1]);
            assertEquals(data, decrypted, "Failed for data: " + data);
            
            // 私钥加密/公钥解密
            encrypted = RsaUtils.encryptByPrivateKey(data, keys[1]);
            decrypted = RsaUtils.decryptByPublicKey(encrypted, keys[0]);
            assertEquals(data, decrypted, "Failed for data: " + data);
        }
    }

    @Test
    public void testByteArrayRoundTrip() {
        String[] keys = RsaUtils.createKeys(2048);
        byte[] originalData = new byte[256];
        for (int i = 0; i < originalData.length; i++) {
            originalData[i] = (byte) (i % 256);
        }
        
        String encrypted = RsaUtils.encryptByPublicKey(originalData, keys[0]);
        byte[] decrypted = RsaUtils.decryptByPrivateKeyWithByte(encrypted, keys[1]);
        
        assertArrayEquals(originalData, decrypted);
    }

    // ==================== 异常测试 ====================

    @Test
    public void testInvalidPublicKey() {
        String data = "Test";
        String invalidKey = "InvalidKeyBase64String!!!";
        
        assertThrows(SonsureCommonsException.class, () -> {
            RsaUtils.encryptByPublicKey(data, invalidKey);
        });
        
        assertThrows(SonsureCommonsException.class, () -> {
            RsaUtils.decryptByPublicKey("encrypted", invalidKey);
        });
    }

    @Test
    public void testInvalidPrivateKey() {
        String data = "Test";
        String invalidKey = "InvalidKeyBase64String!!!";
        
        assertThrows(SonsureCommonsException.class, () -> {
            RsaUtils.encryptByPrivateKey(data, invalidKey);
        });
        
        assertThrows(SonsureCommonsException.class, () -> {
            RsaUtils.decryptByPrivateKey("encrypted", invalidKey);
        });
    }

    @Test
    public void testInvalidEncryptedData() {
        String[] keys = RsaUtils.createKeys(2048);
        String invalidEncrypted = "InvalidBase64Data!!!";
        
        assertThrows(Exception.class, () -> {
            RsaUtils.decryptByPrivateKey(invalidEncrypted, keys[1]);
        });
        
        assertThrows(Exception.class, () -> {
            RsaUtils.decryptByPublicKey(invalidEncrypted, keys[0]);
        });
    }

    @Test
    public void testNullData() {
        String[] keys = RsaUtils.createKeys(2048);
        
        assertThrows(NullPointerException.class, () -> {
            RsaUtils.encryptByPublicKey((String) null, keys[0]);
        });
        
        assertThrows(NullPointerException.class, () -> {
            RsaUtils.encryptByPrivateKey((String) null, keys[1]);
        });
        
        assertThrows(NullPointerException.class, () -> {
            RsaUtils.encryptByPublicKey((byte[]) null, keys[0]);
        });
        
        assertThrows(NullPointerException.class, () -> {
            RsaUtils.encryptByPrivateKey((byte[]) null, keys[1]);
        });
    }

    // ==================== 边界情况测试 ====================

    @Test
    public void testExactlyMaxBlockSize1024() {
        String[] keys = RsaUtils.createKeys(1024);
        // 1024位的最大块大小是 128 - 11 = 117 字节
        byte[] data = new byte[117];
        Arrays.fill(data, (byte) 'A');
        
        String encrypted = RsaUtils.encryptByPublicKey(data, keys[0]);
        byte[] decrypted = RsaUtils.decryptByPrivateKeyWithByte(encrypted, keys[1]);
        assertArrayEquals(data, decrypted);
    }

    @Test
    public void testExactlyMaxBlockSize2048() {
        String[] keys = RsaUtils.createKeys(2048);
        // 2048位的最大块大小是 256 - 11 = 245 字节
        byte[] data = new byte[245];
        Arrays.fill(data, (byte) 'A');
        
        String encrypted = RsaUtils.encryptByPublicKey(data, keys[0]);
        byte[] decrypted = RsaUtils.decryptByPrivateKeyWithByte(encrypted, keys[1]);
        assertArrayEquals(data, decrypted);
    }

    @Test
    public void testOneByteLessThanMaxBlockSize() {
        String[] keys = RsaUtils.createKeys(2048);
        // 244 字节，比最大值少1
        byte[] data = new byte[244];
        Arrays.fill(data, (byte) 'B');
        
        String encrypted = RsaUtils.encryptByPublicKey(data, keys[0]);
        byte[] decrypted = RsaUtils.decryptByPrivateKeyWithByte(encrypted, keys[1]);
        assertArrayEquals(data, decrypted);
    }

    @Test
    public void testOneByteMoreThanMaxBlockSize() {
        String[] keys = RsaUtils.createKeys(2048);
        // 246 字节，比最大值多1，会触发分块
        byte[] data = new byte[246];
        Arrays.fill(data, (byte) 'C');
        
        String encrypted = RsaUtils.encryptByPublicKey(data, keys[0]);
        byte[] decrypted = RsaUtils.decryptByPrivateKeyWithByte(encrypted, keys[1]);
        assertArrayEquals(data, decrypted);
    }
}

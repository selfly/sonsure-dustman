package com.sonsure.dustman.common.utils;

import com.sonsure.dustman.common.exception.SonsureCommonsException;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author liyd
 */
public class RsaUtils {

    public static final String ALGORITHM_RSA = "RSA";

    /**
     * PKCS#1 v1.5 padding占用的字节数
     */
    private static final int RSA_PADDING_BYTES = 11;

    /**
     * 根据密钥长度获取RSA密文块大小（字节）
     *
     * @param keySize 密钥长度（位）
     * @return 密文块大小（字节）
     */
    private static int getEncryptBlockSize(int keySize) {
        return keySize / 8;
    }

    /**
     * 根据密钥长度获取RSA最大明文块大小（字节）
     *
     * @param keySize 密钥长度（位）
     * @return 最大明文块大小（字节）
     */
    private static int getMaxPlainBlockSize(int keySize) {
        return getEncryptBlockSize(keySize) - RSA_PADDING_BYTES;
    }

    /**
     * 从公钥字符串获取密钥长度（位）
     *
     * @param publicKey 公钥字符串（Base64编码）
     * @return 密钥长度（位）
     */
    private static int getKeySizeFromPublicKey(String publicKey) {
        try {
            byte[] keyBytes = decodeBase64(publicKey);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
            return rsaPublicKey.getModulus().bitLength();
        } catch (Exception e) {
            throw new SonsureCommonsException("获取公钥长度失败", e);
        }
    }

    /**
     * 从私钥字符串获取密钥长度（位）
     *
     * @param privateKey 私钥字符串（Base64编码）
     * @return 密钥长度（位）
     */
    private static int getKeySizeFromPrivateKey(String privateKey) {
        try {
            byte[] keyBytes = decodeBase64(privateKey);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
            return rsaPrivateKey.getModulus().bitLength();
        } catch (Exception e) {
            throw new SonsureCommonsException("获取私钥长度失败", e);
        }
    }

    /**
     * 创建密钥
     *
     * @return string [0]=publicKey [1]=privateKey
     */
    public static String[] createKeys() {
        return createKeys(2048);
    }

    /**
     * 创建密钥
     *
     * @return string [0]=publicKey [1]=privateKey
     */
    public static String[] createKeys(int keySize) {
        final byte[][] keysWithByte = createKeysWithByte(keySize);
        return new String[]{encodeBase64(keysWithByte[0]), encodeBase64(keysWithByte[1])};
    }

    /**
     * 创建密钥
     *
     * @return byte [0]=publicKey [1]=privateKey
     */
    public static byte[][] createKeysWithByte() {
        return createKeysWithByte(2048);
    }

    /**
     * 创建密钥
     *
     * @return byte [0]=publicKey [1]=privateKey
     */
    public static byte[][] createKeysWithByte(int keySize) {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator
                    .getInstance(ALGORITHM_RSA);
            keyPairGen.initialize(keySize);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            final PublicKey publicKey = keyPair.getPublic();
            final PrivateKey privateKey = keyPair.getPrivate();
            return new byte[][]{publicKey.getEncoded(), privateKey.getEncoded()};
        } catch (NoSuchAlgorithmException e) {
            throw new SonsureCommonsException("创建密钥失败", e);
        }
    }

    /**
     * 用私钥加密
     *
     * @param data       the data
     * @param privateKey the private key
     * @return byte [ ]
     */
    public static String encryptByPrivateKey(String data, String privateKey) {
        return encryptByPrivateKey(data.getBytes(), privateKey);
    }

    /**
     * 用私钥加密
     *
     * @param data       the data
     * @param privateKey the private key
     * @return byte [ ]
     */
    public static String encryptByPrivateKey(byte[] data, String privateKey) {
        int keySize = getKeySizeFromPrivateKey(privateKey);
        int maxPlainBlockSize = getMaxPlainBlockSize(keySize);
        final byte[][] splitBytes = split(data, maxPlainBlockSize);
        byte[][] encryptBytes = new byte[splitBytes.length][];
        for (int i = 0; i < splitBytes.length; i++) {
            encryptBytes[i] = encryptByPrivateKeyRsaByte(splitBytes[i], privateKey);
        }
        return encodeBase64(merge(encryptBytes));
    }

    /**
     * 用私钥加密
     *
     * @param data       the data
     * @param privateKey the private key
     * @return byte [ ]
     */
    private static byte[] encryptByPrivateKeyRsaByte(byte[] data, String privateKey) {
        int keySize = getKeySizeFromPrivateKey(privateKey);
        int maxPlainBlockSize = getMaxPlainBlockSize(keySize);
        if (data.length > maxPlainBlockSize) {
            throw new SonsureCommonsException("RSA加密最长允许" + maxPlainBlockSize + "个字节（密钥长度：" + keySize + "位）");
        }
        try {
            // 对密钥解密
            byte[] keyBytes = decodeBase64(privateKey);
            // 取得私钥
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            Key priKey = keyFactory.generatePrivate(pkcs8KeySpec);
            // 对数据加密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, priKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new SonsureCommonsException("使用privateKey加密失败", e);
        }
    }

    /**
     * 用公钥解密
     *
     * @param data      the data
     * @param publicKey the public key
     * @return String
     */
    public static String decryptByPublicKey(String data, String publicKey) {
        return new String(decryptByPublicKeyWithByte(data, publicKey));
    }

    /**
     * 用公钥解密
     *
     * @param data      the data
     * @param publicKey the public key
     * @return byte [ ]
     */
    public static byte[] decryptByPublicKeyWithByte(String data, String publicKey) {
        int keySize = getKeySizeFromPublicKey(publicKey);
        int encryptBlockSize = getEncryptBlockSize(keySize);
        final byte[] bytes = decodeBase64(data);
        final byte[][] splitBytes = split(bytes, encryptBlockSize);
        byte[][] decryptBytes = new byte[splitBytes.length][];
        for (int i = 0; i < splitBytes.length; i++) {
            decryptBytes[i] = decryptByPublicKey(splitBytes[i], publicKey);
        }
        return merge(decryptBytes);
    }

    /**
     * 用公钥解密
     *
     * @param data      the data
     * @param publicKey the public key
     * @return byte [ ]
     */
    private static byte[] decryptByPublicKey(byte[] data, String publicKey) {
        try {
            // 对密钥解密
            byte[] keyBytes = decodeBase64(publicKey);
            // 取得公钥
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            Key pubKey = keyFactory.generatePublic(x509KeySpec);
            // 对数据解密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, pubKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new SonsureCommonsException("使用publicKey解密失败", e);
        }
    }

    /**
     * 用公钥加密
     *
     * @param data      the data
     * @param publicKey the public key
     * @return byte [ ]
     */
    public static String encryptByPublicKey(String data, String publicKey) {
        return encryptByPublicKey(data.getBytes(), publicKey);
    }

    /**
     * 用公钥加密
     *
     * @param data      the data
     * @param publicKey the public key
     * @return byte [ ]
     */
    public static String encryptByPublicKey(byte[] data, String publicKey) {
        int keySize = getKeySizeFromPublicKey(publicKey);
        int maxPlainBlockSize = getMaxPlainBlockSize(keySize);
        final byte[][] splitBytes = split(data, maxPlainBlockSize);
        byte[][] encryptBytes = new byte[splitBytes.length][];
        for (int i = 0; i < splitBytes.length; i++) {
            encryptBytes[i] = encryptByPublicKeyRsaByte(splitBytes[i], publicKey);
        }
        return encodeBase64(merge(encryptBytes));
    }

    /**
     * 用公钥加密
     *
     * @param data      the data
     * @param publicKey the public key
     * @return byte [ ]
     */
    private static byte[] encryptByPublicKeyRsaByte(byte[] data, String publicKey) {
        int keySize = getKeySizeFromPublicKey(publicKey);
        int maxPlainBlockSize = getMaxPlainBlockSize(keySize);
        if (data.length > maxPlainBlockSize) {
            throw new SonsureCommonsException("RSA加密最长允许" + maxPlainBlockSize + "个字节（密钥长度：" + keySize + "位）");
        }
        try {
            // 对密钥解密
            byte[] keyBytes = decodeBase64(publicKey);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            Key pubKey = keyFactory.generatePublic(x509KeySpec);
            // 对数据加密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new SonsureCommonsException("使用publicKey加密失败", e);
        }
    }

    /**
     * 用私钥解密
     *
     * @param data       the data
     * @param privateKey the private key
     * @return String string
     */
    public static String decryptByPrivateKey(String data, String privateKey) {
        return new String(decryptByPrivateKeyWithByte(data, privateKey));
    }

    /**
     * 用私钥解密
     *
     * @param data       the data
     * @param privateKey the private key
     * @return byte [ ]
     */
    public static byte[] decryptByPrivateKeyWithByte(String data, String privateKey) {
        int keySize = getKeySizeFromPrivateKey(privateKey);
        int encryptBlockSize = getEncryptBlockSize(keySize);
        final byte[] bytes = decodeBase64(data);
        final byte[][] splitBytes = split(bytes, encryptBlockSize);
        byte[][] decryptBytes = new byte[splitBytes.length][];
        for (int i = 0; i < splitBytes.length; i++) {
            decryptBytes[i] = decryptByPrivateKey(splitBytes[i], privateKey);
        }
        return merge(decryptBytes);
    }

    /**
     * 用私钥解密
     *
     * @param data       the data
     * @param privateKey the private key
     * @return byte [ ]
     */
    private static byte[] decryptByPrivateKey(byte[] data, String privateKey) {
        try {
            // 对密钥解密
            byte[] keyBytes = decodeBase64(privateKey);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            Key priKey = keyFactory.generatePrivate(pkcs8KeySpec);
            // 对数据解密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new SonsureCommonsException("使用privateKey解密失败", e);
        }
    }

    /**
     * 拆分byte数组为几个等份（最后一份可能小于len）
     *
     * @param array 数组
     * @param len   每个小节的长度
     * @return 拆分后的数组 byte [ ] [ ]
     */
    private static byte[][] split(byte[] array, int len) {
        int x = array.length / len;
        int y = array.length % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        byte[][] arrays = new byte[x + z][];
        byte[] arr;
        for (int i = 0; i < x + z; i++) {
            if (i == x + z - 1 && y != 0) {
                arr = new byte[y];
                System.arraycopy(array, i * len, arr, 0, y);
            } else {
                arr = new byte[len];
                System.arraycopy(array, i * len, arr, 0, len);
            }
            arrays[i] = arr;
        }
        return arrays;
    }


    /**
     * Merge byte [ ].
     *
     * @param arrays the arrays
     * @return the byte [ ]
     */
    private static byte[] merge(byte[][] arrays) {
        int length = 0;
        for (byte[] array : arrays) {
            if (null != array) {
                length += array.length;
            }
        }
        byte[] result = new byte[length];

        length = 0;
        for (byte[] array : arrays) {
            if (null != array) {
                System.arraycopy(array, 0, result, length, array.length);
                length += array.length;
            }
        }
        return result;
    }

    public static byte[] decodeBase64(String data) {
        return Base64.getDecoder().decode(data);
    }

    public static String encodeBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

}

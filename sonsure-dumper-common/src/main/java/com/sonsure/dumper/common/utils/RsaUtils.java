package com.sonsure.dumper.common.utils;

import com.sonsure.dumper.common.exception.SonsureCommonsException;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author liyd
 */
public class RsaUtils {

    public static final String ALGORITHM_RSA = "RSA";

    /**
     * RSA加密最长117字节
     */
    public static final int RSA_MAX_BYTES_LENGTH = 117;

    /**
     * RSA加密后固定长度
     */
    public static final int RSA_ENCRYPT_BYTES_LENGTH = 128;

    /**
     * 创建密钥
     *
     * @return string [0]=publicKey [1]=privateKey
     */
    public static String[] createKeys() {
        return createKeys(1024);
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
        return createKeysWithByte(1024);
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
        final byte[][] splitBytes = split(data, RSA_MAX_BYTES_LENGTH);
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

        if (data.length > RSA_MAX_BYTES_LENGTH) {
            throw new SonsureCommonsException("RSA加密最长允许117个字节");
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
        final byte[] bytes = decodeBase64(data);
        final byte[][] splitBytes = split(bytes, RSA_ENCRYPT_BYTES_LENGTH);
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
        final byte[][] splitBytes = split(data, RSA_MAX_BYTES_LENGTH);
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

        if (data.length > RSA_MAX_BYTES_LENGTH) {
            throw new SonsureCommonsException("RSA加密最长允许117个字节");
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
        final byte[] bytes = decodeBase64(data);
        final byte[][] splitBytes = split(bytes, RSA_ENCRYPT_BYTES_LENGTH);
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

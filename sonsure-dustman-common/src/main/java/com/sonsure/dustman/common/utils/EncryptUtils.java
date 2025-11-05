/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.common.utils;


import com.sonsure.dustman.common.exception.SonsureCommonsException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

/**
 * @author liyd
 * @since 7/2/14
 */
public class EncryptUtils {

    private static final Pattern BCRYPT_PATTERN = Pattern
            .compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");

    /**
     * 对字符串md5加密
     *
     * @param str 加密字符串
     * @return md5 md 5
     */
    public static String getMD5(String str) {
        return getMD5(str, null, 1);
    }

    /**
     * 对字符串md5加密
     *
     * @param str  加密字符串
     * @param salt 盐
     * @return md5 md 5
     */
    public static String getMD5(String str, String salt) {
        return getMD5(str, salt, 1);
    }

    /**
     * 对字符串md5加密
     *
     * @param str            加密字符串
     * @param hashIterations the hash iterations
     * @return md5 md 5
     */
    public static String getMD5(String str, int hashIterations) {
        return getMD5(str, null, hashIterations);
    }

    /**
     * 对字符串md5加密
     *
     * @param str            加密字符串
     * @param salt           盐
     * @param hashIterations 散列次数
     * @return md5
     */
    public static String getMD5(String str, String salt, int hashIterations) {

        return getMD5(Optional.ofNullable(str).map(String::getBytes).orElse(new byte[0]), salt, hashIterations);
    }

    /**
     * 计算内容md5值,可以是文件内容byte
     *
     * @param bytes 计算md5值内容
     * @return md5
     */
    public static String getMD5(byte[] bytes) {
        return getMD5(bytes, "", 1);
    }

    /**
     * 计算内容md5值
     * 可以是字符串byte，也可以是文件byte
     *
     * @param bytes          the bytes
     * @param salt           盐
     * @param hashIterations 散列次数
     * @return md5 md 5
     */
    public static String getMD5(byte[] bytes, String salt, int hashIterations) {

        try {

            // 生成一个MD5加密计算摘要
            // 注意：MD5 算法存在安全漏洞，仅用于非敏感场景（如文件校验和）
            @SuppressWarnings("java:S4790")
            MessageDigest md = MessageDigest.getInstance("MD5");

            if (StrUtils.isNotBlank(salt)) {
                md.update(salt.getBytes());
            }
            byte[] hashed = md.digest(bytes);

            for (int i = 0; i < hashIterations - 1; i++) {
                md.reset();
                hashed = md.digest(hashed);
            }
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            String md5 = new BigInteger(1, hashed).toString(16);
            //当数字以0开头时会去掉0，补齐
            if (md5.length() < 32) {
                md5 = String.format("%32s", md5).replace(' ', '0');
            }
            return md5;
        } catch (Exception e) {
            throw new SonsureCommonsException("MD5加密出现错误", e);
        }
    }

    /**
     * BCrypt加密（注意：使用 PBKDF2 算法，不能与 BCryptPasswordEncoder 互操作）
     *
     * @param str the str
     * @return string BCrypt格式的哈希值
     */
    public static String encodeBCrypt(String str) {
        return encodeBCrypt(str, 10, null);
    }

    /**
     * BCrypt加密（注意：使用 PBKDF2 算法，不能与 BCryptPasswordEncoder 互操作）
     *
     * @param str      the str
     * @param strength the strength
     * @return string BCrypt格式的哈希值
     */
    public static String encodeBCrypt(String str, int strength) {
        return encodeBCrypt(str, strength, null);
    }

    /**
     * BCrypt加密（使用JDK内置的PBKDF2WithHmacSHA256实现，功能类似BCrypt）
     * <p>
     * ⚠️ 重要：此实现使用 PBKDF2 而非真正的 BCrypt Blowfish 算法
     * 虽然格式兼容（$2a$10$...），但不能与 Spring BCryptPasswordEncoder 互操作
     *
     * @param charSequence the char sequence
     * @param strength     the strength (迭代次数，建议值 10-12)
     * @param random       the random
     * @return string 格式: $2a$10$... (仅格式兼容BCrypt)
     */
    public static String encodeBCrypt(CharSequence charSequence, int strength, SecureRandom random) {
        BCryptImpl bcrypt = new BCryptImpl(random);
        String salt = bcrypt.gensalt(strength > 0 ? strength : 10);
        return bcrypt.hashpw(charSequence.toString(), salt);
    }

    /**
     * 检查加密的字符串
     *
     * @param charSequence the char sequence
     * @param encodedStr   the encoded str
     * @return boolean
     */
    public static boolean matchesBCrypt(CharSequence charSequence, String encodedStr) {
        if (encodedStr == null || encodedStr.isEmpty()) {
            return false;
        }

        if (!BCRYPT_PATTERN.matcher(encodedStr).matches()) {
            return false;
        }

        BCryptImpl bcrypt = new BCryptImpl(null);
        return bcrypt.checkpw(charSequence.toString(), encodedStr);
    }

    /**
     * 对字符串进行SHA-256加密（推荐用于安全敏感场景）
     *
     * @param str 加密字符串
     * @return SHA-256哈希值
     */
    public static String getSHA256(String str) {
        return getSHA256(str, null, 1);
    }

    /**
     * 对字符串进行SHA-256加密
     *
     * @param str  加密字符串
     * @param salt 盐
     * @return SHA-256哈希值
     */
    public static String getSHA256(String str, String salt) {
        return getSHA256(str, salt, 1);
    }

    /**
     * 对字符串进行SHA-256加密
     *
     * @param str            加密字符串
     * @param hashIterations 散列次数
     * @return SHA-256哈希值
     */
    public static String getSHA256(String str, int hashIterations) {
        return getSHA256(str, null, hashIterations);
    }

    /**
     * 对字符串进行SHA-256加密
     *
     * @param str            加密字符串
     * @param salt           盐
     * @param hashIterations 散列次数
     * @return SHA-256哈希值
     */
    public static String getSHA256(String str, String salt, int hashIterations) {
        return getSHA256(Optional.ofNullable(str).map(String::getBytes).orElse(new byte[0]), salt, hashIterations);
    }

    /**
     * 计算内容SHA-256值,可以是文件内容byte
     *
     * @param bytes 计算SHA-256值内容
     * @return SHA-256哈希值
     */
    public static String getSHA256(byte[] bytes) {
        return getSHA256(bytes, "", 1);
    }

    /**
     * 计算内容SHA-256值
     * 可以是字符串byte，也可以是文件byte
     *
     * @param bytes          the bytes
     * @param salt           盐
     * @param hashIterations 散列次数
     * @return SHA-256哈希值
     */
    public static String getSHA256(byte[] bytes, String salt, int hashIterations) {
        try {
            // 生成一个SHA-256加密计算摘要
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            if (StrUtils.isNotBlank(salt)) {
                md.update(salt.getBytes());
            }
            byte[] hashed = md.digest(bytes);

            for (int i = 0; i < hashIterations - 1; i++) {
                md.reset();
                hashed = md.digest(hashed);
            }
            // digest()最后确定返回SHA-256 hash值，返回值为8为字符串。因为SHA-256 hash值是32位的hex值，实际上就是64位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            String sha256 = new BigInteger(1, hashed).toString(16);
            //当数字以0开头时会去掉0，补齐
            if (sha256.length() < 64) {
                sha256 = String.format("%64s", sha256).replace(' ', '0');
            }
            return sha256;
        } catch (Exception e) {
            throw new SonsureCommonsException("SHA-256加密出现错误", e);
        }
    }

    /**
     * 计算内容的CRC32校验和（推荐用于文件校验）
     * CRC32速度快，结果短（8位十六进制），非常适合文件完整性检查
     *
     * @param str 要计算校验和的字符串
     * @return CRC32校验和（8位十六进制字符串）
     */
    public static String getCRC32(String str) {
        return getCRC32(Optional.ofNullable(str).map(String::getBytes).orElse(new byte[0]));
    }

    /**
     * 计算内容的CRC32校验和
     *
     * @param bytes 要计算校验和的字节数组
     * @return CRC32校验和（8位十六进制字符串）
     */
    public static String getCRC32(byte[] bytes) {
        CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        long checksum = crc32.getValue();
        return String.format("%08x", checksum);
    }

    /**
     * 计算内容的CRC32校验和（带盐）
     *
     * @param str  要计算校验和的字符串
     * @param salt 盐
     * @return CRC32校验和（8位十六进制字符串）
     */
    public static String getCRC32(String str, String salt) {
        if (StrUtils.isNotBlank(salt)) {
            return getCRC32((str + salt).getBytes());
        }
        return getCRC32(str.getBytes());
    }

    /**
     * 纯Java实现的密码哈希算法（使用JDK内置功能）
     * 注意：使用 PBKDF2 算法而非真正的 BCrypt Blowfish
     * 生成的哈希格式与BCrypt兼容（$2a$10$...），但算法不同
     * <p>
     * ⚠️ 重要提示：
     * - 此实现使用 PBKDF2WithHmacSHA256
     * - 不能与 Spring BCryptPasswordEncoder 互相验证
     * - 如需兼容 BCryptPasswordEncoder，请使用 bcrypt-java 或 jBCrypt
     */
    private static class BCryptImpl {
        private static final String BCRYPT_SALT_PREFIX = "$2a$";
        private static final String BCRYPT_ENCODE_CHARS = "./ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        private static final int SALT_LENGTH = 16;
        private static final int KEY_LENGTH = 24;
        private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

        private final SecureRandom secureRandom;

        public BCryptImpl(SecureRandom secureRandom) {
            this.secureRandom = secureRandom != null ? secureRandom : new SecureRandom();
        }

        public String gensalt(int rounds) {
            if (rounds < 4 || rounds > 31) {
                throw new SonsureCommonsException("BCrypt rounds must be between 4 and 31");
            }

            StringBuilder salt = new StringBuilder(BCRYPT_SALT_PREFIX);
            salt.append(String.format("%02d", rounds));
            salt.append("$");

            byte[] randomBytes = new byte[SALT_LENGTH];
            secureRandom.nextBytes(randomBytes);

            String encoded = encodeBase64(randomBytes, SALT_LENGTH);
            salt.append(encoded);
            String result = salt.toString();
            return result;
        }

        public String hashpw(String password, String salt) {
            if (salt == null || salt.length() < 29) {
                throw new SonsureCommonsException("Invalid salt");
            }

            try {
                int rounds = Integer.parseInt(salt.substring(4, 6));
                String saltStr = salt.substring(7, 29);
                byte[] saltBytes = decodeBase64(saltStr);

                // 使用 PBKDF2 生成哈希
                PBEKeySpec spec = new PBEKeySpec(
                        password.toCharArray(),
                        saltBytes,
                        calculateIterations(rounds),
                        KEY_LENGTH * 8
                );

                SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
                byte[] hash = factory.generateSecret(spec).getEncoded();

                String encodedHash = encodeBase64(hash, KEY_LENGTH);
                // salt是29字符，encodedHash是31字符，总共60字符
                return salt + encodedHash;
            } catch (Exception e) {
                throw new SonsureCommonsException("Password hashing failed", e);
            }
        }

        public boolean checkpw(String password, String hashed) {
            try {
                if (hashed == null || hashed.length() != 60) {
                    return false;
                }
                String salt = hashed.substring(0, 29);
                String computedHash = hashpw(password, salt);
                return computedHash.equals(hashed);
            } catch (Exception e) {
                return false;
            }
        }

        /**
         * 将 BCrypt 的 rounds 转换为 PBKDF2 的 iterations
         */
        private int calculateIterations(int rounds) {
            // BCrypt 使用 2^rounds 次迭代
            // 为了兼容性，我们使用类似的迭代次数
            return Math.max(1000, 1 << Math.max(rounds, 4));
        }

        private String encodeBase64(byte[] bytes, int length) {
            StringBuilder result = new StringBuilder();

            // BCrypt uses modified Base64 encoding
            // 16 bytes -> 22 chars
            // 24 bytes -> 31 chars (only encode 23 bytes to 30 chars + 1 char from last byte)

            if (length == 16) {
                // 16字节编码为22字符：前15字节=5组=20字符 + 最后1字节=2字符
                for (int i = 0; i < 15; i += 3) {
                    int v = ((bytes[i] & 0xFF) << 16)
                            | ((bytes[i + 1] & 0xFF) << 8)
                            | (bytes[i + 2] & 0xFF);

                    result.append(BCRYPT_ENCODE_CHARS.charAt((v >>> 18) & 0x3F));
                    result.append(BCRYPT_ENCODE_CHARS.charAt((v >>> 12) & 0x3F));
                    result.append(BCRYPT_ENCODE_CHARS.charAt((v >>> 6) & 0x3F));
                    result.append(BCRYPT_ENCODE_CHARS.charAt(v & 0x3F));
                }
                // 最后1字节：输出2个字符
                int v = (bytes[15] & 0xFF) << 16;
                result.append(BCRYPT_ENCODE_CHARS.charAt((v >>> 18) & 0x3F));
                result.append(BCRYPT_ENCODE_CHARS.charAt((v >>> 12) & 0x3F));
            } else if (length == 24) {
                // 24字节编码为31字符
                // 前21字节：7组 * 4 = 28字符
                for (int i = 0; i < 21; i += 3) {
                    int v = ((bytes[i] & 0xFF) << 16)
                            | ((bytes[i + 1] & 0xFF) << 8)
                            | (bytes[i + 2] & 0xFF);

                    result.append(BCRYPT_ENCODE_CHARS.charAt((v >>> 18) & 0x3F));
                    result.append(BCRYPT_ENCODE_CHARS.charAt((v >>> 12) & 0x3F));
                    result.append(BCRYPT_ENCODE_CHARS.charAt((v >>> 6) & 0x3F));
                    result.append(BCRYPT_ENCODE_CHARS.charAt(v & 0x3F));
                }
                // 处理字节21, 22: 2字节 -> 3个6位字符，得到3字符
                int v2 = ((bytes[21] & 0xFF) << 16) | ((bytes[22] & 0xFF) << 8);
                result.append(BCRYPT_ENCODE_CHARS.charAt((v2 >>> 18) & 0x3F));
                result.append(BCRYPT_ENCODE_CHARS.charAt((v2 >>> 12) & 0x3F));
                result.append(BCRYPT_ENCODE_CHARS.charAt((v2 >>> 6) & 0x3F));
                // 总共: 28 + 3 = 31字符
            }

            return result.toString();
        }

        private byte[] decodeBase64(String str) {
            byte[] result = new byte[SALT_LENGTH];
            int out = 0;

            // Decode 22 chars to 16 bytes
            if (str.length() == 22) {
                // 前20字符解码为15字节
                for (int i = 0; i < 20 && out < 15; i += 4) {
                    int b0 = BCRYPT_ENCODE_CHARS.indexOf(str.charAt(i));
                    int b1 = BCRYPT_ENCODE_CHARS.indexOf(str.charAt(i + 1));
                    int b2 = BCRYPT_ENCODE_CHARS.indexOf(str.charAt(i + 2));
                    int b3 = BCRYPT_ENCODE_CHARS.indexOf(str.charAt(i + 3));

                    int combined = (b0 << 18) | (b1 << 12) | (b2 << 6) | b3;
                    result[out++] = (byte) (combined >>> 16);
                    result[out++] = (byte) ((combined >>> 8) & 0xFF);
                    result[out++] = (byte) (combined & 0xFF);
                }
                // 最后2字符解码为1字节
                int b0 = BCRYPT_ENCODE_CHARS.indexOf(str.charAt(20));
                int b1 = BCRYPT_ENCODE_CHARS.indexOf(str.charAt(21));
                int combined = (b0 << 18) | (b1 << 12);
                result[out] = (byte) (combined >>> 16);
            }

            return result;
        }
    }
}

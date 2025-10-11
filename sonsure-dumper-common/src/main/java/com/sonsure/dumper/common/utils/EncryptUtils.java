/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.common.utils;

import com.sonsure.dumper.common.exception.SonsureException;
import com.sonsure.dumper.common.spring.encrypt.BCrypt;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.regex.Pattern;

/**
 * Created by liyd on 7/2/14.
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

        return getMD5(str.getBytes(), salt, hashIterations);
    }

    /**
     * 计算内容md5值,可以是文件内容byte
     *
     * @param bytes 计算md5值内容
     * @return md5
     */
    public static String getMD5(byte[] bytes) {
        return getMD5(bytes, null, 1);
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
            
            MessageDigest md = MessageDigest.getInstance("MD5");

            if (StrUtils.isNotBlank(salt)) {
                md.update(salt.getBytes());
            }
            // 计算md5函数
//            md.update(str.getBytes());

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
            throw new SonsureException("MD5加密出现错误", e);
        }
    }


    /**
     * BCrypt加密
     *
     * @param str the str
     * @return string string
     */
    public static String encodeBCrypt(String str) {
        return encodeBCrypt(str, 10, null);
    }

    /**
     * BCrypt加密
     *
     * @param str      the str
     * @param strength the strength
     * @return string string
     */
    public static String encodeBCrypt(String str, int strength) {
        return encodeBCrypt(str, strength, null);
    }

    /**
     * BCrypt加密
     *
     * @param charSequence the char sequence
     * @param strength     the strength
     * @param random       the random
     * @return string
     */
    public static String encodeBCrypt(CharSequence charSequence, int strength, SecureRandom random) {
        String salt;
        if (strength > 0) {
            if (random != null) {
                salt = BCrypt.gensalt(strength, random);
            } else {
                salt = BCrypt.gensalt(strength);
            }
        } else {
            salt = BCrypt.gensalt();
        }
        return BCrypt.hashpw(charSequence.toString(), salt);
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

        return BCrypt.checkpw(charSequence.toString(), encodedStr);
    }

    /**
     * 使用 HMAC-SHA1 签名方法对对encryptText进行签名
     *
     * @param encryptText 被签名的字符串
     * @param encryptKey  密钥
     * @return byte [ ]
     */
    public static byte[] hmacSHA1Encrypt(String encryptText, String encryptKey) {
        try {
            byte[] data = encryptKey.getBytes();
            //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
            SecretKey secretKey = new SecretKeySpec(data, "HmacSHA1");
            //生成一个指定 Mac 算法 的 Mac 对象
            Mac mac = Mac.getInstance("HmacSHA1");
            //用给定密钥初始化 Mac 对象
            mac.init(secretKey);

            byte[] text = encryptText.getBytes();
            //完成 Mac 操作
            return mac.doFinal(text);
        } catch (Exception e) {
            throw new SonsureException("HmacSHA1加密出现错误", e);
        }
    }
}

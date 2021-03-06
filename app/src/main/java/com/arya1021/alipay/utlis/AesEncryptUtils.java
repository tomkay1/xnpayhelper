package com.arya1021.alipay.utlis;


import org.java_websocket.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class AesEncryptUtils {

    /**
     * 可配置到Constant中，并读取配置文件注入,16位,自己定义
     */
    private static final String KEY = "APP|Xniu@1021A16";


    /**
     * 参数分别代表 算法名称/加密模式/数据填充方式
     */
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

    /**
     * 加密
     * @param content 加密的字符串
     * @param encryptKey key值
     * @return
     * @throws Exception
     */
    public static String encrypt(String content, String encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));
        byte[] b = cipher.doFinal(content.getBytes("utf-8"));
        // 采用base64算法进行转码,避免出现中文乱码

        return Base64.encodeBytes(b);

    }

    /**
     * 解密
     * @param encryptStr 解密的字符串
     * @param decryptKey 解密的key值
     * @return
     * @throws Exception
     */
    public static String decrypt(String encryptStr, String decryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
        // 采用base64算法进行转码,避免出现中文乱码
        byte[] encryptBytes = Base64.decode(encryptStr);
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
        return new String(decryptBytes);
    }

    public static String encrypt(String content) throws Exception {
        return encrypt(content, KEY);
    }
    public static String decrypt(String encryptStr) throws Exception {
        return decrypt(encryptStr, KEY);
    }


    public static void main(String[] args) throws Exception {
        System.out.println(decrypt(""));
        String content= "你好啊秘密23123dfdfgsdfsdf*&)*^^%^&*)*jdfsd8729348273467867324624=+1827313";
        System.out.println("加密前：" + content);

        String encrypt = encrypt(content);
        System.out.println("加密后：" + encrypt);

        long start = System.currentTimeMillis();
        String decrypt = decrypt(encrypt);
        System.out.println("解密后：" + decrypt);
        System.out.println(System.currentTimeMillis() - start);
    }
}


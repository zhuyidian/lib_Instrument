package com.dunn.logger;

import java.security.MessageDigest;
import java.util.Random;

public class Tools {
    // 0123456789
    public static final char[] NUMBERS = new char[] { 48, 49, 50, 51, 52, 53,
            54, 55, 56, 57 };

    /**
     * 获取char[]内的随机数
     * @param chars 随机的数据源
     * @param length 需要最终长度
     * @return
     */
    public static String getRandom(char[] chars, int length) {
        if(length > 0 && chars != null && chars.length != 0){
            StringBuilder str = new StringBuilder(length);
            Random random = new Random();
            for (int i = 0; i < length; i++) {
                str.append(chars[random.nextInt(chars.length)]);
            }
            return str.toString();
        }
        return null;
    }

    /**
     * 获取请求加密字符串    test+当前时间+随机数
     * @param rNumber 随机数
     * @return time 当前时间
     */
    public static String getRequestEncryp(long time, String rNumber){
        return MD5("zP58638rNJJnZe3Z" + time + rNumber).toLowerCase();
    }

    public final static String MD5(String s) {
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            return  toHexString(md).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 如需要小写则把ABCDEF改成小写,或结果直接转换
    private static final char HEX_DIGITS[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    public static String toHexString(byte[] bData) {
        StringBuilder sb = new StringBuilder(bData.length * 2);
        for (int i = 0; i < bData.length; i++) {
            sb.append(HEX_DIGITS[(bData[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[bData[i] & 0x0f]);
        }
        return sb.toString();
    }
}

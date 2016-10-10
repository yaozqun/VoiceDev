package com.grgbanking.baselib.util;

import java.util.Random;

/**
 * 字符串操作工具类
 */
public final class StringUtil {

    private static final int RANDOM_COUNT = 10;

    private StringUtil() {
    }

    public static int toInt(String srcStr) {
        return toInt(srcStr, 0);
    }

    public static int toInt(String srcStr, int defaultValue) {
        try {
            return Integer.parseInt(srcStr);
        } catch (Exception e) {
        }
        return defaultValue;
    }

    /**
     * 是否为null或空字符串
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(CharSequence str) {
        return null == str || str.length() == 0;
    }

    /**
     * 生成N位随机数
     *
     * @param length
     * @return
     */
    public static String getRandomNumbers(int length) {
        if (length <= 0) {
            return "";
        }

        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(RANDOM_COUNT));
        }
        return sb.toString();
    }

    /**
     * 转成大写
     *
     * @param str
     * @return
     */
    public static String toUpperString(String str) {
        StringBuilder sb = new StringBuilder("");
        if (str != null) {
            for (char c : str.toCharArray()) {
                sb.append(Character.toUpperCase(c));
            }
        }
        return sb.toString();
    }

    /**
     * 转成小写
     *
     * @param str
     * @return
     */
    public static String toLowerString(String str) {
        StringBuilder sb = new StringBuilder("");
        if (str != null) {
            for (char c : str.toCharArray()) {
                sb.append(Character.toLowerCase(c));
            }
        }
        return sb.toString();
    }

    /**
     * 判断扫描到的标签是否为我们自己的标签
     *
     * @param dataStr
     * @return
     */
    public static boolean isOurBox(String dataStr) {
        // TODO Auto-generated method stub
        boolean isOurBox = false;
        if (!StringUtil.isEmpty(dataStr) && dataStr.length() > 0 && dataStr.charAt(0) == 'C') {
            isOurBox = true;
        }
        return isOurBox;
    }

    /**
     * String 转long
     *
     * @param str
     * @return
     */
    public static long parseLong(String str) {
        if (isEmpty(str)) {
            return 0;
        } else {
            try {
                return Long.parseLong(str);
            } catch (Exception e) {
                return 0;
            }

        }
    }

    /**
     * String 转long
     *
     * @param str
     * @return
     */
    public static String filterNull(String str) {
        if (isEmpty(str)) {
            return "-";
        }
        return str;
    }

    /**
     * @param value
     * @param strs
     * @return
     * @Title: isContain
     * @Description: 判断数组是否包含某个字符串
     */
    public static boolean isContain(String value, String[] strs) {
        if (strs != null && value != null) {
            for (String str : strs) {
                if (value.equals(str)) {
                    return true;
                }

            }
        }
        return false;
    }
}

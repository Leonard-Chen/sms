package com.gdut.sms.common.utils;

import java.util.Random;

/**
 * 随机字符串生成工具
 * 支持指定种子，可生成任意长度的数字、字母、特殊字符及混合字符串
 *
 * @author ckx
 */
public class RandomUUID {

    private RandomUUID() {
    }

    //字符集
    private static final String DIGITS = "0123456789";
    private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LETTERS = LOWER_CASE + UPPER_CASE;
    private static final String ALPHA_NUMERIC = LETTERS + DIGITS;
    private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;:,.<>?/";
    private static final String ALL_CHARS = ALPHA_NUMERIC + SPECIAL_CHARS;

    public enum CharType {
        DIGIT,
        LOWERCASE,
        UPPERCASE,
        LETTER,
        ALPHANUMERIC,
        ALLCHARS
    }

    /**
     * 生成随机字符串，默认使用系统时间作为种子
     *
     * @param length 字符串长度
     * @param type   包含的字符类型
     * @return 随机字符串
     */
    public static String generate(int length, CharType type) {
        return generate(length, type, System.currentTimeMillis());
    }

    /**
     * 使用指定种子生成随机字符串
     *
     * @param length 字符串长度
     * @param type   包含的字符类型
     * @param seed   随机种子
     * @return 随机字符串
     */
    public static String generate(int length, CharType type, long seed) {
        if (length <= 0) {
            throw new IllegalArgumentException("字符串长度必需大于0");
        }

        String chars = getChars(type);
        Random random = new Random(seed);
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        return sb.toString();
    }

    /**
     * 根据枚举获取对应字符集合
     */
    private static String getChars(CharType type) {
        switch (type) {
            case DIGIT:
                return DIGITS;
            case LOWERCASE:
                return LOWER_CASE;
            case UPPERCASE:
                return UPPER_CASE;
            case LETTER:
                return LETTERS;
            case ALPHANUMERIC:
                return ALPHA_NUMERIC;
            case ALLCHARS:
                return ALL_CHARS;
            default:
                throw new IllegalArgumentException("不支持的字符类型: " + type);
        }
    }
}

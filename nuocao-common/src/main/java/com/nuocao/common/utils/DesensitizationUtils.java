package com.nuocao.common.utils;

import java.util.regex.Pattern;

/**
 * 字符串脱敏工具类
 */
public class DesensitizationUtils {

    // 常用正则模式预编译，提高性能
    private static final Pattern CHINESE_NAME_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("^[1-9]\\d{5}(?:18|19|20)\\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern BANK_CARD_PATTERN = Pattern.compile("^[1-9]\\d{9,19}$");

    /**
     * 通用脱敏方法
     *
     * @param str          原始字符串
     * @param startInclude 开始位置(包含，从0开始计数)
     * @param endExclude   结束位置(不包含)
     * @param maskChar     脱敏字符
     * @return 脱敏后的字符串
     */
    public static String desensitize(String str, int startInclude, int endExclude, char maskChar) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        int length = str.length();

        // 参数校验和调整
        startInclude = Math.max(0, startInclude);
        endExclude = Math.min(length, endExclude);

        if (startInclude >= endExclude) {
            return str; // 无需脱敏
        }

        // 构建脱敏后的字符串
        StringBuilder sb = new StringBuilder(str);
        for (int i = startInclude; i < endExclude; i++) {
            sb.setCharAt(i, maskChar);
        }
        return sb.toString();
    }

    /**
     * 通用脱敏方法（使用默认脱敏字符*）
     */
    public static String desensitize(String str, int startInclude, int endExclude) {
        return desensitize(str, startInclude, endExclude, '*');
    }

    /**
     * 手机号脱敏（显示前3位和后4位）
     * 例如：13800138000 -> 138****8000
     */
    public static String desensitizePhone(String phone) {
        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            return phone; // 不是手机号格式，返回原值
        }
        return desensitize(phone, 3, 7, '*');
    }

    /**
     * 身份证号脱敏（显示前6位和后4位）
     * 例如：110101199001011234 -> 110101********1234
     */
    public static String desensitizeIdCard(String idCard) {
        if (idCard == null || !ID_CARD_PATTERN.matcher(idCard).matches()) {
            return idCard;
        }
        int length = idCard.length();
        return desensitize(idCard, 6, length - 4, '*');
    }

    /**
     * 中文姓名脱敏
     * 2字姓名：张*    -> 张*
     * 3字姓名：张*三  -> 张*三
     * 4字及以上姓名：张**三  -> 张**三
     */
    public static String desensitizeChineseName(String name) {
        if (name == null || !CHINESE_NAME_PATTERN.matcher(name).matches()) {
            return name;
        }

        int length = name.length();
        if (length == 2) {
            return desensitize(name, 1, 2, '*'); // 第二个字脱敏
        } else {
            return desensitize(name, 1, length - 1, '*'); // 中间的字脱敏
        }
    }

    /**
     * 邮箱脱敏
     * 例如：example@domain.com -> e*****e@domain.com
     */
    public static String desensitizeEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            return email;
        }

        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email;
        }

        // 保留第一位和@符号前的一位
        String prefix = email.substring(0, atIndex);
        String suffix = email.substring(atIndex);

        if (prefix.length() <= 2) {
            return desensitize(prefix, 1, prefix.length(), '*') + suffix;
        } else {
            return desensitize(prefix, 1, prefix.length() - 1, '*') + suffix;
        }
    }

    /**
     * 银行卡号脱敏（显示前6位和后4位）
     * 例如：6222021234567890 -> 622202******7890
     */
    public static String desensitizeBankCard(String bankCard) {
        if (bankCard == null || !BANK_CARD_PATTERN.matcher(bankCard).matches()) {
            return bankCard;
        }
        int length = bankCard.length();
        return desensitize(bankCard, 6, length - 4, '*');
    }

    /**
     * 自定义规则的脱敏（完全符合您的要求）
     *
     * @param str 原始字符串
     * @param start 开始位置（从1开始计数，符合日常习惯）
     * @param end 结束位置（包含）
     * @param maskChar 脱敏字符
     * @return 脱敏后的字符串
     */
    public static String customDesensitize(String str, int start, int end, char maskChar) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        // 转换为从0开始的索引
        int startInclude = Math.max(0, start - 1);
        int endExclude = Math.min(str.length(), end);

        return desensitize(str, startInclude, endExclude, maskChar);
    }

    /**
     * 自定义规则的脱敏（使用默认脱敏字符*）
     */
    public static String customDesensitize(String str, int start, int end) {
        return customDesensitize(str, start, end, '*');
    }
}
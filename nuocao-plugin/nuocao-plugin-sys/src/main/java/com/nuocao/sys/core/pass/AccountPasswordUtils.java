package com.nuocao.sys.core.pass;

import cn.hutool.core.convert.Convert;
import com.alibaba.excel.util.StringUtils;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.utils.NumberUtils;

import java.security.SecureRandom;

/**
 * 密码工具类
 */
public class AccountPasswordUtils {

    private final static String DEFAULT_PASSWORD="123456";
    // 定义字符池
    private static final String ALPHA_CAPS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPHA = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMERIC = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*_=+-/";
    // 组合所有字符
    private static final String PASSWORD_RANDOM_DATA = ALPHA_CAPS + ALPHA + NUMERIC + SPECIAL_CHARS;

    private static SecureRandom random = new SecureRandom();
    /**
     * 获取默认密码
     **/
    public static String getDefaultPassword() {
        CommonExceptionI18n.notBlank(DEFAULT_PASSWORD,"Default password not set");
        return DEFAULT_PASSWORD;
    }

    /**
     * 生成随机密码
     * @param length
     * @return
     */
    public static String genPassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            // 从 DATA 字符串中随机选择一个索引
            int index = random.nextInt(PASSWORD_RANDOM_DATA.length());
            sb.append(PASSWORD_RANDOM_DATA.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 校验密码强度
     **/
    public static void validNewPassword(Long userId, String account, String name, String phone, String email, String newPassword) {
        validPasswordComplexity(newPassword);
        //如果没输入用户ID，则不处理
        if(!NumberUtils.isGtZero(userId)){
            return;
        }
        //可以扩展密码历史记录登

    }

    public static void validNewPassword(String newPassword){
        validPasswordComplexity(newPassword);
    }




    /**
     * 验证密码复杂度
     * @param password
     */
    private static void validPasswordComplexity(String password){
        if(StringUtils.isEmpty(password)){
            throw new CommonExceptionI18n("sys.password.notEmpty");
        }
        // 密码最小长度
        Integer passwordMinLength =  6;
        if(NumberUtils.isGtZero(passwordMinLength)){
            Integer minLengthInt = Convert.toInt(passwordMinLength);
            if(password.length() < minLengthInt){
                throw new CommonExceptionI18n("sys.password.length.min",passwordMinLength);
            }
        }
        // 密码最大长度
        Integer passwordMaxLength = 25;
        if(NumberUtils.isGtZero(passwordMaxLength)){
            Integer maxLengthInt = Convert.toInt(passwordMaxLength);
            if(password.length() > maxLengthInt){
                throw new CommonExceptionI18n("sys.password.length.max",passwordMinLength);
            }
        }
        // 密码复杂度
        String passwordComplexity=PasswordComplexityEnum.REG1.getValue();
        if(StringUtils.isNotBlank(passwordComplexity)){
            // 据复杂度配置判断
            boolean notLimit = PasswordComplexityEnum.REG0.getValue().equals(passwordComplexity);
            if(!notLimit){
                //得到验证配置枚举
                PasswordComplexityEnum complexityEnum = PasswordComplexityEnum.of(passwordComplexity);
                //如果有配置则验证强度
                if(complexityEnum!=null){
                    if(!password.matches(complexityEnum.getReg())) {
                        throw new CommonExceptionI18n(complexityEnum.getI18nKey());
                    }
                }
            }
        }
    }

    /**
     * 判断字符串是否存在N个连续相同的字符
     **/
    private static boolean hasConsecutiveChars(String str, int n) {
        // 处理无效输入：字符串为空、n非正、或字符串长度不足
        if (str == null || n <= 0 || str.length() < n) {
            return false;
        }
        // 特殊情况：n=1时只要字符串非空即存在连续1个字符
        if (n == 1) {
            return true;
        }

        int maxStartIndex = str.length() - n;
        for (int i = 0; i <= maxStartIndex; i++) {
            char currentChar = str.charAt(i);
            boolean isConsecutive = true;
            // 检查后续n-1个字符是否相同
            for (int j = 1; j < n; j++) {
                if (str.charAt(i + j) != currentChar) {
                    isConsecutive = false;
                    break;
                }
            }
            if (isConsecutive) {
                return true;
            }
        }
        return false;
    }



}

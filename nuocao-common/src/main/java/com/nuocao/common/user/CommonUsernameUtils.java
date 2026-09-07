package com.nuocao.common.user;

import cn.hutool.core.util.StrUtil;
import com.nuocao.common.exception.CommonException;

public class CommonUsernameUtils {
    /**
     * 用户名正则表达式：字母开头，数字、字母、下划线组合，长度4-20位A-Z
     */
    private static final String USERNAME_REGEX = "^[a-z][a-z0-9_]{3,19}$";

    /**
     * 判断是否用户名
     *
     **/
    public static boolean isUsername(String username) {
        if (StrUtil.isBlank(username)) {
            return false;
        }
        return username.matches(USERNAME_REGEX);
    }

    /**
     * 不是用户名
     *
     **/
    public static boolean notUsername(String username) {
        return !isUsername(username);
    }


    /**
     * 校验邮箱格式
     **/
    public static void validUsername(String username) {
        if(notUsername(username)) {
            CommonException.throwException("common.username.error", username);
        }
    }
}

package com.nuocao.sys.modular.user.enums;

import lombok.Getter;

/**
 * 系统修改密码验证方式枚举
 **/
@Getter
public enum SysUpdatePasswordValidTypeEnum {

    /** 旧密码 */
    OLD("OLD"),

    /** 手机号 */
    PHONE("PHONE"),

    /** 邮箱 */
    EMAIL("EMAIL");

    private final String value;

    SysUpdatePasswordValidTypeEnum(String value) {
        this.value = value;
    }
}

package com.nuocao.sys.modular.account.enums;

import lombok.Getter;

/**
 * 系统修改密码验证方式枚举
 **/
@Getter
public enum AccountUpdatePasswordMethodEnum {

    /** 旧密码 */
    OLD("old"),

    /** 手机号 */
    PHONE("phone");

    private final String value;

    AccountUpdatePasswordMethodEnum(String value) {
        this.value = value;
    }
}

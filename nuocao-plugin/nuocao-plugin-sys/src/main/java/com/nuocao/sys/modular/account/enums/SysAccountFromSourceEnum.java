package com.nuocao.sys.modular.account.enums;

import lombok.Getter;

/**
 * 账户来源字典
 */
@Getter
public enum SysAccountFromSourceEnum {
    /** 系统自建 */
    SYS_ADD("sys_add"),

    /** 用户注册 */
    USER_REG("user_reg"),
;

    private final String value;

    SysAccountFromSourceEnum(String value) {
        this.value = value;
    }
}

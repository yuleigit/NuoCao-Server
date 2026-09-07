package com.nuocao.sys.core.enums;

import lombok.Getter;

/**
 * 系统角色编码
 */
@Getter
public enum SysRoleCodeEnum {

    /** 租户超管账号角色 */
    SUPER_ADMIN("superAdmin", "超管");



    private final String value;

    private final String name;

    SysRoleCodeEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }
}

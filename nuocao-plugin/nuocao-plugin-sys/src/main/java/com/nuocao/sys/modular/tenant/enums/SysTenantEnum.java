package com.nuocao.sys.modular.tenant.enums;

import lombok.Getter;

/**
 * 客户机构枚举
 **/
@Getter
public enum SysTenantEnum {

    /** 测试 */
    TEST("TEST");

    private final String value;

    SysTenantEnum(String value) {
        this.value = value;
    }
}

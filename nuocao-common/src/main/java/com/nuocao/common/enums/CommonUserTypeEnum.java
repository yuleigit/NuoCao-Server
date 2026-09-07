package com.nuocao.common.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * 用户类型
 */
@Getter
public enum CommonUserTypeEnum implements StrArrayValuable {

    /** 管理员 */
    ADMIN("admin"),
    /** 账户 账户与admin通用，登录后再sys_user中能找到角色就可以作为管理员 */
    ACCOUNT("account"),

    /** 会员 */
    MEMBER("member");

    private final String value;
    private static final String[] ARRARS= Arrays.stream(CommonUserTypeEnum.values()).map(CommonUserTypeEnum::getValue).toArray(String[]::new);

    CommonUserTypeEnum(String value) {
        this.value = value;
    }

    @Override
    public String[] array() {
        return ARRARS;
    }
}

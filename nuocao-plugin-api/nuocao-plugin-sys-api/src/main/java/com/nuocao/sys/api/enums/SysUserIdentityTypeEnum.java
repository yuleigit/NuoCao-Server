package com.nuocao.sys.api.enums;

import lombok.Getter;

/**
 * 用户身份对照 类型
 */
@Getter
public enum SysUserIdentityTypeEnum {

    ROLE(1),
    POSITION(2);

    private final Integer value;

    SysUserIdentityTypeEnum(Integer value){
        this.value=value;
    }
}

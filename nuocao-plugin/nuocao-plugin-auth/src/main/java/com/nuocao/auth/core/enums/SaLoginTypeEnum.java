package com.nuocao.auth.core.enums;

import com.nuocao.common.enums.StrArrayValuable;
import lombok.Getter;

/**
 * sa-token登录类型
 */

@Getter
public enum SaLoginTypeEnum implements StrArrayValuable {

    //目前只支持管理平台登录，可自行扩展
    ADMIN("admin"),
    MEMBER("member")
    ;




    private final String value;

    SaLoginTypeEnum(String value){
        this.value=value;
    }

    @Override
    public String[] array() {
        return new String[0];
    }
}

package com.nuocao.auth.modular.login.enums;

import com.nuocao.common.enums.StrArrayValuable;
import lombok.Getter;

import java.util.Arrays;

/**
 * 手机号邮箱类型枚举
 **/
@Getter
public enum AuthPhoneOrEmailTypeEnum implements StrArrayValuable {

    /** 手机号 */
    PHONE("phone"),

    /** 邮箱 */
    EMAIL("email");

    private final String value;

    private static final String[] ARRARS = Arrays.stream(AuthPhoneOrEmailTypeEnum.values()).map(AuthPhoneOrEmailTypeEnum::getValue).toArray(String[]::new);

    AuthPhoneOrEmailTypeEnum(String value) {
        this.value = value;
    }


    @Override
    public String[] array() {
        return ARRARS;
    }
}

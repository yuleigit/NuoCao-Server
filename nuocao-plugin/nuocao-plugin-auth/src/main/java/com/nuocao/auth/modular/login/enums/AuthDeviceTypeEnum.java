package com.nuocao.auth.modular.login.enums;

import com.nuocao.common.enums.StrArrayValuable;
import com.nuocao.common.exception.CommonExceptionI18n;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 登录设备类型枚举
 **/
@Getter
public enum AuthDeviceTypeEnum implements StrArrayValuable {

    /**
     * PC端
     */
    PC("PC"),

    /**
     * 移动端
     */
    APP("APP"),

    /**
     * 小程序端
     */
    MINI("MINI");

    private final String value;

    private static final String[] ARRARS = Arrays.stream(AuthDeviceTypeEnum.values()).map(AuthDeviceTypeEnum::getValue).toArray(String[]::new);


    AuthDeviceTypeEnum(String value) {
        this.value = value;
    }

    public static void validate(String device){
        if(!List.of(ARRARS).contains(device)){
            throw new CommonExceptionI18n("auth.login.device.disable",device);
        }
    }


    @Override
    public String[] array() {
        return ARRARS;
    }
}

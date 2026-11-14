package com.nuocao.dev.core.enums;

import com.nuocao.common.enums.IntArrayValuable;
import lombok.Getter;

import java.util.Arrays;

/**
 * 2fa 登录验证
 */

@Getter
public enum NuoCaoMfaEnforcementEnum implements IntArrayValuable {

    //目前只支持管理平台登录，可自行扩展
    OFF(0), //关闭
    OPTIONAL(1), //可选
    REQUIRED(2) //必须绑定
    ;

    private static int[] ARRAYS = Arrays.stream(NuoCaoMfaEnforcementEnum.values()).mapToInt(NuoCaoMfaEnforcementEnum::getValue).toArray();



    private final Integer value;

    NuoCaoMfaEnforcementEnum(Integer value){
        this.value=value;
    }

    public static NuoCaoMfaEnforcementEnum ofValue(Integer value) {
        if (value == null) {
            return OFF;
        }
        for (NuoCaoMfaEnforcementEnum item : NuoCaoMfaEnforcementEnum.values()) {
            if (item.getValue().equals(value)) {
                return item;
            }
        }
        return OFF;
    }

    @Override
    public int[] array() {
        return ARRAYS;
    }
}

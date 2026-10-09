package com.nuocao.plugin.dev.api.enums.sms;

import com.nuocao.common.enums.IntArrayValuable;
import lombok.Getter;

import java.util.Arrays;

/**
 * 短信类型 枚举
 */
@Getter
public enum DevSmsTypeEnum implements IntArrayValuable {

    /**
     * 验证码
     */
    CODE(1),

    /**
     * 通知类
     */
    NOTIFY(2),

    /**
     * 营销类
     */
    Ad(3)

    ;

    private final Integer value;

    private static int[] ARRAYS= Arrays.stream(DevSmsTypeEnum.values()).mapToInt(DevSmsTypeEnum::getValue).toArray();

    DevSmsTypeEnum(Integer value) {
        this.value = value;
    }


    @Override
    public int[] array() {
        return ARRAYS;
    }
}

package com.nuocao.dev.modular.log.enums;

import com.nuocao.common.enums.StrArrayValuable;
import lombok.Getter;

import java.util.Arrays;

/**
 * 日志执行状态枚举
 **/
@Getter
public enum DevLogExeStatusEnum implements StrArrayValuable {

    /** 成功 */
    SUCCESS("success"),

    /** 失败 */
    FAIL("fail");

    private final String value;
    private static final String[] ARRARS= Arrays.stream(DevLogExeStatusEnum.values()).map(DevLogExeStatusEnum::getValue).toArray(String[]::new);

    DevLogExeStatusEnum(String value) {
        this.value = value;
    }

    @Override
    public String[] array() {
        return ARRARS;
    }
}

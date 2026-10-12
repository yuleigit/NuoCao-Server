package com.nuocao.dev.modular.sms.template.enums;

import com.nuocao.common.enums.IntArrayValuable;
import lombok.Getter;

import java.util.Arrays;

/**
 * 短信模板变量类型枚举
 *
 **/
@Getter
public enum DevSmsVariableTypeEnum implements IntArrayValuable {

    /** 操作日志 */
    STRING(1),

    /** 异常日志 */
    INTEGER(2);


    private final Integer value;

    private static final int[] ARRARS= Arrays.stream(DevSmsVariableTypeEnum.values()).mapToInt(DevSmsVariableTypeEnum::getValue).toArray();

    DevSmsVariableTypeEnum(Integer value) {
        this.value = value;
    }

    @Override
    public int[] array() {
        return ARRARS;
    }
}

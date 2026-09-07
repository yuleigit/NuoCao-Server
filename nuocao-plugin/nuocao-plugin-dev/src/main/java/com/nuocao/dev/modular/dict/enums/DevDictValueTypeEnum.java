package com.nuocao.dev.modular.dict.enums;

import com.nuocao.common.enums.StrArrayValuable;
import com.nuocao.common.exception.CommonException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;

/**
 * 字典值类型枚举
 */
@Getter
public enum DevDictValueTypeEnum implements StrArrayValuable {

    /**
     * 数字
     */
    NUMBER("n"),

    /**
     * 字符串
     */
    STRING("s");

    private final String value;

    private static String[] ARRAYS= Arrays.stream(DevDictValueTypeEnum.values()).map(DevDictValueTypeEnum::getValue).toArray(String[]::new);

    DevDictValueTypeEnum(String value) {
        this.value = value;
    }

    public static void validate(String value) {
        boolean flag = Set.of(ARRAYS).contains(value);
        if(!flag) {
            throw new CommonException("dict value type not exists：{}", value);
        }
    }

    @Override
    public String[] array() {
        return ARRAYS;
    }
}

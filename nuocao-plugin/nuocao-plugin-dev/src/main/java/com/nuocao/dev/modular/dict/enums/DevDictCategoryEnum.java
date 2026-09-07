package com.nuocao.dev.modular.dict.enums;

import com.nuocao.common.enums.StrArrayValuable;
import com.nuocao.common.exception.CommonException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

/**
 * 字典分类枚举
 */
@Getter
public enum DevDictCategoryEnum implements StrArrayValuable {

    /**
     * 系统字典，不可删除
     */
    SYS("sys"),

    /**
     * 业务字典，可自删除
     */
    biz("biz");

    private final String value;

    private static String[] ARRAYS= Arrays.stream(DevDictCategoryEnum.values()).map(DevDictCategoryEnum::getValue).toArray(String[]::new);

    DevDictCategoryEnum(String value) {
        this.value = value;
    }

    public static void validate(String value) {
        boolean flag = Set.of(ARRAYS).contains(value);
        if(!flag) {
            throw new CommonException("dict category not exists：{}", value);
        }
    }

    @Override
    public String[] array() {
        return ARRAYS;
    }
}

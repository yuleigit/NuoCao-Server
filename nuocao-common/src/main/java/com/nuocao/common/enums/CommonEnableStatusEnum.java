package com.nuocao.common.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 *是否可用公用字典
 **/
@Getter
public enum CommonEnableStatusEnum implements IntArrayValuable {

    DISABLE(0,"DISABLE", "禁用"),
    ENABLE(1,"ENABLE", "可用"),
    ;

    private static final int[] ARRAY= Arrays.stream(CommonEnableStatusEnum.values()).mapToInt(CommonEnableStatusEnum::getValue).toArray();

    private final Integer value;

    private final String code;

    private final String message;

    CommonEnableStatusEnum(Integer value,String code, String message) {
        this.value=value;
        this.code = code;
        this.message = message;
    }

    public static String getCodeByValue(Integer value){
        CommonEnableStatusEnum d = Arrays.stream(CommonEnableStatusEnum.values()).filter(o->o.getValue().equals(value)).findFirst().orElse(null);
        return d==null?"":d.getCode();
    }

    @Override
    public int[] array() {
        return ARRAY;
    }
}

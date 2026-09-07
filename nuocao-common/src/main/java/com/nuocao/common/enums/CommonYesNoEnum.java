package com.nuocao.common.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum CommonYesNoEnum implements IntArrayValuable {
    /** 是 */
    YES(1,"是"),

    /** 否 */
    NO(0,"否");

    private static final int[] ARRAY = Arrays.stream(CommonYesNoEnum.values()).mapToInt(CommonYesNoEnum::getValue).toArray();

    private final Integer value;
    private final String name;

    CommonYesNoEnum(Integer value,String name) {
        this.value =value;this.name=name;
    }

    public static boolean isYes(Integer value){
        return YES.value.equals(value);
    }


    @Override
    public int[] array() {
        return ARRAY;
    }
}

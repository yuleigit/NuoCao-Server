package com.nuocao.common.enums;

import lombok.Getter;

/**
 * 通用删除标志枚举
 *
 **/
@Getter
public enum CommonDeleteFlagEnum implements IntArrayValuable {

    /** 未删除 */
    NOT_DELETE(0,"未删除"),

    /** 已删除 */
    DELETED(1,"已删除");

    private final String name;

    private final Integer value;
    CommonDeleteFlagEnum(Integer value,String name){
        this.name=name;
        this.value=value;
    }

    @Override
    public int[] array() {
        return new int[]{0,1};
    }
}

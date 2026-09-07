package com.nuocao.gen.modular.table.enums;

import lombok.Getter;


/**
 * 文件生成方式
 */
@Getter
public enum GenTypeEnum {

    /** 压缩包 */
    ZIP("zip"),

    /** 项目内 */
    PRO("local");

    private final String value;

    GenTypeEnum(String value) {
        this.value = value;
    }
}

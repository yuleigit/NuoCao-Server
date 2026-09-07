package com.nuocao.gen.modular.table.enums;

import com.nuocao.common.enums.StrArrayValuable;
import lombok.Getter;

/**
 * java 变量类型
 */
@Getter
public enum GenJavaTypeEnum {

    /** Integer */
    Integer("Integer"),

    /** Long */
    Long("Long"),

    /** String */
    String("String"),

    /** Boolean */
    Boolean("Boolean"),

    /** Float */
    Float("Float"),

    /** Double */
    Double("Double"),

    /** Date */
    Date("Date"),

    /** Date */
    LocalDateTime("LocalDateTime"),

    /** BigDecimal */
    BigDecimal("BigDecimal");

    private final String value; //类型值


    GenJavaTypeEnum(String value) {
        this.value = value;
    }
}

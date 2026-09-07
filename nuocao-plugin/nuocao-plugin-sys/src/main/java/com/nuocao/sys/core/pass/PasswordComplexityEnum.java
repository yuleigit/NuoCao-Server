package com.nuocao.sys.core.pass;

import lombok.Getter;

import java.util.Arrays;

/**
 * 密码强度 类型
 */
@Getter
public enum PasswordComplexityEnum {
    /**
     * 无限制
     */
    REG0("reg0", "无限制", "sys.password.complexity.reg0",""),

    /**
     * 必须包含数字和字母
     */
    REG1("reg1", "密码必须包含数字和字母", "sys.password.complexity.reg1","^(?=.*[0-9])(?=.*[a-zA-Z]).+$"),

    /**
     * 必须包含数字和大写字母
     */
    REG2("reg2", "密码必须包含数字和大写字母", "sys.password.complexity.reg2","^(?=.*[0-9])(?=.*[A-Z]).+$"),

    /**
     * 必须包含数字、大写字母、小写字母和特殊字符
     */
    REG3("reg3", "密码必须包含数字、大写字母、小写字母和特殊字符", "sys.password.complexity.reg3","^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[^a-zA-Z0-9]).+$"),

    /**
     * 至少包含数字、字母和特殊字符中的两种
     */
    REG4("reg4", "密码至少包含数字、字母和特殊字符中的两种", "sys.password.complexity.reg4","^(?:(?=.*[0-9])(?=.*[a-zA-Z])|(?=.*[0-9])(?=.*[^a-zA-Z0-9])|(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9])).+$"),

    /**
     * 至少包含数字、大写字母、小写字母和特殊字符的三种
     */
    REG5("reg5", "密码至少包含数字、大写字母、小写字母和特殊字符的三种", "sys.password.complexity.reg5","^(?:(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])|(?=.*[0-9])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])|(?=.*[0-9])(?=.*[a-z])(?=.*[^a-zA-Z0-9])|(?=.*[A-Z])(?=.*[a-z])(?=.*[^a-zA-Z0-9])).+$");

    private final String value;

    private final String message;
    private final String i18nKey;
    private final String reg;
    PasswordComplexityEnum(String value, String message,String i18nKey,String reg) {
        this.value = value;
        this.message = message;
        this.i18nKey=i18nKey;
        this.reg=reg;
    }


    public static PasswordComplexityEnum of(String value){
        return Arrays.stream(PasswordComplexityEnum.values())
                .filter(e -> e.getValue().equalsIgnoreCase(value))
                .findFirst()
                .orElse(null); // 或者抛出 IllegalArgumentException
    }
}

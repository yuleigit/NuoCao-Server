package com.nuocao.dev.modular.config.enums;

import com.nuocao.common.enums.StrArrayValuable;
import com.nuocao.common.exception.CommonException;
import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 配置分组
 */
@Getter
public enum DevConfigGroupEnum implements StrArrayValuable {

    /**
     * 系统配置
     */
    SYS("sys"),

    /**
     * 账户注册配置
     */
    ACCOUNT("account"),
    /**
     * 文件-
     */
    FILE("file"),

    /**
     * 邮件
     */
    EMAIL("email"),

    /**
     * 短信
     */
    SMS("sms");

    private static String[] ARRAYS = Arrays.stream(DevConfigGroupEnum.values()).map(DevConfigGroupEnum::getValue).toArray(String[]::new);


    private final String value;

    DevConfigGroupEnum(String value) {
        this.value = value;
    }

    public static void validate(String value) {
        if(!Arrays.stream(ARRAYS).collect(Collectors.toList()).contains(value)) {
            throw new CommonException("不支持的配置分类：{}", value);
        }
    }

    @Override
    public String[] array() {
        return ARRAYS;
    }
}

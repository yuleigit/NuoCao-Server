package com.nuocao.dev.modular.log.enums;

import com.nuocao.common.enums.StrArrayValuable;
import lombok.Getter;

import java.util.Arrays;

/**
 * 日志分类枚举
 *
 **/
@Getter
public enum DevLogCategoryEnum implements StrArrayValuable {

    /** 操作日志 */
    OPERATE("operate"),

    /** 异常日志 */
    EXCEPTION("exception"),

    /** 登录日志 */
    LOGIN("login"),

    /** 登出日志 */
    LOGOUT("logout");

    private final String value;

    private static final String[] ARRARS= Arrays.stream(DevLogCategoryEnum.values()).map(DevLogCategoryEnum::getValue).toArray(String[]::new);

    DevLogCategoryEnum(String value) {
        this.value = value;
    }

    @Override
    public String[] array() {
        return ARRARS;
    }
}

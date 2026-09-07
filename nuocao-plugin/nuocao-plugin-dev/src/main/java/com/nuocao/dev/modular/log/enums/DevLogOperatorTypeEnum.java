package com.nuocao.dev.modular.log.enums;

import com.nuocao.common.enums.CommonUserTypeEnum;
import com.nuocao.common.enums.StrArrayValuable;
import lombok.Getter;

import java.util.Arrays;

/**
 * 日志操作人类型 枚举
 **/
@Getter
public enum DevLogOperatorTypeEnum implements StrArrayValuable {

    /** 管理员 */
    ADMIN(CommonUserTypeEnum.ADMIN.getValue()),
    ACCOUNT(CommonUserTypeEnum.ACCOUNT.getValue()),
    /** 会员 */
    MEMBER(CommonUserTypeEnum.MEMBER.getValue());

    private final String value;
    private static final String[] ARRARS = Arrays.stream(DevLogOperatorTypeEnum.values()).map(DevLogOperatorTypeEnum::getValue).toArray(String[]::new);

    DevLogOperatorTypeEnum(String value) {
        this.value = value;
    }

    @Override
    public String[] array() {
        return ARRARS;
    }
}

package com.nuocao.plugin.dev.api.enums.mfa;

import cn.hutool.core.util.ArrayUtil;
import com.nuocao.common.enums.IntArrayValuable;
import com.nuocao.common.enums.StrArrayValuable;
import lombok.Getter;

import java.util.Arrays;

/**
 * 文件存储引擎类型枚举
 **/
@Getter
public enum DevMfaStatusEnum implements IntArrayValuable {

    /** 绑定中 */
    PENDING(1),

    /** 当前激活 */
    ACTIVE(9),

    /** 已禁用 */
    DISABLED(4);

    private final Integer value;

    private static int[] ARRAYS = Arrays.stream(DevMfaStatusEnum.values()).mapToInt(DevMfaStatusEnum::getValue).toArray();

    DevMfaStatusEnum(Integer value) {
        this.value = value;
    }


    public static DevMfaStatusEnum ofValue(Integer value) {
        return ArrayUtil.firstMatch(o -> o.getValue().equals(value), DevMfaStatusEnum.values());
    }


    @Override
    public int[] array() {
        return ARRAYS;
    }
}

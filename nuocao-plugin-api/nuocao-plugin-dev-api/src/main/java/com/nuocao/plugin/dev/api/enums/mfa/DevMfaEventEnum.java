package com.nuocao.plugin.dev.api.enums.mfa;

import cn.hutool.core.util.ArrayUtil;
import com.nuocao.common.enums.StrArrayValuable;
import lombok.Getter;

import java.util.Arrays;

/**
 * 文件存储引擎类型枚举
 **/
@Getter
public enum DevMfaEventEnum implements StrArrayValuable {

    /** 绑定初始化 */
    BIND_INIT("bind_init"),
    /** 绑定确认 */
    BIND_CONFIRM("bind_confirm"),
    /** 绑定失败 */
    BIND_FAIL("bind_fail"),
    /**验证成功*/
    VERIFY_OK("verify_ok"),
    /**验证失败*/
    VERIFY_FAIL("verify_fail"),
    /**锁定*/
    LOCKED("locked"),
    /**解绑*/
    UNBIND("unbind"),
    /** 重置有效 */
    RECOVERY_USED("recovery_used"),
    /** 管理员重置失败次数 */
    ADMIN_RESET("admin_reset"),
    /** 管理员失效绑定 */
    ADMIN_DISABLE("admin_disable"),
    /** 管理员删除绑定 */
    ADMIN_DELETE("admin_delete")
    ;


    private final String value;

    private static String[] ARRAYS = Arrays.stream(DevMfaEventEnum.values()).map(DevMfaEventEnum::getValue).toArray(String[]::new);

    DevMfaEventEnum(String value) {
        this.value = value;
    }


    public static DevMfaEventEnum ofValue(String value) {
        return ArrayUtil.firstMatch(o -> o.getValue().equals(value), DevMfaEventEnum.values());
    }


    @Override
    public String[] array() {
        return ARRAYS;
    }
}

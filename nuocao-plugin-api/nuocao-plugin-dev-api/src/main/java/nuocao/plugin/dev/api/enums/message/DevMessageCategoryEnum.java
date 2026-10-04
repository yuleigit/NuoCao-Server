package nuocao.plugin.dev.api.enums.message;


import com.nuocao.common.enums.StrArrayValuable;
import lombok.Getter;

import java.util.Arrays;

/**
 * 短信发送通道 枚举
 **/
@Getter
public enum DevMessageCategoryEnum implements StrArrayValuable {

    /** 系统通知 */
    SYS("sys"),
    /** 审核信息 */
    AUDIT("audit"),
    /** 用户消息  */
    MSG("msg"),
;

    private final String value;

    private static String[] ARRAYS = Arrays.stream(DevMessageCategoryEnum.values()).map(DevMessageCategoryEnum::getValue).toArray(String[]::new);

    DevMessageCategoryEnum(String value) {
        this.value = value;
    }

    @Override
    public String[] array() {
        return ARRAYS;
    }
}

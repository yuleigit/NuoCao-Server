package nuocao.plugin.dev.api.enums.sms;


import com.nuocao.common.enums.StrArrayValuable;
import lombok.Getter;

import java.util.Arrays;

/**
 * 短信发送通道 枚举
 **/
@Getter
public enum SmsChannelEnum implements StrArrayValuable {

    /** 本地 */
    ALIYUN("aliyun"),

    /** 腾讯云  */
    TENCENT("tencent"),
;

    private final String value;

    private static String[] ARRAYS = Arrays.stream(SmsChannelEnum.values()).map(SmsChannelEnum::getValue).toArray(String[]::new);

    SmsChannelEnum(String value) {
        this.value = value;
    }

    @Override
    public String[] array() {
        return ARRAYS;
    }
}

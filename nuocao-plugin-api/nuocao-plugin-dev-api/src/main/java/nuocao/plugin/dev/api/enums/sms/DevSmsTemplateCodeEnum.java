package nuocao.plugin.dev.api.enums.sms;


import com.nuocao.common.enums.StrArrayValuable;
import lombok.Getter;

import java.util.Arrays;

/**
 * 短信模板code 枚举
 * 这样写是为了方便在各个模块调用短信模板
 * 因为如果单纯只在数据库中添加code，需要记忆每个code的值，最好同步写到这里
 **/
@Getter
public enum DevSmsTemplateCodeEnum implements StrArrayValuable {

    /** 账户注册短信验证码 */
    ACCOUNT_REG_CODE("account_reg_code"),

    /** 绑定手机号短信验证码  */
    BIND_PHONE_CODE("bind_phone_code"),
;

    private final String value;

    private static String[] ARRAYS = Arrays.stream(nuocao.plugin.dev.api.enums.file.DevFileEngineTypeEnum.values()).map(nuocao.plugin.dev.api.enums.file.DevFileEngineTypeEnum::getValue).toArray(String[]::new);

    DevSmsTemplateCodeEnum(String value) {
        this.value = value;
    }

    @Override
    public String[] array() {
        return ARRAYS;
    }
}

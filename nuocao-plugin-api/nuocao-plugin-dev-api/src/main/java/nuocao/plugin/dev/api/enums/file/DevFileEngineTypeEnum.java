package nuocao.plugin.dev.api.enums.file;

import cn.hutool.core.util.ArrayUtil;
import com.nuocao.common.enums.StrArrayValuable;
import lombok.Getter;

import java.util.Arrays;

/**
 * 文件存储引擎类型枚举
 **/
@Getter
public enum DevFileEngineTypeEnum implements StrArrayValuable {

    /** 本地 */
    LOCAL("local"),

    /** 阿里云 */
    ALIYUN("aliyun"),

    /** 腾讯云 */
    TENCENT("tencent"),

    /** MINIO */
    MINIO("minio");

    private final String value;

    private static String[] ARRAYS = Arrays.stream(DevFileEngineTypeEnum.values()).map(DevFileEngineTypeEnum::getValue).toArray(String[]::new);

    DevFileEngineTypeEnum(String value) {
        this.value = value;
    }


    public static DevFileEngineTypeEnum ofValue(String value) {
        return ArrayUtil.firstMatch(o -> o.getValue().equals(value), DevFileEngineTypeEnum.values());
    }


    @Override
    public String[] array() {
        return ARRAYS;
    }
}

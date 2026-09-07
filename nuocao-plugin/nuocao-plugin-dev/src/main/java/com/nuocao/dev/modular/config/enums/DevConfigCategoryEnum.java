package com.nuocao.dev.modular.config.enums;

import com.nuocao.common.enums.StrArrayValuable;
import com.nuocao.common.exception.CommonException;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 配置分类枚举
 */
@Getter
public enum DevConfigCategoryEnum implements StrArrayValuable {

    /**
     * 系统配置
     */
    SYS_CONFIG("sys_config"),

    /**
     * 账户注册配置
     */
    ACCOUNT_REG("account_reg"),
    /**
     * 密码配置
     */
    PASSWORD_CONFIG("password_config"),
    /**
     * 文件-腾讯云
     */
    FILE_TENCENT("file_tencentT"),

    /**
     * 文件-阿里云
     */
    FILE_ALIYUN("file_aliyun"),

    /**
     * 文件-MINIO
     */
    FILE_MINIO("file_minio"),

    /**
     * 文件-本地配置
     */
    FILE_LOCAL("file_lcoal"),

    /**
     * 邮件-本地
     */
    EMAIL_LOCAL("email_local"),

    /**
     * 邮件-腾讯云
     */
    EMAIL_TENCENT("email_tencent"),

    /**
     * 邮件-阿里云
     */
    EMAIL_ALIYUN("email_aliyun"),

    /**
     * 短信-腾讯云
     */
    SMS_TENCENT("sms_tencent"),

    /**
     * 短信-阿里云
     */
    SMS_ALIYUN("sms_aliyun"),

    /**
     * 其他配置
     */
    OTHER("other");

    private static String[] ARRAYS = Arrays.stream(DevConfigCategoryEnum.values()).map(DevConfigCategoryEnum::getValue).toArray(String[]::new);


    private final String value;

    DevConfigCategoryEnum(String value) {
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

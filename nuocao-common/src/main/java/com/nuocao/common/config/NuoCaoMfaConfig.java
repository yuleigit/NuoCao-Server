package com.nuocao.common.config;

import lombok.Getter;
import lombok.Setter;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 2fa 登录动态验证配置
 */
@Getter
@Setter
@Inject("${nuocao.mfa}")
@Configuration
public class NuoCaoMfaConfig {

    /** 2faReqId 有效期 */
    private Duration requestTtl = Duration.ofSeconds(300);

    /** 绑定时 otpauth 二维码有效期 */
    private Duration bindTtl = Duration.ofMinutes(15);

    /** TOTP 参数 */
    private Totp totp = new Totp();

    /** secret 落库加密密钥 */
    private SecretKey secretKey = new SecretKey();

    @Getter
    @Setter
    public static class Totp {
        private String issuer = "NuoCaoAdmin";
        private int digits = 6;
        private int periodSeconds = 30;
        private int window = 1;
        private int secretBytes = 20;
        private int maxFailedAttempts = 5;
        private Duration lockDuration = Duration.ofMinutes(15);
    }

    /**
     * secret 加密密钥配置，支持多版本灰度轮换。
     * <p>keys 的 value 为 32 字节密钥的 Base64 字符串，active-version 指定新数据使用的版本。
     */
    @Getter
    @Setter
    public static class SecretKey {
        private int activeVersion = 1;
        private Map<Integer, String> keys = new HashMap<>();
    }
}

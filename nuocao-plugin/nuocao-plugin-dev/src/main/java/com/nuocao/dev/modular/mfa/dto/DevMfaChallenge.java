package com.nuocao.dev.modular.mfa.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 2FA 登录挑战，存放在缓存中，有效期由 nuocao.mfa.request-ttl 控制。
 **/
@Getter
@Setter
public class DevMfaChallenge implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 验证动态码 */
    public static final String PURPOSE_VERIFY = "VERIFY";

    /** 未绑定用户先绑定 */
    public static final String PURPOSE_BIND = "BIND";

    private String reqId;

    private Long tenantId;

    private String ownerType;

    private Long ownerId;

    private String username;

    private String device;

    private String deviceId;

    private String purpose;

    private String bindId;

    private String ip;

    private String userAgent;

    /** 过期时间戳（毫秒） */
    private long expireAt;

    /** 当前 reqId 已失败次数 */
    private int attempts;
}

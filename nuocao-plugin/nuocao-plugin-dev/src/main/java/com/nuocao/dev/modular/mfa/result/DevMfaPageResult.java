package com.nuocao.dev.modular.mfa.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 2FA 绑定分页结果（不包含 secret 密文）
 **/
@Getter
@Setter
public class DevMfaPageResult {

    @ApiModelProperty(value = "租户ID")
    private Long tenantId;

    @ApiModelProperty(value = "绑定主体类型")
    private String ownerType;

    @ApiModelProperty(value = "用户ID")
    private Long ownerId;

    @ApiModelProperty(value = "用户名称")
    private String ownerName;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "算法")
    private String algorithm;

    @ApiModelProperty(value = "验证码位数")
    private Integer digits;

    @ApiModelProperty(value = "时间步长（秒）")
    private Integer periodSeconds;

    @ApiModelProperty(value = "连续失败次数")
    private Integer failedAttempts;

    @ApiModelProperty(value = "锁定截止时间")
    private LocalDateTime lockedUntil;

    @ApiModelProperty(value = "绑定过期时间")
    private LocalDateTime bindExpiresAt;

    @ApiModelProperty(value = "启用时间")
    private LocalDateTime activatedAt;

    @ApiModelProperty(value = "最近验证时间")
    private LocalDateTime lastUsedAt;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
}

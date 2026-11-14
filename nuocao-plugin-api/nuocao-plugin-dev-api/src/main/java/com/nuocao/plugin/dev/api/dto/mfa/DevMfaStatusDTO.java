package com.nuocao.plugin.dev.api.dto.mfa;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户 2FA 状态
 */
@Data
public class DevMfaStatusDTO implements Serializable {

    @ApiModelProperty("是否已绑定并启用")
    private boolean bound;

    @ApiModelProperty("状态值：1=绑定中，9=已启用，4=已失效")
    private Integer status;

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("启用时间")
    private LocalDateTime activatedAt;

    @ApiModelProperty("最近一次验证时间")
    private LocalDateTime lastUsedAt;

    @ApiModelProperty("锁定截止时间")
    private LocalDateTime lockedUntil;

    @ApiModelProperty("连续失败次数")
    private Integer failedAttempts;
}

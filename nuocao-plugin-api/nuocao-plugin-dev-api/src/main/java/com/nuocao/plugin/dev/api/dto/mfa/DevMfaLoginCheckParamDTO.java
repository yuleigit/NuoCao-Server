package com.nuocao.plugin.dev.api.dto.mfa;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录时 2FA 策略检查参数
 */
@Data
public class DevMfaLoginCheckParamDTO implements Serializable {

    @ApiModelProperty("租户ID")
    private Long tenantId;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("登录账号")
    private String username;

    @ApiModelProperty("登录设备")
    private String device;

    @ApiModelProperty("登录设备ID")
    private String deviceId;

    @ApiModelProperty("客户端IP")
    private String ip;

    @ApiModelProperty("客户端UserAgent")
    private String userAgent;
}

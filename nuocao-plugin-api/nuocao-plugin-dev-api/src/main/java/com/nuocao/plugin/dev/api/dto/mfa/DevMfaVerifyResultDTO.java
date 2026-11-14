package com.nuocao.plugin.dev.api.dto.mfa;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 动态码验证成功结果
 */
@Data
public class DevMfaVerifyResultDTO implements Serializable {

    @ApiModelProperty("租户ID")
    private Long tenantId;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("登录账号")
    private String username;

    @ApiModelProperty("用户姓名")
    private String name;

    @ApiModelProperty("登录设备")
    private String device;

    @ApiModelProperty("登录设备ID")
    private String deviceId;

    @ApiModelProperty("命中的时间步")
    private long matchedStep;
}

package com.nuocao.dev.modular.mfa.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * 2FA 绑定主体定位参数（复合主键）
 **/
@Getter
@Setter
public class DevMfaOwnerParam {

    @ApiModelProperty(value = "租户ID", required = true)
    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    @ApiModelProperty(value = "绑定主体类型", notes = "默认 user")
    private String ownerType;

    @ApiModelProperty(value = "用户ID", required = true)
    @NotNull(message = "用户ID不能为空")
    private Long ownerId;
}

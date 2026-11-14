package com.nuocao.dev.modular.mfa.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 2FA 操作事件分页结果
 **/
@Getter
@Setter
public class DevMfaEventPageResult {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "租户ID")
    private Long tenantId;

    @ApiModelProperty(value = "绑定主体类型")
    private String ownerType;

    @ApiModelProperty(value = "用户ID")
    private Long ownerId;

    @ApiModelProperty(value = "事件类型")
    private String event;

    @ApiModelProperty(value = "关联ID")
    private String refId;

    @ApiModelProperty(value = "客户端IP")
    private String ip;

    @ApiModelProperty(value = "客户端UserAgent")
    private String userAgent;

    @ApiModelProperty(value = "事件详情")
    private String detail;

    @ApiModelProperty(value = "创建人")
    private String createUser;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
}

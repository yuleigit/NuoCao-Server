package com.nuocao.dev.modular.mfa.param;

import com.nuocao.common.page.SortPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 2FA 操作事件分页查询参数
 **/
@Getter
@Setter
public class DevMfaEventPageParam extends SortPageParam {

    @ApiModelProperty(value = "租户ID，为空时使用当前登录租户")
    private Long tenantId;

    @ApiModelProperty(value = "绑定主体类型")
    private String ownerType;

    @ApiModelProperty(value = "用户ID")
    private Long ownerId;

    @ApiModelProperty(value = "事件类型")
    private String event;

    @ApiModelProperty(value = "开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endTime;
}

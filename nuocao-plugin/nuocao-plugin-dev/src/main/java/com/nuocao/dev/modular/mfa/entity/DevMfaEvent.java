package com.nuocao.dev.modular.mfa.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.nuocao.common.pojo.CommonEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 2FA 操作事件实体
 **/
@Getter
@Setter
@Table(value = "dev_mfa_event")
public class DevMfaEvent extends CommonEntity {

    @ApiModelProperty(value = "id", position = 1)
    @Id
    private Long id;

    @ApiModelProperty(value = "租户ID", position = 2)
    private Long tenantId;

    @ApiModelProperty(value = "绑定主体类型", position = 3)
    private String ownerType;

    @ApiModelProperty(value = "绑定主体ID", position = 4)
    private Long ownerId;

    @ApiModelProperty(value = "事件类型", position = 5)
    private String event;

    @ApiModelProperty(value = "关联ID（如 mfaReqId、bindId）", position = 6)
    private String refId;

    @ApiModelProperty(value = "客户端IP", position = 7)
    private String ip;

    @ApiModelProperty(value = "客户端UserAgent", position = 8)
    private String userAgent;

    @ApiModelProperty(value = "事件详情", position = 9)
    private String detail;
}

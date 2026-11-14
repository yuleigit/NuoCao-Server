package com.nuocao.dev.modular.mfa.param;

import com.nuocao.common.page.SortPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 2FA 绑定分页查询参数
 **/
@Getter
@Setter
public class DevMfaPageParam extends SortPageParam {

    @ApiModelProperty(value = "租户ID，为空时使用当前登录租户")
    private Long tenantId;

    @ApiModelProperty(value = "绑定主体类型", notes = "默认 user")
    private String ownerType;

    @ApiModelProperty(value = "用户ID")
    private Long ownerId;

    @ApiModelProperty(value = "用户名称，模糊查询")
    private String ownerName;

    @ApiModelProperty(value = "状态：1=绑定中，9=已启用，4=已失效")
    private Integer status;
}

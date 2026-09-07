package com.nuocao.auth.modular.login.param.login;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotNull;


/**
 * 切换租户
 *
 * @author LEI
 * @date 2022/7/7 16:46
 **/
@Getter
@Setter
public class AuthSwitchTenantParam {

    /** 租户ID */
    @ApiModelProperty(value = "租户ID", position = 5)
    @NotNull(message = "机构ID必须填写")
    private Long tenantId;

    @ApiModelProperty(value = "当前设备", position = 5)
    private String device;
}

package com.nuocao.sys.modular.account.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * 已登录用户确认绑定 2FA 参数
 **/
@Getter
@Setter
public class SysAccountMfaBindConfirmParam {

    @ApiModelProperty(value = "绑定ID", required = true, position = 1)
    @NotBlank
    private String bindId;

    @ApiModelProperty(value = "动态码", required = true, position = 2)
    @NotBlank
    private String code;
}

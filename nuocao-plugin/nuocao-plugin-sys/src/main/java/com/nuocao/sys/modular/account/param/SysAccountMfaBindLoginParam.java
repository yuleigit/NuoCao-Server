package com.nuocao.sys.modular.account.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 登录流程中未绑定用户发起绑定 参数
 **/
@Getter
@Setter
public class SysAccountMfaBindLoginParam {

    @ApiModelProperty(value = "2FA验证请求ID", required = true, position = 1)
    @NotBlank
    private String mfaReqId;

    @ApiModelProperty(value = "用户ID", required = true, position = 2)
    @NotNull
    private Long userId;
}

package com.nuocao.sys.modular.account.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 登录流程中未绑定用户确认绑定 参数
 **/
@Getter
@Setter
public class SysAccountMfaBindLoginConfirmParam {

    @ApiModelProperty(value = "2FA验证请求ID", required = true, position = 1)
    @NotBlank
    private String mfaReqId;

    @ApiModelProperty(value = "用户ID", required = true, position = 2)
    @NotNull
    private Long userId;

    @ApiModelProperty(value = "绑定ID", required = true, position = 3)
    @NotBlank
    private String bindId;

    @ApiModelProperty(value = "动态码", required = true, position = 4)
    @NotBlank
    private String code;
}

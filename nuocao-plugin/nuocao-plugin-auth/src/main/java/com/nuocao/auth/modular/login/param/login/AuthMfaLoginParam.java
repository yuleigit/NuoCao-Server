package com.nuocao.auth.modular.login.param.login;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 2FA 动态码登录验证参数
 **/
@Getter
@Setter
public class AuthMfaLoginParam {

    @ApiModelProperty(value = "2FA验证请求ID", required = true, position = 1)
    @NotBlank
    private String mfaReqId;

    @ApiModelProperty(value = "用户ID", required = true, position = 2)
    @NotNull
    private Long userId;

    @ApiModelProperty(value = "动态码", required = true, position = 3)
    @NotBlank
    private String code;
}

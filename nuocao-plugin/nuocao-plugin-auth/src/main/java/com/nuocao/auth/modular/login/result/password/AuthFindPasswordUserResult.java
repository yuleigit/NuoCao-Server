package com.nuocao.auth.modular.login.result.password;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 找回密码的用户信息实体
 */
@Getter
@Setter
public class AuthFindPasswordUserResult {

    @ApiModelProperty(value="用户账号")
    private String account;

    @ApiModelProperty(value="用户手机号")
    private String phone;

    @ApiModelProperty(value = "用户的邮箱")
    private String email;

}

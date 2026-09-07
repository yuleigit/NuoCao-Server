package com.nuocao.auth.modular.login.param.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * 获取邮箱验证码参数
 **/
@Getter
@Setter
public class AuthGetEmailValidCodeParam {

    /** 邮箱 */
    @ApiModelProperty(value = "邮箱", required = true, position = 1)
    @NotBlank(message = "邮箱不能为空")
    private String email;

    /** 验证码 */
    @ApiModelProperty(value = "验证码", required = true, position = 2)
    @NotBlank(message = "验证码不能为空")
    private String validCode;

    /** 验证码请求号 */
    @ApiModelProperty(value = "验证码请求号", required = true, position = 3)
    @NotBlank(message = "验证码请求号不能为空")
    private String validCodeReqId;
}

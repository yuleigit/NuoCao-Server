package com.nuocao.auth.modular.login.param.password;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;

@Setter
@Getter
public class AuthPhoneFindPasswordParam {

    /** 手机号 */
    @ApiModelProperty(value = "手机号", required = true, position = 1)
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /** 验证码 */
    @ApiModelProperty(value = "验证码", required = true, position = 2)
    @NotBlank(message = "验证码不能为空")
    private String validCode;

    /** 验证码请求号 */
    @ApiModelProperty(value = "验证码请求号", required = true, position = 3)
    @NotBlank(message = "验证码请求号不能为空")
    private String validCodeReqId;

    /** 新密码 */
    @ApiModelProperty(value = "新密码",notes = "加密传输", required = true, position = 4)
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}

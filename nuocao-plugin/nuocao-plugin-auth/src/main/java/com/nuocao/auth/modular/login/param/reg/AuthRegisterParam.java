package com.nuocao.auth.modular.login.param.reg;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * 注册参数
 **/
@Getter
@Setter
public class AuthRegisterParam {
    /** 账号 */
    @ApiModelProperty(value = "姓名", required = true)
    @NotBlank(message = "姓名不能为空")
    private String name;

    /** 账号 */
    @ApiModelProperty(value = "账号", required = true)
    @NotBlank(message = "账号不能为空")
    private String username;

    /** 账号 */
    @ApiModelProperty(value = "手机号码")
    private String phone;


    /** 注册EMAIL */
    @ApiModelProperty(value = "注册EMAIL")
    private String email;

    /** 密码 */
    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message = "密码不能为空")
    private String password;

    /** 验证码 */
    @ApiModelProperty(value = "验证码")
    private String validCode;

    /** 验证码请求号 */
    @ApiModelProperty(value = "验证码请求号")
    private String validCodeReqId;
}

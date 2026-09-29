package com.nuocao.sys.modular.account.pojo.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * 用户通过验证邮箱修改密码参数

 **/
@Getter
@Setter
public class SysAccountUpdatePwdByEmailParam {

    /** 邮箱 */
    @ApiModelProperty(value = "邮箱")
    @NotBlank(message = "email不能为空")
    private String email;

    /** 验证码 */
    @ApiModelProperty(value = "验证码")
    @NotBlank(message = "validCode不能为空")
    private String validCode;

    /** 验证码请求号 */
    @ApiModelProperty(value = "验证码请求号")
    @NotBlank(message = "validCodeReqNo不能为空")
    private String validCodeReqNo;

    /** 新密码 */
    @ApiModelProperty(value = "新密码")
    @NotBlank(message = "newPassword不能为空")
    private String newPassword;
}

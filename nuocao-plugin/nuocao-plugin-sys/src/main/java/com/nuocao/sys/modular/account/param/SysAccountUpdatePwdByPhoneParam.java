package com.nuocao.sys.modular.account.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * 用户通过验证手机号修改密码参数
 *
 **/
@Getter
@Setter
public class SysAccountUpdatePwdByPhoneParam {

    /** 验证码 */
    @ApiModelProperty(value = "验证码")
    @NotBlank(message = "validCode不能为空")
    private String validCode;

    /** 验证码请求号 */
    @ApiModelProperty(value = "验证码请求号")
    @NotBlank(message = "validCodeReqId不能为空")
    private String validCodeReqId;

    /** 新密码 */
    @ApiModelProperty(value = "新密码")
    @NotBlank(message = "newPassword不能为空")
    private String newPassword;
}

package com.nuocao.sys.modular.account.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * 获取邮箱验证码
 *
 **/
@Getter
@Setter
public class SysAccountGetEmailValidCodeParam {

    /** 邮箱 */
    @ApiModelProperty(value = "邮箱")
    @NotBlank(message = "email不能为空")
    private String email;

    /** 验证码 */
    @ApiModelProperty(value = "验证码",notes = "获取手机验证码时，需要填写图片验证码")
    @NotBlank(message = "validCode不能为空")
    private String validCode;

    /** 验证码请求号 */
    @ApiModelProperty(value = "验证码请求号")
    @NotBlank(message = "validCodeReqNo不能为空")
    private String validCodeReqNo;
}

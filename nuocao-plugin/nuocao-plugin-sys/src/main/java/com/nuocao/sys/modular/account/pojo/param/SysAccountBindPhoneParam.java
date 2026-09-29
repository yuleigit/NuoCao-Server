package com.nuocao.sys.modular.account.pojo.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * 绑定手机号参数
 **/
@Getter
@Setter
public class SysAccountBindPhoneParam {

    /** 手机号 */
    @ApiModelProperty(value = "手机号")
    @NotBlank(message = "phone不能为空")
    private String phone;

    /** 验证码 */
    @ApiModelProperty(value = "验证码")
    @NotBlank(message = "validCode不能为空")
    private String validCode;

    /** 验证码请求号 */
    @ApiModelProperty(value = "验证码请求号")
    @NotBlank(message = "validCodeReqNo不能为空")
    private String validCodeReqNo;
}

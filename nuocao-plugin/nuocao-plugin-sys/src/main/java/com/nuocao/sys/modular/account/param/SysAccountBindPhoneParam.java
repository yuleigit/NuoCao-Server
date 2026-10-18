package com.nuocao.sys.modular.account.param;

import com.nuocao.common.validation.Mobile;
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
    @Mobile
    private String phone;

    /** 验证码 */
    @ApiModelProperty(value = "短信验证码")
    @NotBlank(message = "validCode不能为空")
    private String validCode;

    /** 验证码请求号 */
    @ApiModelProperty(value = "验证码请求号")
    @NotBlank(message = "validCodeReqId不能为空")
    private String validCodeReqId;
}

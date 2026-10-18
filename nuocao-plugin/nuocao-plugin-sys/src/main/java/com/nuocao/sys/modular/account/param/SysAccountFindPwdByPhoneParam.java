package com.nuocao.sys.modular.account.param;

import com.nuocao.common.validation.Mobile;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;


/**
 * 用户找回密码参数
 * @deprecated 已迁移至 auth 模块，请使用 AuthPhoneFindPasswordParam
 **/
@Deprecated
@Getter
@Setter
public class SysAccountFindPwdByPhoneParam {

    /** 手机号 */
    @ApiModelProperty(value = "手机号",notes = "加密发送")
    @NotBlank(message = "phone不能为空")
    @Mobile
    private String phone;

    /** 验证码 */
    @ApiModelProperty(value = "验证码")
    @NotBlank(message = "validCode不能为空")
    private String validCode;

    /** 验证码请求号 */
    @ApiModelProperty(value = "验证码请求号")
    @NotBlank(message = "validCodeReqId不能为空")
    private String validCodeReqId;

    /** 新密码 */
    @ApiModelProperty(value = "新密码",notes = "加密发送")
    @NotBlank(message = "newPassword不能为空")
    private String newPassword;
}

package com.nuocao.sys.modular.user.pojo.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import org.noear.solon.validation.annotation.NotBlank;

/**
 * 用户修改签名图片接口
 **/
@Getter
@Setter
public class SysUserSignatureParam {

    /** 签名图片base64编码 */
    @ApiModelProperty(value = "signature", required = true)
    @NotBlank(message = "signature签名图片不能为空")
    private String signature;
}

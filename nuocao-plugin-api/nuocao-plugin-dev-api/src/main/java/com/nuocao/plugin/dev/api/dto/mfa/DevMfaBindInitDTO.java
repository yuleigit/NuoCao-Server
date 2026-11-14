package com.nuocao.plugin.dev.api.dto.mfa;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 绑定初始化结果
 */
@Data
public class DevMfaBindInitDTO implements Serializable {

    @ApiModelProperty("绑定ID")
    private String bindId;

    @ApiModelProperty("otpauth 二维码内容")
    private String otpauthUri;

    @ApiModelProperty("二维码图片（data:image/png;base64，可直接用于 img src）")
    private String qrCodeDataUrl;

    @ApiModelProperty("有效期（秒）")
    private long expiresIn;
}

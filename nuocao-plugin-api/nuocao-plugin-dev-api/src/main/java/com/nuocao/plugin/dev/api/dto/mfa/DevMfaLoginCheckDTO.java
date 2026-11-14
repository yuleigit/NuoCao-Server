package com.nuocao.plugin.dev.api.dto.mfa;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录时 2FA 策略检查结果
 */
@Data
public class DevMfaLoginCheckDTO implements Serializable {

    @ApiModelProperty("是否需要输入动态码")
    private boolean needMfa;

    @ApiModelProperty("是否需要先绑定（强制 2FA 且用户未绑定）")
    private boolean needBindMfa;

    @ApiModelProperty("动态码验证请求ID，5 分钟内有效")
    private String mfaReqId;

    @ApiModelProperty("mfaReqId 有效期（秒）")
    private long expiresIn;

    @ApiModelProperty("绑定ID")
    private String bindId;

    @ApiModelProperty("otpauth 二维码内容")
    private String otpauthUri;

    @ApiModelProperty("二维码图片（data:image/png;base64，可直接用于 img src）")
    private String qrCodeDataUrl;

    @ApiModelProperty("绑定二维码有效期（秒）")
    private long bindExpiresIn;
}

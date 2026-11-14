package com.nuocao.auth.modular.login.result.login;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 登录结果
 */
@Data
public class AuthLoginResult {
    private Long tenantId;

    private String token;

    /**
     * 登录的账号,非必要,仅loginToTenant使用
     */
    private String username;

    /**
     * 登录人姓名，非必要，仅loginToTenant使用
     */
    private String name;

    /**
     * 是否需要输入 2FA 动态码（此时不返回 token）
     */
    @ApiModelProperty(value = "是否需要输入2FA动态码")
    private boolean needMfa;

    /**
     * 是否需要先绑定 2FA（强制开启且用户未绑定时，此时不返回 token）
     */
    @ApiModelProperty(value = "是否需要先绑定2FA")
    private boolean needBindMfa;

    /**
     * 2FA 验证请求ID，前端回传，5 分钟内有效
     */
    @ApiModelProperty(value = "2FA验证请求ID")
    private String mfaReqId;

    /**
     * 2FA 验证用户ID，与 mfaReqId 一起回传
     */
    @ApiModelProperty(value = "2FA验证用户ID")
    private Long userId;

    /**
     * mfaReqId 有效期（秒）
     */
    @ApiModelProperty(value = "2FA验证请求有效期（秒）")
    private Long mfaExpiresIn;

    /**
     * 绑定ID
     */
    @ApiModelProperty(value = "2FA绑定ID")
    private String bindId;

    /**
     * otpauth 二维码内容
     */
    @ApiModelProperty(value = "otpauth二维码内容")
    private String otpauthUri;

    /**
     * 绑定二维码图片（data:image/png;base64）
     */
    @ApiModelProperty(value = "2FA绑定二维码图片Base64")
    private String qrCodeDataUrl;

    /**
     * 绑定二维码有效期（秒）
     */
    @ApiModelProperty(value = "绑定二维码有效期（秒）")
    private Long bindExpiresIn;

    public AuthLoginResult(String token,Long tenantId){
        this.tenantId=tenantId;
        this.token=token;
    }

    /**
     * 需要二次验证或绑定时使用，不签发 token
     */
    public AuthLoginResult(Long tenantId){
        this.tenantId=tenantId;
    }
}

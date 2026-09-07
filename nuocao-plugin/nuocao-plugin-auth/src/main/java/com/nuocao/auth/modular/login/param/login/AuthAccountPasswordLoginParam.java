package com.nuocao.auth.modular.login.param.login;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.Length;
import org.noear.solon.validation.annotation.NotBlank;


/**
 * 账号密码登录参数
 **/
@Getter
@Setter
public class AuthAccountPasswordLoginParam {

    /** 用户名 */
    @ApiModelProperty(value = "用户名", required = true, position = 1)
    @NotBlank
    @Length(min=3)
    private String username;

    /** 密码 */
    @ApiModelProperty(value = "密码", required = true, position = 2)
    @NotBlank
    private String password;

    /** 设备 */
    @ApiModelProperty(value = "设备", position = 3)
    private String device;

    /**
     * 登录设备ID，会存储到sa-token中，如果接入app可根据deviceId推送信息
     */
    @ApiModelProperty(value="设备ID",position = 4)
    private String deviceId;

    /** 验证码 */
    @ApiModelProperty(value = "验证码", position = 5)
    private String validCode;

    /** 验证码请求号 */
    @ApiModelProperty(value = "验证码请求号", position = 6)
    private String validCodeReqId;
}

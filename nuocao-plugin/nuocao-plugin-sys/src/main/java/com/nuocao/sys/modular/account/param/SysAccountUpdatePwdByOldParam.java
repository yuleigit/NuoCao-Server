package com.nuocao.sys.modular.account.param;

import io.swagger.annotations.ApiModelProperty;
import org.noear.solon.validation.annotation.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 使用旧密码修改用户密码
 *
 **/
@Getter
@Setter
public class SysAccountUpdatePwdByOldParam {

    /** 旧密码 */
    @ApiModelProperty(value = "旧密码")
    @NotBlank(message = "password不能为空")
    private String password;

    /** 新密码 */
    @ApiModelProperty(value = "新密码")
    @NotBlank(message = "newPassword不能为空")
    private String newPassword;
}

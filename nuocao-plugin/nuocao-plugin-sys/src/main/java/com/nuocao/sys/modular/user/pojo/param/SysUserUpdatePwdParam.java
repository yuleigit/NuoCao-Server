package com.nuocao.sys.modular.user.pojo.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import org.noear.solon.validation.annotation.NotBlank;

/**
 * 用户修改密码参数
 **/
@Getter
@Setter
public class SysUserUpdatePwdParam {

    /** 旧密码 */
    @ApiModelProperty(value = "旧密码", required = true, position = 2)
    @NotBlank(message = "password不能为空")
    private String password;

    /** 新密码 */
    @ApiModelProperty(value = "新密码", required = true, position = 3)
    @NotBlank(message = "newPassword不能为空")
    private String newPassword;
}

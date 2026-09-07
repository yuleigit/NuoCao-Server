package com.nuocao.sys.modular.user.pojo.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * 用户通过验证旧密码修改密码参数
 **/
@Getter
@Setter
public class SysUserUpdatePwdByOldParam {

    /** 旧密码 */
    @ApiModelProperty(value = "旧密码")
    @NotBlank(message = "password不能为空")
    private String password;

    /** 新密码 */
    @ApiModelProperty(value = "新密码")
    @NotBlank(message = "newPassword不能为空")
    private String newPassword;
}

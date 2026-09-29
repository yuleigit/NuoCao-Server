package com.nuocao.sys.modular.user.pojo.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 重置密码参数

 **/
@Getter
@Setter
public class SysUserResetPwdParam {

    /** id */
    @ApiModelProperty(value = "id", required = true)
    @NotNull(message = "id不能为空")
    private Long id;

    @ApiModelProperty(value = "新密码", required = true)
    @NotBlank
    private String newPassword;
}

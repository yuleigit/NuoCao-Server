package com.nuocao.sys.modular.account.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 重置密码参数
 *

 **/
@Getter
@Setter
public class SysAccountResetPwdParam {

    /** id */
    @ApiModelProperty(value = "账户ID", required = true)
    @NotNull
    private Long id;

    @ApiModelProperty(value = "新密码", required = true)
    @NotBlank
    private String newPassword;
}

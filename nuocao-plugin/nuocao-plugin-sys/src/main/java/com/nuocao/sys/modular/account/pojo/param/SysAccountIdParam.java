package com.nuocao.sys.modular.account.pojo.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotNull;
import org.noear.solon.validation.annotation.Size;

/**
 * 系统战鼓 Id参数
 *

 **/
@Getter
@Setter
public class SysAccountIdParam {

    /** id */
    @ApiModelProperty(value = "账户ID", required = true)
    @NotNull
    private Long id;
}

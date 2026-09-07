package com.nuocao.sys.modular.user.pojo.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import org.noear.solon.validation.annotation.NotNull;

/**
 * 站内信Id参数
 */
@Getter
@Setter
public class SysUserMessageIdParam {

    /** id */
    @ApiModelProperty(value = "id", required = true)
    @NotNull(message = "id不能为空")
    private Long id;
}

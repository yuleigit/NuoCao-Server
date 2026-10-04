package com.nuocao.dev.modular.message.pojo.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 站内信Id参数
 **/
@Getter
@Setter
public class DevMessageIdParam {

    /** id */
    @ApiModelProperty(value = "id", required = true)
    @NotNull(message = "id不能为空")
    private Long id;
}

package com.nuocao.dev.modular.config.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;


/**
 * 配置Id参数
 */
@Getter
@Setter
public class DevConfigIdParam {

    /** id */
    @ApiModelProperty(value = "configId", required = true)
    @NotBlank(message = "configId不能为空")
    private Long id;
}

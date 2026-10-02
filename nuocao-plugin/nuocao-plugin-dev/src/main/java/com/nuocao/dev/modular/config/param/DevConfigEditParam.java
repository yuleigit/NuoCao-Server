package com.nuocao.dev.modular.config.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 配置编辑参数
 */
@Getter
@Setter
public class DevConfigEditParam extends DevConfigAddEditCommonParam {

    /** id */
    @ApiModelProperty(value = "id", required = true, position = 1)
    @NotNull(message = "id不能为空")
    private Long id;

}

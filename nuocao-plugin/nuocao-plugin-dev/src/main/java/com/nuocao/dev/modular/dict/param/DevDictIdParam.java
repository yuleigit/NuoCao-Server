package com.nuocao.dev.modular.dict.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 字典Id参数
 */
@Getter
@Setter
public class DevDictIdParam {

    /** dictId */
    @ApiModelProperty(value = "dictId", required = true)
    @NotNull
    private Long id;
}

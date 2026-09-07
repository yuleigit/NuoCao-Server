package com.nuocao.gen.modular.table.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 代码生成表tableId参数
 **/
@Getter
@Setter
public class GenTableIdParam {

    /** id */
    @ApiModelProperty(value = "tableId", required = true)
    @NotNull(message = "tableId不能为空")
    private Long id;
}

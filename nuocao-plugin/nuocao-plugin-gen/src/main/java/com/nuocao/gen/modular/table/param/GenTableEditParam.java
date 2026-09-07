package com.nuocao.gen.modular.table.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * 代码生成表编辑参数
 *
 **/
@Getter
@Setter
public class GenTableEditParam extends GenTableAddEditCommonParam {

    /** id */
    @ApiModelProperty(value = "id", required = true, position = 1)
    @NotNull(message = "id不能为空")
    private String id;

}

package com.nuocao.dev.modular.dict.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 字典列表参数
 */
@Getter
@Setter
public class DevDictListParam {

    /** 父id */
    @ApiModelProperty(value = "父id")
    private String parentKey;

    /** 字典分类 */
    @ApiModelProperty(value = "字典分类")
    private String category;
}

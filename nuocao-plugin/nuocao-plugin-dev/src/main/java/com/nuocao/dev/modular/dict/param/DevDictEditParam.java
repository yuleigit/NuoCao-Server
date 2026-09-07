package com.nuocao.dev.modular.dict.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 字典编辑参数
 */
@Getter
@Setter
public class DevDictEditParam extends DevDictAddEditCommonParam {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    @NotNull
    private Long id;

}

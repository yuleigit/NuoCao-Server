package com.nuocao.dev.modular.dict.param;

import com.nuocao.common.validation.InStrEnum;
import com.nuocao.dev.modular.dict.enums.DevDictCategoryEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;


/**
 * 字典添加参数
 *
 */
@Getter
@Setter
public class DevDictAddParam extends DevDictAddEditCommonParam {

    /** 分类 */
    @ApiModelProperty(value = "分类", position = 4)
    @NotBlank
    @InStrEnum(DevDictCategoryEnum.class)
    private String category=DevDictCategoryEnum.SYS.getValue();

}

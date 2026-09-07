package com.nuocao.dev.modular.config.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 配置添加参数
 */
@Getter
@Setter
public class DevConfigAddEditCommonParam {



    /** 配置值 */
    @ApiModelProperty(value = "配置值", required = true, position = 2)
    @NotBlank(message = "configValue不能为空")
    private String configValue;

    /** 备注 */
    @ApiModelProperty(value = "备注", position = 3)
    private String remark;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", required = true, position = 4)
    @NotNull(message = "sortCode不能为空")
    private Integer sortCode;
}

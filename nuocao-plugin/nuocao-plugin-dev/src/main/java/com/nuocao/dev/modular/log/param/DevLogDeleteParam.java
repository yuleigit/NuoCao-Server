package com.nuocao.dev.modular.log.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * 日志清空参数
 */
@Getter
@Setter
public class DevLogDeleteParam {

    /** 日志分类 */
    @ApiModelProperty(value = "日志分类", required = true)
    @NotBlank(message = "category不能为空")
    private String category;
}

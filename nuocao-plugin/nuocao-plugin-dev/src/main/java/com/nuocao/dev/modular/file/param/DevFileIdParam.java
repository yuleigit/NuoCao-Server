package com.nuocao.dev.modular.file.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import org.noear.solon.validation.annotation.NotBlank;

/**
 * 文件Id参数
 */
@Getter
@Setter
public class DevFileIdParam {

    /** id */
    @ApiModelProperty(value = "fileId", required = true)
    @NotBlank(message = "fileId不能为空")
    private String id;
}

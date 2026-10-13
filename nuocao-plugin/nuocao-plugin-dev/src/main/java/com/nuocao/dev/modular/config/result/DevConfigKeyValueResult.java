package com.nuocao.dev.modular.config.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * key value显示配置信息
 */
@Setter
@Getter
public class DevConfigKeyValueResult {

    @ApiModelProperty(value = "配置键", required = true, position = 1)
    private String configKey;

    @ApiModelProperty(value = "配置值", required = true, position = 2)
    private String configValue;
}
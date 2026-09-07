package com.nuocao.dev.modular.sms.log.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import org.noear.solon.validation.annotation.NotNull;

/**
 * 短信Id参数
 */
@Getter
@Setter
public class DevSmsLogIdParam {

    /** id */
    @ApiModelProperty(value = "logId", required = true)
    @NotNull
    private Long id;
}

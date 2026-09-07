package com.nuocao.dev.modular.sms.template.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 字典Id参数
 */
@Getter
@Setter
public class DevSmsTemplateIdParam {

    /** templateId */
    @ApiModelProperty(value = "templateId", required = true)
    @NotNull
    private Long templateId;
}

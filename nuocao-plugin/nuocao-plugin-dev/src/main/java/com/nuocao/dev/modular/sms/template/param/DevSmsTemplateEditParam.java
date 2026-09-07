package com.nuocao.dev.modular.sms.template.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 字典编辑参数
 */
@Getter
@Setter
public class DevSmsTemplateEditParam extends DevSmsTemplateAddEditCommonParam {

    /** templateId */
    @ApiModelProperty(value = "templateId", position = 1)
    @NotNull
    private Long id;

}

package com.nuocao.dev.modular.sms.template.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import com.nuocao.plugin.dev.api.enums.sms.DevSmsTypeEnum;


/**
 * 字典添加参数
 *
 */
@Getter
@Setter
public class DevSmsTemplateAddParam extends DevSmsTemplateAddEditCommonParam {

    /** 短信类型
     * {@link DevSmsTypeEnum}
     * */
    @ApiModelProperty(value = "短信类型", position = 2)
    private Integer type;

    /** 是否系统内置 */
    @ApiModelProperty(value = "是否系统内置", position = 3)
    private Integer isSys;


}

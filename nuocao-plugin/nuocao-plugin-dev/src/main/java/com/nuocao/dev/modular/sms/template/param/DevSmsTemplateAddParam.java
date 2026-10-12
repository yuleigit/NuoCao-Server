package com.nuocao.dev.modular.sms.template.param;

import com.nuocao.common.enums.CommonYesNoEnum;
import com.nuocao.common.validation.InEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import com.nuocao.plugin.dev.api.enums.sms.DevSmsTypeEnum;
import org.noear.solon.validation.annotation.Length;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;


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
    @NotNull
    @InEnum(value = DevSmsTypeEnum.class)
    private Integer type;

    /** 是否系统内置 */
    @ApiModelProperty(value = "是否系统内置", position = 3)
    @NotNull
    @InEnum(value= CommonYesNoEnum.class)
    private Integer isSys;

    /** 模板编码 */
    @ApiModelProperty(value = "模板编码",notes = "同一通道只能存在一个相同编码",position = 5)
    @NotBlank
    @Length(max = 50)
    private String code;
}

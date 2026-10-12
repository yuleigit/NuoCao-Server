package com.nuocao.dev.modular.sms.template.result;

import com.nuocao.plugin.dev.api.enums.sms.DevSmsTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DevSmsTemplateSelectorResult {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    private Long id;

    /** 短信类型
     * {@link DevSmsTypeEnum}
     * */
    @ApiModelProperty(value = "短信类型", position = 2)
    private Integer type;

    /** 模板使用短信通道 */
    @ApiModelProperty(value = "模板使用短信通道", position = 4)
    private String channelCode;

    /** 模板名称 */
    @ApiModelProperty(value = "模板名称", position = 3)
    private String name;

    /** 模板编码 */
    @ApiModelProperty(value = "模板编码",notes = "同一通道只能存在一个相同编码",position = 5)
    private String code;
    /** 排序码 */
    @ApiModelProperty(value = "模板可用性", position = 7)
    private Integer status;

}

package com.nuocao.dev.modular.sms.template.result;

import com.nuocao.plugin.dev.api.dto.sms.DevSmsTemplateVariableDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import com.nuocao.plugin.dev.api.enums.sms.DevSmsTypeEnum;

import java.util.List;

@Getter
@Setter
public class DevSmsTemplateResult {

    /** templateId */
    @ApiModelProperty(value = "templateId", position = 1)
    private Long id;

    /** 短信类型
     * {@link DevSmsTypeEnum}
     * */
    @ApiModelProperty(value = "短信类型", position = 2)
    private Integer type;

    /** 是否系统内置 */
    @ApiModelProperty(value = "是否系统内置", position = 3)
    private Integer isSys;

    /** 模板使用短信通道 */
    @ApiModelProperty(value = "模板使用短信通道", position = 4)
    private String channelCode;

    /** 模板内容 */
    @ApiModelProperty(value = "模板内容", position = 4)
    private String content;

    /** 模板名称 */
    @ApiModelProperty(value = "模板名称", position = 3)
    private String name;

    /** 模板编码 */
    @ApiModelProperty(value = "模板编码",notes = "同一通道只能存在一个相同编码",position = 5)
    private String code;

    /** 通道端模板ID */
    @ApiModelProperty(value = "通道端模板ID", position = 6)
    private String apiTemplateId;

    /** 排序码 */
    @ApiModelProperty(value = "模板可用性", position = 7)
    private Integer status;

    /** 模板备注说明 */
    @ApiModelProperty(value = "模板备注说明",position = 8)
    private String remark;
    /** 使用签名 */
    @ApiModelProperty(value = "使用签名",position = 8)
    private String signName;
    @ApiModelProperty(value = "模板变量集合",notes = "json表达的key value对",position = 9)
    private List<DevSmsTemplateVariableDTO> variables;
}

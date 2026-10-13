package com.nuocao.plugin.dev.api.dto.sms;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.core.handler.JacksonTypeHandler;
import com.nuocao.plugin.dev.api.enums.sms.DevSmsTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 短信模板 模板应用dto
 */
@Getter
@Setter
public class DevSmsTemplateDTO {

    @ApiModelProperty(value = "templateId", position = 1)
    private Long id;

    /** 短信类型
     * {@link DevSmsTypeEnum}
     * */
    @ApiModelProperty(value = "短信类型", position = 2)
    private Integer type;

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
    /** 签名" */
    @ApiModelProperty(value = "签名", position = 7)
    private String signName;
    /** 模板参数
     *
     * 模板参数目前仅支持[code,username,userAccount]等这个支持度需在templateService中配置
     * */
    @ApiModelProperty(value = "模板参数", position = 10)
    private List<DevSmsTemplateVariableDTO> variables;


}

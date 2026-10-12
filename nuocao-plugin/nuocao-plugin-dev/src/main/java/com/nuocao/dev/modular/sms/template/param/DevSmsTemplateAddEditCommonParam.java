package com.nuocao.dev.modular.sms.template.param;

import com.nuocao.common.enums.CommonEnableStatusEnum;
import com.nuocao.common.validation.InEnum;
import com.nuocao.common.validation.InStrEnum;
import com.nuocao.plugin.dev.api.dto.sms.DevSmsTemplateVariableDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import com.nuocao.plugin.dev.api.enums.sms.SmsChannelEnum;
import org.noear.solon.validation.annotation.Length;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

import java.io.Serializable;
import java.util.List;


/**
 * 短信模板 添加修改通用参数
 *
 */
@Getter
@Setter
public class DevSmsTemplateAddEditCommonParam implements Serializable {






    /** 模板使用短信通道 */
    @ApiModelProperty(value = "模板使用短信通道", position = 4)
    @NotBlank
    @InStrEnum(SmsChannelEnum.class)
    private String channelCode;

    /** 模板名称 */
    @ApiModelProperty(value = "模板名称", position = 3)
    @NotBlank
    @Length(max = 50)
    private String name;

    /** 通道端模板ID */
    @ApiModelProperty(value = "通道端模板ID", position = 6)
    @NotBlank
    @Length(max = 50)
    private String apiTemplateId;

    /** 排序码 */
    @ApiModelProperty(value = "模板可用性", position = 7)
    @NotNull
    @InEnum(CommonEnableStatusEnum.class)
    private Integer status;

    /** 模板备注说明 */
    @ApiModelProperty(value = "模板备注说明",position = 8)
    @Length(max = 200)
    private String remark;

    /** 模板内容 */
    @ApiModelProperty(value = "模板内容",position = 9)
    @NotBlank
    @Length(max = 4000)
    private String content;

    /** 模板中的参数 */
    @ApiModelProperty(value = "模板变量集合",notes = "json表达的key value对",position = 9)
    private List<DevSmsTemplateVariableEditParam> variables;
}

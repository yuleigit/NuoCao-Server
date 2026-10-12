package com.nuocao.plugin.dev.api.dto.sms;

import lombok.Getter;
import lombok.Setter;

/**
 * 短信模消息模板 参数实体
 */
@Getter
@Setter
public class DevSmsTemplateVariableDTO {

    /**
     * 变量名称（英文）
     */
    private String name;

    /**
     * 类型
     */
    private Integer type;

}

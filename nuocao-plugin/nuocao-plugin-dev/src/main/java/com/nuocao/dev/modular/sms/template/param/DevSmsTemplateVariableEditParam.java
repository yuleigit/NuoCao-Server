package com.nuocao.dev.modular.sms.template.param;

import com.nuocao.common.validation.InEnum;
import com.nuocao.dev.modular.sms.template.enums.DevSmsVariableTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;

@Getter
@Setter
public class DevSmsTemplateVariableEditParam{
        /**
         * 变量名称（英文）
         */
        @NotBlank(message = "变量名不能为空")
        private String name;

        /**
         * 类型
         */
        @NotBlank(message = "变量类型不能为空")
        @InEnum(value= DevSmsVariableTypeEnum.class)
        private Integer type;

}

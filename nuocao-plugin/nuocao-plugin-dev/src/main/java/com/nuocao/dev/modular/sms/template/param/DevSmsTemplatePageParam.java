package com.nuocao.dev.modular.sms.template.param;

import com.nuocao.common.page.SortPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 字典查询参数
 */
@Getter
@Setter
public class DevSmsTemplatePageParam extends SortPageParam {

    /** 通道编码 */
    @ApiModelProperty(value = "通道编码")
    private String channelCode;

    /** 模板名称 */
    @ApiModelProperty(value = "模板名称")
    private String name;
    /** 模板编码 */
    @ApiModelProperty(value = "模板编码")
    private String code;

}

package com.nuocao.dev.modular.sms.log.param;

import com.nuocao.common.page.SortPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 短信查询参数
 */
@Getter
@Setter
public class DevSmsLogPageParam extends SortPageParam {


    /** 短信引擎 */
    @ApiModelProperty(value = "短信引擎")
    private String channelCode;

    /** 手机号关键词 */
    @ApiModelProperty(value = "手机号关键词")
    private String phone;

    /** 短信发送模板编码 */
    @ApiModelProperty(value = "短信发送模板编码")
    private String templateCode;

    private String content;
}

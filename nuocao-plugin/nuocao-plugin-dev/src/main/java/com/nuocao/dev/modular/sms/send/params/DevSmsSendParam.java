package com.nuocao.dev.modular.sms.send.params;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 短信发送控制器
 */
@Getter
@Setter
public class DevSmsSendParam {

    @ApiModelProperty(value = "接收号码")
    @NotBlank
    private String phoneNumber;

    /**
     * 模板编码
     */
    @ApiModelProperty(value = "模板编码")
    @NotBlank
    private String templateCode;

    /**
     * 模板编码
     */
    @ApiModelProperty(value = "发送参数")
    private LinkedHashMap<String,String> params;


}

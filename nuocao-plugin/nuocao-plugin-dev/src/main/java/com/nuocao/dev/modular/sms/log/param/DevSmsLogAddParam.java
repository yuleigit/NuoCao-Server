package com.nuocao.dev.modular.sms.log.param;

import com.nuocao.common.validation.InEnum;
import com.nuocao.plugin.dev.api.enums.sms.SmsChannelEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import com.nuocao.plugin.dev.api.enums.sms.DevSmsTypeEnum;
import org.noear.solon.validation.annotation.NotBlank;

import java.time.LocalDateTime;

/**
 * 短信日志添加
 */
@Getter
@Setter
public class DevSmsLogAddParam {
    /** 短信通道编码
     * {@link SmsChannelEnum}
     * */
    @ApiModelProperty(value = "短信通道编码", position = 2)
    @NotBlank
    private String channelCode;

    /**
     * 模板编码
     */
    @ApiModelProperty(value = "模板编码", position = 2)
    private String templateCode;

    /**
     * 短信类型
     */
    @ApiModelProperty(value = "短信类型", position = 2)
    @InEnum(DevSmsTypeEnum.class)
    private Integer type;

    /** 手机号
     * {@link DevSmsTypeEnum}
     * */
    @ApiModelProperty(value = "手机号码", position = 3)
    @NotBlank
    private String phone;

    /** 短信发送签名 */
    @ApiModelProperty(value = "短信发送签名", position = 4)
    @NotBlank
    private String signName;

    @ApiModelProperty(value = "短信内容")
    @NotBlank
    private String content;

    /** 发送参数 */
    @ApiModelProperty(value = "发送参数", position = 6)
    private String templateParam;

    /** 接收人ID */
    @ApiModelProperty(value = "接收人ID", position = 6)
    private Long userId;

    /** 接收人账号类型
     * {@link }
     * */
    @ApiModelProperty(value = "接收人账号类型", position = 6)
    private String userType;

    /** 发送时间
     * */
    @ApiModelProperty(value = "发送时间", position = 6)
    private LocalDateTime sendTime;

    /** 发送返回结码 */
    @ApiModelProperty(value = "发送返回结码", position = 7)
    private String apiResultCode;
    /** 发送返回消息 */
    @ApiModelProperty(value = "发送返回消息", position = 7)
    private String apiResultMsg;
    /** 发送请求ID */
    @ApiModelProperty(value = "发送请求ID", position = 7)
    private String apiRequestId;
}

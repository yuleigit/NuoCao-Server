package com.nuocao.dev.modular.message.pojo.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import nuocao.plugin.dev.api.dto.message.DevMessageReceiverInfoDTO;
import nuocao.plugin.dev.api.dto.message.DevMessageUserResultDTO;

import java.util.List;

/**
 * 站内信结果
 */
@Getter
@Setter
public class DevMessageDetailResult {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    private Long id;

    /** 分类 */
    @ApiModelProperty(value = "分类", position = 2)
    private String category;

    /** 主题 */
    @ApiModelProperty(value = "主题", position = 3)
    private String title;

    /** 正文 */
    @ApiModelProperty(value = "正文", position = 4)
    private String content;

    /** 扩展信息 */
    @ApiModelProperty(value = "扩展信息", position = 5)
    private String extJson;
    @ApiModelProperty(value = "发送人Id", position = 5)
    private Long senderUserId;

    @ApiModelProperty(value = "发送", position = 5)
    private DevMessageUserResultDTO sender;

    /** 接收信息集合 */
    @ApiModelProperty(value = "接收信息集合", position = 6)
    private List<DevMessageReceiverInfoDTO> receiverInfoList;

}

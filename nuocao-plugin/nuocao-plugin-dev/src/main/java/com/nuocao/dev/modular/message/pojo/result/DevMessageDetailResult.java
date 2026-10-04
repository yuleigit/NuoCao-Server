package com.nuocao.dev.modular.message.pojo.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

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

    /** 接收信息集合 */
    @ApiModelProperty(value = "接收信息集合", position = 6)
    private List<DevReceiverInfo> receiveInfoList;

    /**
     * 接收信息类
     */
    @Getter
    @Setter
    public static class DevReceiverInfo {

        /** 接收人ID */
        @ApiModelProperty(value = "接收人ID", position = 1)
        private Long receiverUserId;

        /** 接收人姓名 */
        @ApiModelProperty(value = "接收人姓名", position = 2)
        private String receiverUserName;

        /** 是否已读 */
        @ApiModelProperty(value = "是否已读", position = 3)
        private Boolean read;
    }
}

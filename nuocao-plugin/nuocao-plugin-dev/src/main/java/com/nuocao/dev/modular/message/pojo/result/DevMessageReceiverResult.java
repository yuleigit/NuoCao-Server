package com.nuocao.dev.modular.message.pojo.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import nuocao.plugin.dev.api.dto.message.DevMessageUserResultDTO;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 站内信结果
 */
@Getter
@Setter
public class DevMessageReceiverResult {

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

    @ApiModelProperty(value = "是否已读", position = 4)
    private Integer read;
    @ApiModelProperty(value = "是否已读", position = 4)
    private LocalDateTime readTime;

    @ApiModelProperty(value = "发送人Id", position = 5)
    private Long senderUserId;
    @ApiModelProperty(value = "发送", position = 5)
    private DevMessageUserResultDTO sender;

    /** 创建时间 */
    @ApiModelProperty(value = "创建时间", position = 7)
    private Date createTime;

    /** 创建人 */
    @ApiModelProperty(value = "创建人", position = 8)
    private String createUser;

}

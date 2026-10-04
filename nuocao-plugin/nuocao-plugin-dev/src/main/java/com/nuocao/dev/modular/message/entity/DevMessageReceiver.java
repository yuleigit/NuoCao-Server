package com.nuocao.dev.modular.message.entity;


import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 站累心接收用户 实体
 **/
@Getter
@Setter
@Table(value = "dev_message_receiver")
public class DevMessageReceiver {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    @Id
    private Long id;

    /** 消息ID */
    @ApiModelProperty(value = "消息ID", position = 2)
    private Long messageId;

    /** 用户ID */
    @ApiModelProperty(value = "用户ID", position = 3)
    private Long receiverUserId;

    /** 已读状态 */
    @ApiModelProperty(value = "已读状态",notes = "1=是，0=否", position = 4)
    private Integer read;

    @ApiModelProperty(value="读取时间")
    private LocalDateTime readTime ;


}

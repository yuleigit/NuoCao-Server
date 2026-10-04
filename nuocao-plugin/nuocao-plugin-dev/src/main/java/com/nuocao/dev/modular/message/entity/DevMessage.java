package com.nuocao.dev.modular.message.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.nuocao.common.pojo.CommonEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 站内信实体
 **/
@Getter
@Setter
@Table(value = "dev_message")
public class DevMessage extends CommonEntity {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    @Id
    private Long id;

    /** 分类 */
    @ApiModelProperty(value = "分类", position = 2)
    private String category;

    /** 标题 */
    @ApiModelProperty(value = "标题", position = 3)
    private String title;

    /** 正文 */
    @ApiModelProperty(value = "正文", position = 4)
    private String content;

    @ApiModelProperty("发送人用户ID,展示用的可能并不是实际创建人")
    private Long senderUserId;

}

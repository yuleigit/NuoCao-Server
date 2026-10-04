package com.nuocao.dev.modular.message.pojo.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotEmpty;
import java.util.List;

/**
 * 站内信发送参数
 **/
@Getter
@Setter
public class DevMessageSendParam {

    /** 主题 */
    @ApiModelProperty(value = "主题", required = true, position = 1)
    @NotBlank(message = "subject不能为空")
    private String title;

    /** 接收人id集合 */
    @ApiModelProperty(value = "接收人id集合", required = true, position = 2)
    @NotEmpty(message = "receiverIdList不能为空")
    private List<Long> receiverIdList;

    /** 正文 */
    @ApiModelProperty(value = "正文", position = 3)
    private String content;

    /** 分类 */
    @ApiModelProperty(value = "分类", position = 4, hidden = true)
    @NotBlank(message = "category不能为空")
    private String category;

    /** 正文 */
    @ApiModelProperty(value = "发送人ID，0=系统", position = 3)
    private Long senderUserId;
}

package com.nuocao.plugin.dev.api.dto.message;

import com.nuocao.plugin.dev.api.enums.message.DevMessageCategoryEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DevMessageSendParamDTO {
    @ApiModelProperty(value = "目录",required = true)
    @Builder.Default
    private DevMessageCategoryEnum category=DevMessageCategoryEnum.SYS;

    @ApiModelProperty(value = "标题",required = true)
    private String title;

    @ApiModelProperty(value = "内容",notes = "可以为空")
    private String content;

    @ApiModelProperty(value = "发送人用户ID",required = true,notes = "传入0为系统用户")
    @Builder.Default
    private Long senderUserId = 0L;

    @ApiModelProperty("接收用户ID集合")
    private List<Long> receiverIdList;
}

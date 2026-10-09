package com.nuocao.plugin.dev.api.dto.message;


import com.nuocao.common.page.SortPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DevMessagePageParamDTO extends SortPageParam {

    @ApiModelProperty(value = "目录")
    private String category;

    @ApiModelProperty(value = "接收用户ID",required = true)
    private Long receiverUserId;

    @ApiModelProperty("检索关键词")
    private String keywords;

    @ApiModelProperty("发送人用户ID")
    private Long senderUserId;

    @ApiModelProperty("读取状态")
    private Integer read;
}

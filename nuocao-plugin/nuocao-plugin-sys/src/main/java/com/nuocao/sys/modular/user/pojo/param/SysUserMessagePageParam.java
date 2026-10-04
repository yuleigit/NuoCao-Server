package com.nuocao.sys.modular.user.pojo.param;

import com.nuocao.common.page.SortPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SysUserMessagePageParam extends SortPageParam {

    @ApiModelProperty(value = "目录",required = true)
    private String category;

    @ApiModelProperty("检索关键词")
    private String keywords;

    @ApiModelProperty("发送人用户ID")
    private Long senderUserId;

    @ApiModelProperty("读取状态")
    private Integer read;
}

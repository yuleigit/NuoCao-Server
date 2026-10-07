package com.nuocao.dev.modular.message.pojo.param;

import com.nuocao.common.page.SortPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 站内信查询参数
 **/
@Getter
@Setter
public class DevMessagePageParam extends SortPageParam {


    /** 站内信分类 */
    @ApiModelProperty(value = "站内信分类")
    private String category;

    /** 主题关键词 */
    @ApiModelProperty(value = "搜索关键词")
    private String searchKey;
    /** 发送人ID */
    @ApiModelProperty(value = "发送人ID")
    private Long senderUserId;
    /** 接收用户id */
    @ApiModelProperty(value = "接收用户id")
    private Long receiverUserId;

    /** 接收用户id */
    @ApiModelProperty(value = "接收用户id")
    private List<Long> receiverUserIds;
}

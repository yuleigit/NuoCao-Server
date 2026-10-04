package com.nuocao.dev.modular.message.pojo.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 站内信列表参数
 **/
@Getter
@Setter
public class DevMessageListParam {

    /** 接收人id集合 */
    @ApiModelProperty(value = "接收人id集合")
    private Long receiverId;

    /** 条数" */
    @ApiModelProperty(value = "条数")
    private Integer limit;

    /** 条数" */
    @ApiModelProperty(value = "条数")
    private Integer read;
}

package com.nuocao.dev.modular.log.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 操作日志柱状图数据结果
 */
@Getter
@Setter
public class DevLogOpBarChartDataResult {

    /** 日期 */
    @ApiModelProperty(value = "日期", position = 1)
    private String date;

    /** 标题 */
    @ApiModelProperty(value = "标题", position = 2)
    private String title;

    /** 数量 */
    @ApiModelProperty(value = "数量", position = 3)
    private Long count;
}

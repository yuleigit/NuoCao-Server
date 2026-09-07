package com.nuocao.dev.modular.log.param;

import com.nuocao.common.page.SortPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 日志查询参数
 */
@Getter
@Setter
public class DevLogPageParam extends SortPageParam {

    /** 日志分类 */
    @ApiModelProperty(value = "日志分类")
    private String category;

    /** 日志名称关键词 */
    @ApiModelProperty(value = "日志名称关键词")
    private String title;


    private Long userId;

    private String username;


    private List<String> categorys;

}

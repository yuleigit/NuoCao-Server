package com.nuocao.dev.modular.dict.param;

import com.nuocao.common.page.SortPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 字典查询参数
 */
@Getter
@Setter
public class DevDictPageParam extends SortPageParam {

    /** 父id */
    @ApiModelProperty(value = "父id")
    private Long parentId;

    /** 字典分类 */
    @ApiModelProperty(value = "字典分类")
    private String category;
    /** 搜索关键词 */
    @ApiModelProperty(value = "搜索关键词")
    private String searchKey;

}

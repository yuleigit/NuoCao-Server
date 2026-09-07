package com.nuocao.gen.modular.table.param;

import com.nuocao.common.page.SortPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 代码生成表查询参数
 */
@Getter
@Setter
public class GenTablePageParam extends SortPageParam {

    /** 主表名称 */
    @ApiModelProperty(value = "主表名称", required = true, position = 1)
    private String tableName;

}

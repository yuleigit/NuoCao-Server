package com.nuocao.sys.modular.role.pojo.param;

import com.nuocao.common.page.SortPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 角色查询参数
 **/
@Getter
@Setter
public class SysRolePageParam extends SortPageParam {




    /** 组织id */
    @ApiModelProperty(value = "组织id")
    private String id;

    /** 职位分类 */
    @ApiModelProperty(value = "职位分类")
    private String category;

    /** 名称关键词 */
    @ApiModelProperty(value = "名称关键词")
    private String searchKey;
}

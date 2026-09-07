package com.nuocao.sys.modular.org.pojo.param;

import com.nuocao.common.page.SortPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 组织查询参数
 **/
@Getter
@Setter
public class SysOrgPageParam extends SortPageParam {

    /** 父id */
    @ApiModelProperty(value = "父id")
    private Long parentId;

    /** 名称关键词 */
    @ApiModelProperty(value = "名称关键词")
    private String orgName;
}

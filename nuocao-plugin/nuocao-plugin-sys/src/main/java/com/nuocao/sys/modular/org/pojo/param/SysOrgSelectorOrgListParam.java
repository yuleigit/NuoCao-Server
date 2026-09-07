package com.nuocao.sys.modular.org.pojo.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 组织列表选择器参数

 **/
@Getter
@Setter
public class SysOrgSelectorOrgListParam  {

    /** 父id */
    @ApiModelProperty(value = "父id")
    private Long parentId;

}

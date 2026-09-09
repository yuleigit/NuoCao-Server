package com.nuocao.sys.modular.menu.pojo.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 菜单树查询参数
 **/
@Getter
@Setter
public class SysMenuTreeParam {

    /** 顶级菜单ID */
    @ApiModelProperty(value = "顶级菜单ID")
    private Long topId;

    @ApiModelProperty(value = "菜单Id集合",hidden = true)
    private List<Long> menuIds;

}

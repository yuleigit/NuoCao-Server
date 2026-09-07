package com.nuocao.sys.modular.menu.pojo.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 菜单树查询参数
 **/
@Getter
@Setter
public class SysMenuTreeParam {

    /** 顶级菜单ID */
    @ApiModelProperty(value = "顶级菜单ID")
    private Long topId;


}

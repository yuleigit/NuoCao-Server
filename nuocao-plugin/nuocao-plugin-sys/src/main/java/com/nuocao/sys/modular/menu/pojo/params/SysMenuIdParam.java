package com.nuocao.sys.modular.menu.pojo.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import org.noear.solon.validation.annotation.NotNull;

/**
 * 菜单Id参数
 **/
@Getter
@Setter
public class SysMenuIdParam {

    /** menuId */
    @ApiModelProperty(value = "菜单ID", required = true)
    @NotNull
    private Long id;
}

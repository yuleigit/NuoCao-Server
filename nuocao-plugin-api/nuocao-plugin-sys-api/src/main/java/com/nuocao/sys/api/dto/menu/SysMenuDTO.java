package com.nuocao.sys.api.dto.menu;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 租户套餐 菜单列表 对象
 */
@Getter
@Setter
public class SysMenuDTO {

    /** 菜单ID
     * */
    @ApiModelProperty(value = "菜单ID", position = 8)
    private Long id;

    /** 菜单类型
     * */
    @ApiModelProperty(value = "菜单类型", position = 8)
    private String type;

    /** 标题 */
    @ApiModelProperty(value = "标题", position = 3)
    private String title;


    /** 编码不能重复且只有菜单、按钮及才可用 */
    @ApiModelProperty(value = "编码不能重复且只有菜单、按钮及才可用", position = 4)
    private String code;
}

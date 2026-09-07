package com.nuocao.sys.modular.role.pojo.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 角色拥有的权限结果
 **/
@Getter
@Setter
public class SysRolePermissionResult {

    /** 角色id */
    @ApiModelProperty(value = "角色id", position = 1)
    private Long id;

    /** 角色名 */
    @ApiModelProperty(value = "角色名", position = 1)
    private String name;

    /** 角色编码 */
    @ApiModelProperty(value = "角色编码", position = 1)
    private String code;

    /** 已授权的所有ID集合 */
    @ApiModelProperty(value = "已授权的所有ID集合", position = 2)
    private List<Long> menuIds;


}

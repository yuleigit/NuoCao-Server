package com.nuocao.sys.modular.role.pojo.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import org.noear.solon.validation.annotation.Validated;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

import java.util.Set;

/**
 * 角色授权权限参数
 **/
@Getter
@Setter
public class SysRoleSetPermissionParam {

    /** 角色id */
    @ApiModelProperty(value = "角色id", required = true, position = 1)
    @NotNull
    private Long id;

    /** 授权功能菜单ID集合 */
    @Validated
    @ApiModelProperty(value = "授权功能菜单ID集合", required = true, position = 2)
    @NotNull
    private Set<Long> menuIds;
}

package com.nuocao.sys.modular.role.pojo.result;

import com.nuocao.sys.modular.role.pojo.SysRoleBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 角色拥有的权限结果
 **/
@Getter
@Setter
public class SysRoleResult extends SysRoleBase {

    /** 角色id */
    @ApiModelProperty(value = "角色id", position = 1)
    private Long id;


}

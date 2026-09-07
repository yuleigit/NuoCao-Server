package com.nuocao.sys.modular.user.pojo.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 用户角色身份名称与id的列表返回实例，用于返回
 */
@Getter
@Setter
public class SysUserIdentityRoleResult {
    /** id */
    @ApiModelProperty(value = "角色ID", position = 1)
    private Long id;

    /** 姓名 */
    @ApiModelProperty(value = "角色名", position = 7)
    private String name;

    /** 昵称 */
    @ApiModelProperty(value = "角色编码", position = 8)
    private String code;

}

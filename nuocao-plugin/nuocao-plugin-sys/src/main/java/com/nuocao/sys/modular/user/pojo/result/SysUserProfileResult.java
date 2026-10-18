
package com.nuocao.sys.modular.user.pojo.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户结果集
 **/
@Getter
@Setter
public class SysUserProfileResult extends SysUserResult {

    @ApiModelProperty("角色名称集合")
    private String roleNames;

    @ApiModelProperty("所属组织名称集合")
    private String orgNames;




}

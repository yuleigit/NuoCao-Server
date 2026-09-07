package com.nuocao.sys.modular.permission.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * 数据权限编辑参数
 **/
@Getter
@Setter
public class SysUserPermissionViewResult {

    /** ID */
    @ApiModelProperty(value = "ID", required = true, position = 1)
    private Long id;

    /** 权限类型ORG=组织权限 */
    @ApiModelProperty(value = "权限类型ORG=组织权限", required = true, position = 2)
    private String category;

    /** 目录类型USER，ROLE */
    @ApiModelProperty(value = "目录类型USER，ROLE", required = true, position = 3)
    private String objectType;

    /** 对象ID，比如Userid，或者ROLEID */
    @ApiModelProperty(value = "对象ID，比如Userid，或者ROLEID", required = true, position = 4)
    private Long objectId;

    /** 权限范围ALL,ONLY,CHILD,CUSTOM,SELF */
    @ApiModelProperty(value = "权限范围ALL,ONLY,CHILD,CUSTOM,SELF", required = true, position = 5)
    private String spaceType;

    /** 权限ID集合 */
    @ApiModelProperty(value = "权限ID集合", required = true, position = 6)
    private Set<Long> ids;

}

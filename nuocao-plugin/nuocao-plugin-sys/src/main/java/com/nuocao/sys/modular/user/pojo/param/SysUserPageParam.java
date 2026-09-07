package com.nuocao.sys.modular.user.pojo.param;

import com.nuocao.common.page.SortPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户查询参数
 **/
@Getter
@Setter
public class SysUserPageParam extends SortPageParam {

    /** 账号 */
    @ApiModelProperty(value = "账号")
    private String username;

    /** 昵称/姓名 */
    @ApiModelProperty(value = "昵称/姓名")
    private String searchKey;

    /** 用户状态 */
    @ApiModelProperty(value = "用户状态")
    private String status;

    /** 所属组织 */
    @ApiModelProperty(value = "所属组织")
    private Long orgId;

    /** phone */
    @ApiModelProperty(value = "phone")
    private String phone;
    /** email */
    @ApiModelProperty(value = "email")
    private String email;

    /** 所属角色ID */
    @ApiModelProperty(value = "所属角色ID")
    private Long roleId;
}

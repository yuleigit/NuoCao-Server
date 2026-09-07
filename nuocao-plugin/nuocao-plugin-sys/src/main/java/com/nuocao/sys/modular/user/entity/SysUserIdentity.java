package com.nuocao.sys.modular.user.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户身份实体
 **/
@Getter
@Setter
@Table(value = "sys_user_identity")
public class SysUserIdentity {

    /**
     * id
     */
    @ApiModelProperty(value = "userId", position = 1)
    @Id
    private Long id;

    /**
     * 身份类型
     * {@link com.nuocao.sys.api.enums.SysUserIdentityTypeEnum}
     */
    @ApiModelProperty(value = "身份类型", position = 1)
    private Integer type;


    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID", position = 2)
    private Long userId;
    /**
     * 对应的身份ID，如roleId,positionId;
     */
    @ApiModelProperty(value = "角色ID", position = 3)
    private Long objectId;

    @ApiModelProperty(value="租户ID", position = 4)
    @Column(tenantId = true)
    private Long tenantId;

}

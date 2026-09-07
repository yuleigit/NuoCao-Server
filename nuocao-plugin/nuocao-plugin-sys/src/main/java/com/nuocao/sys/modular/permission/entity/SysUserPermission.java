package com.nuocao.sys.modular.permission.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import com.nuocao.common.pojo.CommonEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * 数据权限实体
 **/
@Getter
@Setter
@Table(value = "sys_user_permission")
public class SysUserPermission extends CommonEntity {

    /** ID */
    @Id
    @ApiModelProperty(value = "ID", position = 1)
    private Long id;



    /** 目录类型USER，ROLE */
    @ApiModelProperty(value = "目录类型user，role", position = 3)
    private String objectType;



    /** 对象ID，比如Userid，或者ROLEID */
    @ApiModelProperty(value = "对象ID，比如Userid，或者ROLEID", position = 4)
    private Long objectId;


    /** 权限菜单ID集合 */
    @ApiModelProperty(value = "权限菜单ID集合", position = 6)
    @Column(typeHandler = Fastjson2TypeHandler.class)
    private Set<Long> menuIds;


    /** 租户 */
    @ApiModelProperty(value = "租户", position = 7)
    @Column(tenantId=true)
    private Long tenantId;




















}

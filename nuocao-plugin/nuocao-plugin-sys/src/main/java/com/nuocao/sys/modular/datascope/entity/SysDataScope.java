package com.nuocao.sys.modular.datascope.entity;

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
@Table(value = "sys_data_scope")
public class SysDataScope extends CommonEntity {


    @Id


    /** ID */
    @ApiModelProperty(value = "ID", position = 1)
    private Long id;




    /** 权限类型ORG=组织权限 */
    @ApiModelProperty(value = "权限类型ORG=组织权限", position = 2)
    private String category;




    /** 目录类型USER，ROLE */
    @ApiModelProperty(value = "目录类型USER，ROLE", position = 3)
    private String objectType;




    /** 对象ID，比如Userid，或者ROLEID */
    @ApiModelProperty(value = "对象ID，比如Userid，或者ROLEID", position = 4)
    private Long objectId;




    /** 权限范围ALL,ONLY,CHILD,CUSTOM,SELF */
    @ApiModelProperty(value = "权限范围ALL,ONLY,CHILD,CUSTOM,SELF", position = 5)
    private String scopeType;




    /** 权限ID集合 */
    @ApiModelProperty(value = "权限ID集合", position = 6)
    @Column(typeHandler = Fastjson2TypeHandler.class)
    private Set<Long> ids;




    /** 租户 */
    @ApiModelProperty(value = "租户", position = 7)
    @Column(tenantId=true)
    private Long tenantId;




















}

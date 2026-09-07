package com.nuocao.sys.modular.user.pojo.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nuocao.sys.api.utils.SysOrgApiUtils;
import com.nuocao.sys.api.utils.SysTenantApiUtils;
import com.nuocao.sys.api.utils.SysUserApiUtils;
import com.nuocao.sys.modular.user.pojo.SysUserBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 用户结果集
 **/
@Data
public class SysUserPageResult extends SysUserBase {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    private Long id;
    /** 头像 */
    @ApiModelProperty(value = "用户名", position = 3)
    private String username;
    /** 头像 */
    @ApiModelProperty(value = "头像", position = 3)
    private String avatar;

    /** 职位id */
    @ApiModelProperty(value = "职位id", position = 34)
    private String positionId;

    /** 上次登录ip */
    @ApiModelProperty(value = "上次登录ip", position = 37)
    private String lastLoginIp;

    /** 上次登录地点 */
    @ApiModelProperty(value = "上次登录地点", position = 38)
    private String lastLoginAddress;

    /** 上次登录时间 */
    @ApiModelProperty(value = "上次登录时间", position = 39)
    private Date lastLoginTime;

    /** 上次登录设备 */
    @ApiModelProperty(value = "上次登录设备", position = 40)
    private String lastLoginDevice;

    /** 最新登录ip */
    @ApiModelProperty(value = "最新登录ip", position = 41)
    private String latestLoginIp;

    /** 最新登录地点 */
    @ApiModelProperty(value = "最新登录地点", position = 42)
    private String latestLoginAddress;

    /** 最新登录时间 */
    @ApiModelProperty(value = "最新登录时间", position = 43)
    private Date latestLoginTime;

    /** 最新登录设备 */
    @ApiModelProperty(value = "最新登录设备", position = 44)
    private String latestLoginDevice;

    /** 用户状态 */
    @ApiModelProperty(value = "用户状态", position = 45)
    private Integer status;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", position = 46)
    private Integer sortCode;


    /** 所属组织 */
    @ApiModelProperty(value = "所属组织", position = 46)
    private String orgName;


    /** 所属角色集合 */
    @ApiModelProperty(value = "所属角色集合", position = 46)
    private List<String> roleNames;


    /** 是否为租户管理员 */
    @ApiModelProperty(value = "所属角色集合", position = 46)
    private Boolean isTenantAdmin;

    /**租户ID */
    @JsonIgnore
    private Long tenantId;

}

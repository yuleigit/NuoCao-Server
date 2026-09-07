package com.nuocao.sys.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysTenantDTO {

    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID", position = 1)
    private Long id;

    /**
     * 租户所属组织
     */
    @ApiModelProperty(value = "组织ID", position = 1)
    private Long orgId;

    /** 租户名称 */
    @ApiModelProperty(value = "租户名称", position = 2)
    private String tenantName;

    /** 套餐ID */
    @ApiModelProperty(value = "套餐ID", position = 3)
    private Long packageId;

    /** 数据规则形式 */
    @ApiModelProperty(value = "数据规则形式", position = 4)
    private String dataRule;

    @ApiModelProperty(value = "租户编码;不重复，数字字母组合、主要生成数据库用", position = 5)
    private String tenantCode;

    /** 联系人 */
    @ApiModelProperty(value = "联系人", position = 6)
    private String contactUser;

    /** 联系电话 */
    @ApiModelProperty(value = "联系电话", position = 7)
    private String contactPhone;

    /** 地址 */
    @ApiModelProperty(value = "地址", position = 8)
    private String contactAddress;

    /** 系统名称 */
    @ApiModelProperty(value = "系统名称", position = 9)
    private String sysName;


    /** 到期时间 */
    @ApiModelProperty(value = "到期时间", position = 11)
    private LocalDateTime expireTime;

    /** 可用状态 */
    @ApiModelProperty(value = "可用状态", position = 12)
    private String enableStatus;

    /** 租户管理用户ID */
    @ApiModelProperty(value = "租户管理用户ID", position = 10)
    private Long tenantAdminId;

}

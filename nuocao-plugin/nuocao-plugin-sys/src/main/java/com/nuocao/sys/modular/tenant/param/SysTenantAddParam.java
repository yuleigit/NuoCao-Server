package com.nuocao.sys.modular.tenant.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

import java.time.LocalDateTime;

/**
 * 客户机构添加参数
 *
 * @author Lei
 * @date  2025/06/20 14:26
 **/
@Getter
@Setter
public class SysTenantAddParam {

    /** 租户名称 */
    @ApiModelProperty(value = "租户名称", position = 2)
    private String tenantName;

    /** 组织分组ID */
    @ApiModelProperty(value = "组织分组ID", required = true, position = 3)
    @NotNull(message = "组织分组不能为空")
    private Long orgId;

    /** 套餐ID */
    @ApiModelProperty(value = "套餐ID", required = true, position = 3)
    @NotNull(message = "套餐不能为空")
    private Long packageId;

    /** 数据规则形式 */
    @ApiModelProperty(value = "数据规则形式", position = 4)
    private String dataRule;

    /** 租户编码;不重复，数字字母组合、主要生成数据库用 */
    @ApiModelProperty(value = "租户编码;不重复，数字字母组合、主要生成数据库用", required = true, position = 5)
    @NotBlank(message = "tenantCode不能为空")
    private String tenantCode;

    /** 联系人 */
    @ApiModelProperty(value = "联系人", required = true, position = 6)
    @NotBlank(message = "contactName不能为空")
    private String contactName;

    /** 联系电话 */
    @ApiModelProperty(value = "联系电话", required = true, position = 7)
    @NotBlank(message = "contactsPhone不能为空")
    private String contactTel;

    /** 地址 */
    @ApiModelProperty(value = "地址", position = 8)
    private String contactAddress;

    /** 系统名称 */
    @ApiModelProperty(value = "系统名称", required = true, position = 9)
    @NotBlank(message = "sysName不能为空")
    private String sysName;

    /** 租户管理用户ID */
    @ApiModelProperty(value = "租户管理用户ID", required = true, position = 10)
    @NotNull(message = "tenantAdminId不能为空")
    private Long tenantAdminId;

    /** 租户管理员用户名 */
    @ApiModelProperty(value = "租户管理员用户名", required = true, position = 10)
    @NotNull(message = "管理员用户名不能为空")
    private String adminAccount;

    /** 租户管理员用户名 */
//    @ApiModelProperty(value = "管理员密码", required = true, position = 10)
//    @NotNull(message = "管理员密码不能为空")
//    private String adminPassword;

    /** 到期时间 */
    @ApiModelProperty(value = "到期时间", required = true, position = 11)
    @NotNull(message = "expireTime不能为空")
    private LocalDateTime expireTime;

    /** 可用状态 */
    @ApiModelProperty(value = "可用状态", required = true, position = 12)
    @NotBlank(message = "enableStatus不能为空")
    private String enableStatus;

}

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
public class SysTenantAddParam extends SysTenantBaseParam {
    /** 租户管理用户ID */
    @ApiModelProperty(value = "租户管理用户ID", required = true, position = 10)
    @NotNull(message = "tenantAdminId不能为空")
    private Long tenantAdminId;

    /** 数据规则形式 */
    @ApiModelProperty(value = "数据规则形式", position = 4)
    private String dataRule;

    /** 租户编码;不重复，数字字母组合、主要生成数据库用 */
    @ApiModelProperty(value = "租户编码;不重复，数字字母组合、主要生成数据库用", required = true, position = 5)
    @NotBlank(message = "tenantCode不能为空")
    private String tenantCode;

    /** 租户管理员用户名 */
    @ApiModelProperty(value = "租户管理员用户名", required = true, position = 10)
    @NotNull(message = "管理员用户名不能为空")
    private String adminAccount;


}

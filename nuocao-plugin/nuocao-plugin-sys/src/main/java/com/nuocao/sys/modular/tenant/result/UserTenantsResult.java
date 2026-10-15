package com.nuocao.sys.modular.tenant.result;

import com.nuocao.sys.api.dto.SysTenantDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserTenantsResult {

    /**
     * 当前租户
     */
    @ApiModelProperty("当前租户")
    SysTenantDTO current;

    /**
     * 租户列表
     */
    @ApiModelProperty("我有的所有租户")
    List<UserTenantResult> tenants;


    @Data
    public static class UserTenantResult{
        /**
         * 租户ID
         */
        @ApiModelProperty(value = "租户ID", position = 1)
        private Long id;

        @ApiModelProperty(value = "租户名称", position = 2)
        private String tenantName;

        @ApiModelProperty(value = "租户编码;不重复，数字字母组合、主要生成数据库用", position = 3)
        private String tenantCode;

        /** 系统名称 */
        @ApiModelProperty(value = "系统名称", position = 4)
        private String sysName;

        /** 可用状态 */
        @ApiModelProperty(value = "可用状态", position = 5)
        private String enableStatus;

    }

}

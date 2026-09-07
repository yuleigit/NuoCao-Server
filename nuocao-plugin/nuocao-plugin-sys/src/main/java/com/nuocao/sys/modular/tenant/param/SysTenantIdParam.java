package com.nuocao.sys.modular.tenant.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import org.noear.solon.validation.annotation.NotNull;

/**
 * 客户机构Id参数
 **/
@Getter
@Setter
public class SysTenantIdParam {

    /** 租户ID */
    @ApiModelProperty(value = "租户ID", required = true)
    @NotNull(message = "租户ID不能为空")
    private Long id;
}

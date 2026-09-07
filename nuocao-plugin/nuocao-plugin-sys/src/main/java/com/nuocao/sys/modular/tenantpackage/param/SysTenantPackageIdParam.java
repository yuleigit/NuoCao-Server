package com.nuocao.sys.modular.tenantpackage.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import org.noear.solon.validation.annotation.NotNull;

/**
 * 租户套餐Id参数
 **/
@Getter
@Setter
public class SysTenantPackageIdParam {

    /** 租户套餐ID */
    @ApiModelProperty(value = "租户套餐ID", required = true)
    @NotNull(message = "planId不能为空")
    private Long id;
}

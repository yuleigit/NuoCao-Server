package com.nuocao.sys.modular.tenantpackage.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import org.noear.solon.validation.annotation.Length;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

import java.util.List;

/**
 * 租户套餐编辑参数
 *
 * @author Lei
 * @date  2025/06/19 16:30
 **/
@Getter
@Setter
public class SysTenantPackageEditParam extends SysTenantPackageParamBase {

    /** 租户套餐ID */
    @ApiModelProperty(value = "租户套餐ID", required = true, position = 1)
    @NotNull(message = "planId不能为空")
    private Long id;

}

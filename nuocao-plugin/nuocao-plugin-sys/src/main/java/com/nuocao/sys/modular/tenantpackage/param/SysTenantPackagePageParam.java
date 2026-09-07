package com.nuocao.sys.modular.tenantpackage.param;

import com.nuocao.common.page.SortPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 租户套餐查询参数
 *
 * @author Lei
 * @date  2025/06/19 16:30
 **/
@Getter
@Setter
public class SysTenantPackagePageParam extends SortPageParam {


    /** 套餐名称 */
    @ApiModelProperty(value = "套餐名称")
    private String name;

}

package com.nuocao.sys.modular.tenantpackage.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.Length;
import org.noear.solon.validation.annotation.NotBlank;

import java.util.List;

@Getter
@Setter
public class SysTenantPackageParamBase  {
    /** 套餐名称 */
    @ApiModelProperty(value = "套餐名称", required = true, position = 2)
    @NotBlank(message = "planName不能为空")
    @Length(max = 50)
    private String name;

    /** 备注说明 */
    @ApiModelProperty(value = "备注说明", position = 3)
    private String remark;



    /** 备注说明 */
    @ApiModelProperty(value = "启用状态", position = 3)
    private Integer enableStatus;


    /** 被授权的菜单ID集合 */
    @ApiModelProperty(value = "被授权的菜单ID集合", position = 3)
    private List<Long> menuIds;
}

package com.nuocao.sys.modular.tenant.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

import java.time.LocalDateTime;

/**
 * 客户机构编辑参数
 **/
@Getter
@Setter
public class SysTenantEditParam  extends SysTenantBaseParam{

    /** 租户ID */
    @ApiModelProperty(value = "租户ID", required = true, position = 1)
    @NotNull(message = "客户不能为空")
    private Long id;


}
